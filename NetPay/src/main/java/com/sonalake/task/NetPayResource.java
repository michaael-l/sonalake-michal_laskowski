package com.sonalake.task;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetPayResource {
	private Float netPay;
	private String countryCode;

}
