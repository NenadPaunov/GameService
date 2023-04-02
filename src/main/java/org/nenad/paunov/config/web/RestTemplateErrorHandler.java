package org.nenad.paunov.config.web;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.nenad.paunov.exception.ExternalServiceException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Setter
public class RestTemplateErrorHandler implements ResponseErrorHandler {

	private String serviceName;

	@Override
	public boolean hasError(ClientHttpResponse httpResponse)
			throws IOException {

		return (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError());
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		String body = IOUtils.toString(httpResponse.getBody(), StandardCharsets.UTF_8);
		throw new ExternalServiceException("Exception occurred while calling " + serviceName + ": " + body, httpResponse.getStatusCode());
	}
}
