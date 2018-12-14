package com.sonalake.task;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CurrencyRatesFetcher {

	@Autowired
	private NetPayConfiguration configuration;

	private RestTemplate restTemplate = new RestTemplate();

	public Map<String, SingleCurrencyRateResource> fetchLatest() {

		return Stream.of(restTemplate.getForObject(configuration.getNbpApiUrl(), NbpApiResponseResource[].class))
				.flatMap(rate -> rate.getRates().stream()).filter(Objects::nonNull)
				.filter(rate -> configuration.getNbpApiKnownCurrencies().contains(rate.getCode()))
				.map(currency -> SingleCurrencyRateResource.builder().code(currency.getCode()).mid(currency.getMid())
						.build())
				.collect(Collectors.toMap(SingleCurrencyRateResource::getCode, Function.identity()));
	}

}
