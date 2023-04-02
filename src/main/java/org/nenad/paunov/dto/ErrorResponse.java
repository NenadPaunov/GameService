package org.nenad.paunov.dto;

public record ErrorResponse(ErrorCode code, String message) {

	public enum ErrorCode {
		BAD_REQUEST,
		NOT_FOUND,
		INVALID_STATE,
		INTERNAL_SERVER_ERROR
	}
}
