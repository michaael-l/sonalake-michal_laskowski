package com.sonalake.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetPayResource {
	private Float netPay;
	private String countryCode;
	private String currencyCode;

}
