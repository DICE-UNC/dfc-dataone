/**
 *
 */
package org.irods.jargon.dataone.auth;

import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.utils.RestTestingProperties;
import org.irods.jargon.testutils.TestingPropertiesHelper;
import org.irods.jargon.testutils.TestingUtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lisa Stillwell - RENCI (www.renci.org)
 *
 */
public class RestAuthUtils {

	private static Logger log = LoggerFactory.getLogger(RestAuthUtils.class);

	/**
	 * from anonymous build an iRODS account
	 *
	 * @param restConfiguration
	 * @return
	 * @throws JargonException
	 */
	public static IRODSAccount getIRODSAccountFromBasicAuthValues(
			final RestConfiguration restConfiguration) throws JargonException {

		log.info("getIRODSAccountFromBasicAuthValues");

		if (restConfiguration == null) {
			throw new IllegalArgumentException("null restConfiguration");
		}

		// return IRODSAccount.instanceForAnonymous(
		// restConfiguration.getIrodsHost(),
		// restConfiguration.getIrodsPort(),
		// "",
		// restConfiguration.getIrodsZone(),
		// restConfiguration.getDefaultStorageResource());
		return IRODSAccount.instance(restConfiguration.getIrodsHost(),
				restConfiguration.getIrodsPort(),
				restConfiguration.getIrodsUserName(),
				restConfiguration.getIrodsUserPswd(), "",
				restConfiguration.getIrodsZone(),
				restConfiguration.getDefaultStorageResource());

	}

	/**
	 * Return boilerplate http client for testing that uses basic auth
	 *
	 * @param irodsAccount
	 * @param testingProperties
	 * @return
	 * @throws TestingUtilsException
	 */
	public static DefaultHttpClientAndContext httpClientSetup(
			final IRODSAccount irodsAccount, final Properties testingProperties)
					throws TestingUtilsException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount");
		}

		if (testingProperties == null) {
			throw new IllegalArgumentException("null testingProperties");
		}

		TestingPropertiesHelper testingPropertiesHelper = new TestingPropertiesHelper();
		HttpHost targetHost = new HttpHost("localhost",
				testingPropertiesHelper.getPropertyValueAsInt(
						testingProperties,
						RestTestingProperties.REST_PORT_PROPERTY), "http");

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials(irodsAccount.getUserName(),
						irodsAccount.getPassword()));
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local
		// auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
		DefaultHttpClientAndContext clientAndContext = new DefaultHttpClientAndContext();
		clientAndContext.setHost(targetHost.getHostName());
		clientAndContext.setHttpClient(httpclient);
		clientAndContext.setHttpContext(localcontext);
		return clientAndContext;
	}
}
