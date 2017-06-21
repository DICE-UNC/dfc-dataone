/**
 * 
 */
package org.irods.jargon.dataone.configuration;

/**
 * Warns of a missing plugin of a given type
 * 
 * @author mconway
 *
 */
public class PluginNotFoundException extends Exception {

	private static final long serialVersionUID = 2365990131845450987L;

	public PluginNotFoundException(String string) {
		super(string);
	}

	public PluginNotFoundException(Exception e) {
		super(e);
	}

}
