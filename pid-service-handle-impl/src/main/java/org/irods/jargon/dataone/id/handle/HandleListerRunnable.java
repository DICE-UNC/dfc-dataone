package org.irods.jargon.dataone.id.handle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.ListHandlesRequest;
import net.handle.hdllib.ListHandlesResponse;
import net.handle.hdllib.PublicKeyAuthenticationInfo;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResponseMessageCallback;
import net.handle.hdllib.ServerInfo;
import net.handle.hdllib.SiteInfo;
import net.handle.hdllib.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//  ./hdl-list -s 0.NA/11333 300  ../../svr_1/admpriv.bin  11333
public class HandleListerRunnable implements Runnable, ResponseMessageCallback {

	HandleResolver resolver = new HandleResolver();
	private final String authHandle;
	private final String authIndex;
	private final String privateKey;
	private final String namingAuthority;
	boolean showValues = true;
	// private Queue<List<String>> results;
	private LinkedList<List<String>> results;
	private final List<String> handles;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public HandleListerRunnable(
			// Queue<List<String>> queue,
			final LinkedList<List<String>> queue, final String authHandle,
			final String authIndex, final String privateKey,
			final String namingAuthority) {

		// TODO: check for valid args here
		log.info("in HandleListerRunnable");
		results = queue;
		this.authHandle = authHandle;
		this.authIndex = authIndex;
		this.privateKey = privateKey;
		this.namingAuthority = namingAuthority;
		handles = new ArrayList<String>();

		// byte[] key = null;
		// FileInputStream fs = null;
		// //InputStream fs = null;
		// try {
		// //fs =
		// getClass().getClassLoader().getResourceAsStream(this.privateKey);
		// File f = new File(this.privateKey);
		// fs = new FileInputStream(f);
		// key = new byte[(int)f.length()];
		// int n=0;
		// while(n<key.length) key[n++] = (byte)fs.read();
		// fs.read(key);
		// }
		InputStream fis = getClass().getClassLoader().getResourceAsStream(
				this.privateKey);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] key = null;
		try {
			byte[] buf = new byte[1024];
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
			key = bos.toByteArray();
		} catch (Throwable t) {
			log.info("Cannot read private key: {} " + this.privateKey + ": "
					+ t);
			System.exit(-1);
		} finally {
			try {
				fis.close();
				bos.close();
			} catch (IOException e) {
				// not much to do - just log this
				log.warn("HandleLister: cannot close private key file: {}",
						e.getMessage());
			}
		}

		resolver.traceMessages = true;
		PrivateKey privkey = null;
		byte secKey[] = null;
		try {
			if (Util.requiresSecretKey(key)) {
				secKey = Util.getPassphrase("passphrase: ");
			}
			key = Util.decrypt(key, secKey);
			privkey = Util.getPrivateKeyFromBytes(key, 0);
		} catch (Throwable t) {
			System.err.println("Can't load private key in " + this.privateKey
					+ ": " + t);
			System.exit(-1);
		}

		ResolutionRequest resReq = new ResolutionRequest(
				Util.encodeString(this.namingAuthority), null, null, null);
		log.info("finding local sites for {}", resReq);
		SiteInfo sites[] = null;
		try {
			sites = resolver.findLocalSites(resReq);
		} catch (HandleException e) {
			log.error(
					"HandleLister: encountered exception while finding local server sites: {}",
					e.getMessage());
		}

		PublicKeyAuthenticationInfo auth = new PublicKeyAuthenticationInfo(
				Util.encodeString(this.authHandle), Integer.valueOf(
						this.authIndex).intValue(), privkey);
		ListHandlesRequest req = new ListHandlesRequest(
				Util.getZeroNAHandle(Util.encodeString(this.namingAuthority)),
				auth);
		// send a list-handles request to each server in the site
		for (ServerInfo server : sites[0].servers) {
			try {
				resolver.sendRequestToServer(req, sites[0], server, this);
			} catch (Throwable t) {
				log.error("HandleLister: error sending request to server: {}",
						t.getMessage());
			}
		}
	}

	@Override
	public void handleResponse(final AbstractResponse response) {

		resolver.traceMessages = false;

		log.info("got callback, response: {}", response.toString());

		if (response instanceof ListHandlesResponse) {
			try {
				ListHandlesResponse lhResp = (ListHandlesResponse) response;
				byte handleArray[][] = lhResp.handles;
				for (byte[] element : handleArray) {
					String sHandle = Util.decodeString(element);
					log.info("extracted handle: {}", sHandle);
					handles.add(sHandle);
				}
			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
				// e.printStackTrace(System.err);
			}

		} else if (response.responseCode != AbstractMessage.RC_AUTHENTICATION_NEEDED) {
			log.error("handleResponse: retrieval of handle list failed: {}",
					response.toString());
		}
	}

	@Override
	public void run() {
		log.info("in HandleListerRunnable run, handles: {}", handles.toString());
		synchronized (results) {
			results.add(handles);
			results.notify();
		}
	}

	// public List<String> getHandles() {
	// return this.handles;
	// }

}
