package com.fab.timetable.helper;

import java.time.LocalDateTime;

public class ErrorDetails {
	public LocalDateTime timestamp;
	public String message;
	public String details;

	public ErrorDetails(LocalDateTime timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}
}
