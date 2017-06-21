/**
 * 
 */
package org.irods.jargon.dataone.events;

import org.irods.jargon.core.exception.JargonException;

/**
 * Exception in event logging
 * 
 * @author mcc
 *
 */
public class EventLoggingException extends JargonException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5743883673770774132L;

	/**
	 * @param message
	 */
	public EventLoggingException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventLoggingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public EventLoggingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param underlyingIRODSExceptionCode
	 */
	public EventLoggingException(String message, Throwable cause, int underlyingIRODSExceptionCode) {
		super(message, cause, underlyingIRODSExceptionCode);
	}

	/**
	 * @param cause
	 * @param underlyingIRODSExceptionCode
	 */
	public EventLoggingException(Throwable cause, int underlyingIRODSExceptionCode) {
		super(cause, underlyingIRODSExceptionCode);
	}

	/**
	 * @param message
	 * @param underlyingIRODSExceptionCode
	 */
	public EventLoggingException(String message, int underlyingIRODSExceptionCode) {
		super(message, underlyingIRODSExceptionCode);
	}

}
