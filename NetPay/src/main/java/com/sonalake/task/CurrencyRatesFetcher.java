package com.sonalake.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CurrencyRatesFetcher {

	@Autowired
	private NetPayConfiguration configuration;

	private RestTemplate restTemplate = new RestTemplate();

	@Cacheable("rates")
	public Map<String, SingleCurrencyRateResource> fetchLatest() {

		NbpApiResponseResource[] onlineRates = restTemplate.getForObject(configuration.getNbpApiUrl(),
				NbpApiResponseResource[].class);

		Map<String, SingleCurrencyRateResource> result = Stream
				.of(Optional.ofNullable(onlineRates).orElse(fetchOfflineRates()))
				.flatMap(rate -> rate.getRates().stream()).filter(Objects::nonNull)
				// we want just rates that we have configured
				.filter(rate -> configuration.getNbpApiKnownCurrencies().contains(rate.getCode()))
				.map(currency -> SingleCurrencyRateResource.builder().code(currency.getCode()).mid(currency.getMid())
						.build())
				.collect(Collectors.toMap(SingleCurrencyRateResource::getCode, Function.identity()));
		// add "fake" polish currency rate here to simplify processing
		result.put("PLN", SingleCurrencyRateResource.builder().code("PLN").mid(1).build());
		return result;
	}

	private NbpApiResponseResource[] fetchOfflineRates() {
		NbpApiResponseResource[] offlineRates = null;
		try (InputStream stream = getClass().getClassLoader()
				.getResourceAsStream(configuration.getRatesForOfflineModeFileName())) {
			ObjectMapper mapper = new ObjectMapper();
			offlineRates = mapper.readValue(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)),
					NbpApiResponseResource[].class);
		} catch (IOException ioe) {
			throw new RuntimeException("unable to fetch online or online rates");
		}
		return offlineRates;
	}

}
