package org.irods.jargon.dataone.configuration;

/**
 * Pojo containing configuration information
 * 
 * @author Mike Conway - DICE (www.irods.org)
 * 
 */
public class RestConfiguration {

	private String irodsHost = "iren2.renci.org";
	private int irodsPort = 1247;
	private String irodsZone = "dfcmain";
	private String defaultStorageResource = "";
	private String realm = "dfc-dataone";
	private boolean smimeEncryptAdminFunctions = false;
	private String privateCertAbsPath = "";
	private String publicKeyAbsPath = "";
	private String irodsUserName;
	private String irodsUserPswd;
	
	private String handlePrefix = "11333/";

	/**
	 * Optional URL for a web interface to access grid data (typically an
	 * idrop-web installation pointing to the same grid)
	 */
	private String webInterfaceURL = "";

	/**
	 * @return the irodsHost
	 */
	public String getIrodsHost() {
		return irodsHost;
	}

	/**
	 * @param irodsHost
	 *            the irodsHost to set
	 */
	public void setIrodsHost(final String irodsHost) {
		this.irodsHost = irodsHost;
	}

	/**
	 * @return the irodsPort
	 */
	public int getIrodsPort() {
		return irodsPort;
	}

	/**
	 * @param irodsPort
	 *            the irodsPort to set
	 */
	public void setIrodsPort(final int irodsPort) {
		this.irodsPort = irodsPort;
	}

	/**
	 * @return the irodsZone
	 */
	public String getIrodsZone() {
		return irodsZone;
	}

	/**
	 * @param irodsZone
	 *            the irodsZone to set
	 */
	public void setIrodsZone(final String irodsZone) {
		this.irodsZone = irodsZone;
	}

	/**
	 * @return the defaultStorageResource
	 */
	public String getDefaultStorageResource() {
		return defaultStorageResource;
	}

	/**
	 * @param defaultStorageResource
	 *            the defaultStorageResource to set
	 */
	public void setDefaultStorageResource(final String defaultStorageResource) {
		this.defaultStorageResource = defaultStorageResource;
	}

	/**
	 * @return the realm
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * @param realm
	 *            the realm to set
	 */
	public void setRealm(final String realm) {
		this.realm = realm;
	}

	/**
	 * @return the smimeEncryptAdminFunctions
	 */
	public boolean isSmimeEncryptAdminFunctions() {
		return smimeEncryptAdminFunctions;
	}

	/**
	 * @param smimeEncryptAdminFunctions
	 *            the smimeEncryptAdminFunctions to set
	 */
	public void setSmimeEncryptAdminFunctions(boolean smimeEncryptAdminFunctions) {
		this.smimeEncryptAdminFunctions = smimeEncryptAdminFunctions;
	}

	/**
	 * @return the privateCertAbsPath
	 */
	public String getPrivateCertAbsPath() {
		return privateCertAbsPath;
	}

	/**
	 * @param privateCertAbsPath
	 *            the privateCertAbsPath to set
	 */
	public void setPrivateCertAbsPath(String privateCertAbsPath) {
		this.privateCertAbsPath = privateCertAbsPath;
	}

	/**
	 * @return the publicKeyAbsPath
	 */
	public String getPublicKeyAbsPath() {
		return publicKeyAbsPath;
	}

	/**
	 * @param publicKeyAbsPath
	 *            the publicKeyAbsPath to set
	 */
	public void setPublicKeyAbsPath(String publicKeyAbsPath) {
		this.publicKeyAbsPath = publicKeyAbsPath;
	}

	public String getWebInterfaceURL() {
		return webInterfaceURL;
	}

	public void setWebInterfaceURL(String webInterfaceURL) {
		this.webInterfaceURL = webInterfaceURL;
	}

	public String getIrodsUserName() {
		return irodsUserName;
	}

	public void setIrodsUserName(String irodsUserName) {
		this.irodsUserName = irodsUserName;
	}

	public String getIrodsUserPswd() {
		return irodsUserPswd;
	}

	public void setIrodsUserPswd(String irodsUserPswd) {
		this.irodsUserPswd = irodsUserPswd;
	}

	
}

