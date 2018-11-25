package com.fab.timetable.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomException.class);
	private static final long serialVersionUID = 1L;
	
	public CustomException(String msg) {
		super(msg);
		LOGGER.error(msg);
	}
}
