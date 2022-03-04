package com.premio.cinema.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AppRestHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AppRestHandler.class);

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getParameterName() + " parâmetro ausente";
		return buildResponseEntity(new AppError(BAD_REQUEST, error, ex));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" o tipo de dado não é suportado. Os tipos de dados suportados são ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
		return buildResponseEntity(
				new AppError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		AppError appError = new AppError(BAD_REQUEST);
		appError.setMessage("Erro de validação");
		appError.addValidationErrors(ex.getBindingResult().getFieldErrors());
		appError.addValidationError(ex.getBindingResult().getGlobalErrors());
		return buildResponseEntity(appError);
	}

	@ExceptionHandler(javax.validation.ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {
		AppError appError = new AppError(BAD_REQUEST);
		appError.setMessage("Erro de validação");
		appError.addValidationErrors(ex.getConstraintViolations());
		return buildResponseEntity(appError);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		AppError appError = new AppError(NOT_FOUND);
		appError.setMessage(ex.getMessage());
		return buildResponseEntity(appError);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
		String error = "A requisição JSON não está estruturada corretamente";
		return buildResponseEntity(new AppError(HttpStatus.BAD_REQUEST, error, ex));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Erro ao estruturar a resposta JSON";
		return buildResponseEntity(new AppError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		AppError appError = new AppError(BAD_REQUEST);
		appError.setMessage(
				String.format("Não foi possível encontrar o %s mótodo para URL %s", ex.getHttpMethod(), ex.getRequestURL()));
		appError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(appError);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		AppError appError = new AppError(HttpStatus.METHOD_NOT_ALLOWED);
		appError.setMessage(String.format("Método não suporta a requisição dessa URL %s",
				((ServletWebRequest) request).getRequest().getRequestURL().toString()));
		appError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(appError);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		if (ex.getCause() instanceof ConstraintViolationException) {
			return buildResponseEntity(new AppError(HttpStatus.CONFLICT, "Erro Banco de Dados", ex.getCause()));
		}
		return buildResponseEntity(new AppError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		AppError appError = new AppError(BAD_REQUEST);
		appError.setMessage(String.format("O parâmetro '%s' com o valor '%s' não pode ser convertido para o tipo '%s'",
				ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
		appError.setDebugMessage(ex.getMessage());
		return buildResponseEntity(appError);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
			HttpServletRequest request) {
		AppError appError = new AppError(BAD_REQUEST);

		appError.setMessage(ex.getMessage());
		return buildResponseEntity(appError);
	}

	private ResponseEntity<Object> buildResponseEntity(AppError appError) {
		return new ResponseEntity<>(appError, appError.getStatus());
	}
}
