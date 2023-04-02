package org.nenad.paunov.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ExternalServiceException extends RuntimeException {

	private final HttpStatusCode httpStatus;

	public ExternalServiceException(String msg) {
		this(msg, null);
	}

	public ExternalServiceException(String msg, HttpStatusCode httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}
}
