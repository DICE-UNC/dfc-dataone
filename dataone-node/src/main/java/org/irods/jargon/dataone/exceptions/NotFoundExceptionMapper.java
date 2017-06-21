package org.irods.jargon.dataone.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.dataone.service.exceptions.NotFound;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFound> {

	@Override
	public Response toResponse(final NotFound ex) {

		return Response
				.status(Response.Status.NOT_FOUND)
				.header("Vary", "Accept-Encoding")
				.entity(ExceptionUtils.getNotFoundXmlForObjectId(ex.getPid(),
						ex.getDetail_code(), ex.getDescription()))
				.type(MediaType.TEXT_XML).build();

	}

}