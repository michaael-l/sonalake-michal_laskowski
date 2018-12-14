package com.sonalake.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class NetPayController {

	@Autowired
	private CurrencyRatesFetcher currencyRatesFetcher;

	@Autowired
	private NetPayConfiguration configuration;

	@GetMapping("/getPay/{grossAmount}")
	List<NetPayResource> getNetPay(@PathVariable int grossAmount) {

		List<NetPayResource> result = new ArrayList<>();

		Map<String, SingleCurrencyRateResource> currencies = currencyRatesFetcher.fetchLatest();
		currencies.put("PLN", SingleCurrencyRateResource.builder().code("PLN").mid(1).build());

		configuration.getCountries().entrySet().stream().forEach(country -> {
			result.add(NetPayResource.builder().countryCode(country.getValue().getCurrencyCode())
					.netPay(calculateNetAmount(
							grossAmount * currencies.get(country.getValue().getCurrencyCode()).getMid(),
							country.getValue().getTaxRate(), country.getValue().getFixedCostAmount()))
					.build());
		});


		return result;
	}

	private Float calculateNetAmount(float grossAmount, float taxRate, float fixedCost) {
		return grossAmount * (1f - taxRate) - fixedCost;

	}

}
