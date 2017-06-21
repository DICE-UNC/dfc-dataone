/**
 * 
 */
package org.irods.jargon.dataone.repo.impl.dummy;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.DataObjectListResponse;

/**
 * @author mcc
 *
 */
public class DummyRepoServiceImpl extends AbstractDataOneRepoServiceAO {

	public DummyRepoServiceImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.dataone.repo.reposervice.AbstractRepoServiceAO#
	 * getListOfDataoneExposedDataObjects(java.util.Date, java.util.Date,
	 * org.dataone.service.types.v1.ObjectFormatIdentifier, java.lang.Boolean,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(Date arg0, Date arg1, ObjectFormatIdentifier arg2,
			Boolean arg3, Integer arg4, Integer arg5) throws JargonException {
		return new DataObjectListResponse();
	}

	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {
		// FIXME: narrow down the needed public methods
		return null;
	}

	@Override
	public Date getLastModifiedDateForDataObject(DataObject dataObject) throws JargonException {
		return new Date();
	}

	@Override
	public String dataObjectFormat(DataObject dataObject) throws FileNotFoundException, JargonException {
		return "";
	}

}
