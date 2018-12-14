package com.sonalake.task;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NbpApiResponseResource {
	private List<SingleCurrencyRateResource> rates;

}
