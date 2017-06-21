package org.irods.jargon.dataone.pidservice.impl.dummy;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.pid.pidservice.AbstractUniqueIdAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPidServiceAOImpl extends AbstractUniqueIdAO {

	public static final Logger log = LoggerFactory.getLogger(DummyPidServiceAOImpl.class);

	public DummyPidServiceAOImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	@Override
	public Identifier getIdentifierFromDataObject(DataObject dataObject) throws JargonException {
		Identifier id = new Identifier();
		id.setValue(new String("http://handle/dummy/" + String.valueOf(System.currentTimeMillis())));
		return id;
	}

	@Override
	public DataObject getDataObjectFromIdentifier(Identifier identifier) throws JargonException {
		return new DataObject();
	}

}
