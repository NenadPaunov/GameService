package org.nenad.paunov.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.dto.ErrorResponse;
import org.nenad.paunov.exception.EntityNotFoundException;
import org.nenad.paunov.exception.ExternalServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class ControllerExceptionAdvice {

	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED) // 405
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public ErrorResponse wrongHttpMethod(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
		log.warn("Unsupported HTTP method: {}", e.getMessage());
		return new ErrorResponse(ErrorResponse.ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ErrorResponse invalidRequestBody(HttpServletRequest req, MethodArgumentNotValidException e) {
		log.debug("Invalid HTTP request: {}", e.getMessage());
		return new ErrorResponse(ErrorResponse.ErrorCode.BAD_REQUEST, e.getMessage());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // 500
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	public ErrorResponse dataIntegrityViolationException(HttpServletRequest req, DataIntegrityViolationException e) {
		log.debug("Handling DataIntegrityViolationException, returning 500", e);
		return new ErrorResponse(ErrorResponse.ErrorCode.INVALID_STATE,
				"Player with that name already exists: " + e.getMessage());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // 500
	@ExceptionHandler(ExternalServiceException.class)
	@ResponseBody
	public ErrorResponse externalServiceException(HttpServletRequest req, ExternalServiceException e) {
		log.debug("Handling ExternalServiceException, returning 500", e);
		return new ErrorResponse(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR,
				"Error occurred while calling external service: " + e.getMessage());
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)  // 404
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody
	public ErrorResponse entityNotFoundException(HttpServletRequest req, EntityNotFoundException e) {
		log.debug("Handling EntityNotFoundException, returning 500");
		return new ErrorResponse(ErrorResponse.ErrorCode.NOT_FOUND,
				"Entity not found");
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // 500
	@ExceptionHandler(ResourceAccessException.class)
	@ResponseBody
	public ErrorResponse resourceAccessException(HttpServletRequest req, ResourceAccessException e) {
		log.debug("Handling ResourceAccessException, returning 500", e);
		return new ErrorResponse(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR,
				"ResourceAccessException occurred: " + e.getMessage());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // 500
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ErrorResponse uncaughtException(HttpServletRequest req, Exception e) {
		log.error("Uncaught exception", e);
		return new ErrorResponse(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR,
				"Unexpected " + e.getClass().getSimpleName() + " occurred: " + e.getMessage());
	}
}
