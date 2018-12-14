package com.sonalake.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleCurrencyRateResource {
	public SingleCurrencyRateResource(String code, float mid) {
		super();
		this.code = code;
		this.mid = mid;
	}
	private String code;
	private float mid;
}
