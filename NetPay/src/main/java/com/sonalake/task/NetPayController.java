package com.sonalake.task;

import java.math.BigDecimal;
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

	public static final BigDecimal NUM_OF_DAY_IN_MONTHS = new BigDecimal(22);

	@CrossOrigin
	@RequestMapping(value = "/getPay", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	List<NetPayResource> getNetPay(@RequestBody List<NetPayResource> request) {

		Map<String, SingleCurrencyRateResource> currencies = currencyRatesFetcher.fetchLatest();
		return processCountriesAndRates(request, currencies);

	}

	private List<NetPayResource> processCountriesAndRates(List<NetPayResource> request,
			Map<String, SingleCurrencyRateResource> currencies) {

		request.stream()
				.filter(country -> isNetPayValid(country))
				.forEach(country -> country.setNetPay(
						calculateNetAmount(
								country.getNetPay(), 
								currencies.get(country.getCurrencyCode()).getMid(),
								configuration.getCountries().get(country.getCountryCode()).getTaxRate(),
								configuration.getCountries().get(country.getCountryCode()).getFixedCostAmount())));
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
	private BigDecimal calculateNetAmount(BigDecimal grossAmount, float exchangeRate, float taxRate, float fixedCost) {
		BigDecimal monthlyGrossSalary = grossAmount.multiply(NUM_OF_DAY_IN_MONTHS);
		BigDecimal monthlyNetSalary = monthlyGrossSalary.multiply(new BigDecimal(1f - taxRate));
		BigDecimal monthlyNetIncome = monthlyNetSalary.subtract(new BigDecimal(fixedCost));
		return monthlyNetIncome.multiply(new BigDecimal(exchangeRate));
	}

	private boolean isNetPayValid(NetPayResource country) {
		return Objects.nonNull(country.getCountryCode()) && Objects.nonNull(country.getCurrencyCode())
				&& Objects.nonNull(country.getNetPay()) && country.getNetPay().compareTo(BigDecimal.ZERO) > 0;
	}

}
