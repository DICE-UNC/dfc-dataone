/**
 * 
 */
package org.irods.jargon.dataone.configuration;

/**
 * @author mconway
 *
 */
public class PluginRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1878244280920075564L;

	/**
	 * 
	 */
	public PluginRuntimeException() {
	}

	/**
	 * @param arg0
	 */
	public PluginRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public PluginRuntimeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PluginRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public PluginRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
