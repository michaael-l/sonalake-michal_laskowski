package com.sonalake.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NetPayApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Value("classpath:response.json")
	Resource responseOk;

	@Value("classpath:request.json")
	Resource jsonRequest;

	@InjectMocks
	private CurrencyRatesFetcher fetcher;

	@Mock
	private NetPayConfiguration configuration;

	@Mock
	private RestTemplate template;

	@Test
	public void shouldReturnProperRates() throws Exception {
		
		
		//given
		String response = IOUtils.toString(responseOk.getInputStream(), "UTF-8"); 
		String request = IOUtils.toString(jsonRequest.getInputStream(), "UTF-8"); 
		
		//when&then
		MvcResult mvcResult = mockMvc
				.perform(post("/getPay/").content(request).contentType("application/json"))
				.andExpect(status().isOk()).andExpect(content().json(response, false)).andReturn();
	}

}
