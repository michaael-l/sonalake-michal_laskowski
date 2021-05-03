package com.sonalake.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
class NetPayController {

    private final CurrencyRatesFetcher currencyRatesFetcher;
    private final NetPayConfiguration configuration;

    public static final BigDecimal NUM_OF_DAY_IN_MONTHS = new BigDecimal(22);

    @Autowired
    NetPayController(CurrencyRatesFetcher currencyRatesFetcher, NetPayConfiguration configuration) {
        this.currencyRatesFetcher = currencyRatesFetcher;
        this.configuration = configuration;
    }

    @CrossOrigin
    @PostMapping(value = "/getPay", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public List<NetPayResource> getNetPay(@RequestBody List<NetPayResource> request) {

        Map<String, SingleCurrencyRateResource> currencies = currencyRatesFetcher.fetchLatest();
        return processCountriesAndRates(request, currencies);

    }

    private List<NetPayResource> processCountriesAndRates(List<NetPayResource> request,
                                                          Map<String, SingleCurrencyRateResource> currencies) {

        request.stream()
                .filter(this::isNetPayValid)
                .forEach(country -> country.setNetPay(
                        calculateNetAmount(
                                country.getNetPay(),
                                currencies.get(country.getCurrencyCode()).getMid(),
                                configuration.getCountries().get(country.getCountryCode()).getTaxRate(),
                                configuration.getCountries().get(country.getCountryCode()).getFixedCostAmount())));
        return request;
    }

    /**
     * @param grossAmount  gross
     * @param exchangeRate ex rate
     * @param taxRate      tax rate
     * @param fixedCost    fixed cost
     * @return the net amount
     */
    private BigDecimal calculateNetAmount(BigDecimal grossAmount, float exchangeRate, float taxRate, float fixedCost) {
        BigDecimal monthlyGrossSalary = grossAmount.multiply(NUM_OF_DAY_IN_MONTHS);
        BigDecimal monthlyNetSalary = monthlyGrossSalary.multiply(BigDecimal.valueOf(1f - taxRate));
        BigDecimal monthlyNetIncome = monthlyNetSalary.subtract(BigDecimal.valueOf(fixedCost));
        return monthlyNetIncome.multiply(BigDecimal.valueOf(exchangeRate));
    }

    private boolean isNetPayValid(NetPayResource country) {
        return Objects.nonNull(country.getCountryCode()) && Objects.nonNull(country.getCurrencyCode())
                && Objects.nonNull(country.getNetPay()) && country.getNetPay().compareTo(BigDecimal.ZERO) > 0;
    }

}
