package com.sonalake.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class NetPayController {

	@Autowired
	private CurrencyRatesFetcher currencyRatesFetcher;

	@Autowired
	private NetPayConfiguration configuration;

	public static final int NUM_OF_DAY_IN_MONTHS = 22;

	@RequestMapping(value = "/getPay", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	List<NetPayResource> getNetPay(@RequestBody List<NetPayResource> request) {

		Map<String, SingleCurrencyRateResource> currencies = currencyRatesFetcher.fetchLatest();
		return processCountriesAndRates(request, currencies);

	}

	private List<NetPayResource> processCountriesAndRates(List<NetPayResource> request,
			Map<String, SingleCurrencyRateResource> currencies) {

		request.stream()
				.forEach(country -> country.setNetPay(
						calculateNetAmount(country.getNetPay(), currencies.get(country.getCurrencyCode()).getMid(),
								configuration.getCountries().get(country.getCountryCode()).getTaxRate(),
								configuration.getCountries().get(country.getCountryCode()).getFixedCostAmount())));
		return request;
	}

	private Float calculateNetAmount(float grossAmount, float exchangeRate, float taxRate, float fixedCost) {
		return NUM_OF_DAY_IN_MONTHS * grossAmount * exchangeRate * (1f - taxRate) - fixedCost;
	}
}
