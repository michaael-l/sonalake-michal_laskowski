package com.sonalake.task;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Map;

import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyRatesFetcherTest {

	@InjectMocks
	private CurrencyRatesFetcher fetcher;

	@Mock
	private RestTemplate template;

	@Mock
	private NetPayConfiguration configuration;

	@Test
	public void shouldFetchOnlineRates() throws Exception {
		// given
		when(configuration.getNbpApiUrl()).thenReturn("url");
		when(template.getForObject(anyString(), eq(NbpApiResponseResource[].class)))
				.thenReturn(Arrays.array(NbpApiResponseResource.builder()
						.rates(Lists.newArrayList(SingleCurrencyRateResource.builder().code("PLN").mid(1f).build()))
						.build()));

		// when
		Map<String, SingleCurrencyRateResource> rates = fetcher.fetchLatest();

		// then
		assertThat(rates).isNotEmpty();
		assertThat(rates).hasSize(1);
		verify(configuration, times(0)).getRatesForOfflineModeFileName();
	}
	
	@Test
	public void shouldFetchOfflineRates() throws Exception {
		// given
		when(configuration.getNbpApiUrl()).thenReturn("url");
		when(template.getForObject(anyString(), eq(NbpApiResponseResource[].class)))
				.thenThrow(new RestClientException(""));
		when(configuration.getRatesForOfflineModeFileName()).thenReturn("nbp-response.json");

		// when
		Map<String, SingleCurrencyRateResource> rates = fetcher.fetchLatest();

		// then
		assertThat(rates).isNotEmpty();
		assertThat(rates).hasSize(1);
		verify(configuration).getRatesForOfflineModeFileName();
	}

}
