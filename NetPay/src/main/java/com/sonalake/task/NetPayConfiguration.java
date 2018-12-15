package com.sonalake.task;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties
@EnableConfigurationProperties
@Data
public class NetPayConfiguration {

	private String nbpApiUrl;
	private String ratesForOfflineModeFileName;
	private List<String> nbpApiKnownCurrencies;
	private Map<String, Country> countries;

	@Data
	public static class Country {

		private String countryCode;
		private String currencyCode;
		private float fixedCostAmount;
		private float taxRate;
	}

}
