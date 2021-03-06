package com.sonalake.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CurrencyRatesFetcher {

	@Autowired
	private NetPayConfiguration configuration;
	
	@Autowired
	private RestTemplate restTemplate;

	public static final String PLN = "PLN";
	

	/**
	 * results of calling this method are cached to reduce time and bandwidth
	 *  
	 * @return {@code Map} with currency rates mapped by currency codes
	 */
	@Cacheable("rates")
	public Map<String, SingleCurrencyRateResource> fetchLatest() {

		NbpApiResponseResource[] rates = null;
		try {
			rates = restTemplate.getForObject(configuration.getNbpApiUrl(), NbpApiResponseResource[].class);
		} catch (RestClientException rce) {
			rates = fetchOfflineRates();
		}

		Map<String, SingleCurrencyRateResource> result = Stream
				.of(rates)
				.flatMap(rate -> rate.getRates().stream()).filter(Objects::nonNull)
				// we want just rates that we have configured
				.filter(rate -> configuration.getNbpApiKnownCurrencies().contains(rate.getCode()))
				.map(currency -> SingleCurrencyRateResource
						.builder().code(currency.getCode())
						.mid(currency.getMid())
						.build())
				.collect(Collectors.toMap(SingleCurrencyRateResource::getCode, Function.identity()));
		// add "fake" polish currency rate here to simplify processing
		result.put(PLN, SingleCurrencyRateResource.builder().code(PLN).mid(1).build());
		return result;
	}

	/**
	 * if online currency rates are unavailable - this method will provide some
	 * static rates from property file
	 * 
	 * @return {@code Map} with currency rates mapped by currency codes
	 */
	private NbpApiResponseResource[] fetchOfflineRates() {
		NbpApiResponseResource[] offlineRates = null;
		try (InputStream stream = getClass().getClassLoader()
				.getResourceAsStream(configuration.getRatesForOfflineModeFileName())) {
			ObjectMapper mapper = new ObjectMapper();
			assert stream != null;
			offlineRates = mapper.readValue(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)),
					NbpApiResponseResource[].class);
		} catch (IOException ioe) {
			throw new RuntimeException("unable to fetch online or offline rates");
		}
		return offlineRates;
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Scheduled(cron = "0 1 1 * * ?")
	@CacheEvict(cacheNames= {"rates"})
	public void clearCache() {
		// empty
	}

}
