package com.sonalake.task;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	@CrossOrigin
	@RequestMapping(value = "/getPay", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	List<NetPayResource> getNetPay(@RequestBody List<NetPayResource> request) {

		Map<String, SingleCurrencyRateResource> currencies = currencyRatesFetcher.fetchLatest();
		return processCountriesAndRates(request, currencies);

	}

	private List<NetPayResource> processCountriesAndRates(List<NetPayResource> request,
			Map<String, SingleCurrencyRateResource> currencies) {

		request.stream().filter(country -> isNetPayValid(country))
				.forEach(country -> country.setNetPay(
						calculateNetAmount(country.getNetPay(), currencies.get(country.getCurrencyCode()).getMid(),
								configuration.getCountries().get(country.getCountryCode()).getTaxRate(), configuration
										.getCountries().get(country.getCountryCode()).getFixedCostAmount())));
		return request;
	}

	/**
	 * 
	 * @param grossAmount
	 * @param exchangeRate
	 * @param taxRate
	 * @param fixedCost
	 * @return the net amount 
	 */
	private Float calculateNetAmount(float grossAmount, float exchangeRate, float taxRate, float fixedCost) {
		float monthlyGrossSalary = NUM_OF_DAY_IN_MONTHS * grossAmount;
		float monthlyNetSalary = monthlyGrossSalary * (1f - taxRate);
		float monthlyNetIncome = monthlyNetSalary - fixedCost;
		return monthlyNetIncome * exchangeRate;
	}

	private boolean isNetPayValid(NetPayResource country) {
		return Objects.nonNull(country.getCountryCode()) && Objects.nonNull(country.getCurrencyCode())
				&& Objects.nonNull(country.getNetPay()) && country.getNetPay() > 0;
	}

}
