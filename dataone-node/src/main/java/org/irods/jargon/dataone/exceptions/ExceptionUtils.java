package org.irods.jargon.dataone.exceptions;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExceptionUtils {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static String getNotFoundXmlForObjectId(final String pid,
			final String detailCode, final String description) {

		final String name = "NotFound";
		final String errorCode = "404";

		DocumentBuilder documentBuilder;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return e.getMessage();
		} catch (FactoryConfigurationError e) {
			return e.getMessage();
		}
		Document dom = documentBuilder.newDocument();
		// create the root node of the dom
		Element errorNode = dom.createElement("error");

		errorNode.setAttribute("name", name);
		errorNode.setAttribute("detailCode", detailCode);
		errorNode.setAttribute("errorCode", errorCode);

		Element desc = dom.createElement("description");
		desc.appendChild(dom.createTextNode(description));

		dom.appendChild(errorNode);
		errorNode.appendChild(desc);

		try {
			return domToString(dom);
		} catch (Exception ex) {
			// log.error(ex.getMessage());
			return ex.getMessage();
		}
	}

	private static String domToString(final Document document) throws Exception {
		String result = null;
		StringWriter strWtr = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		if (document != null) {
			StreamResult strResult = new StreamResult(strWtr);
			transformer.transform(new DOMSource(document.getDocumentElement()),
					strResult);
			result = strResult.getWriter().toString();
		}
		return result;
	}

}
