package com.sonalake.task;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetPayResource {
	private BigDecimal netPay;
	private String countryCode;
	private String currencyCode;

}
