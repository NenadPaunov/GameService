package org.nenad.paunov.config.web;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RestTemplateContext {
	@Bean
	public RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		PlatformRequestLoggingInterceptor interceptor = new PlatformRequestLoggingInterceptor();
		RestTemplateErrorHandler errorHandler = new RestTemplateErrorHandler();
		return builder
				.requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
				.setReadTimeout(Duration.ofSeconds(30))
				.setConnectTimeout(Duration.ofSeconds(30))
				.interceptors(interceptor)
				.errorHandler(errorHandler)
				.messageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter()))
				.build();
	}
}
