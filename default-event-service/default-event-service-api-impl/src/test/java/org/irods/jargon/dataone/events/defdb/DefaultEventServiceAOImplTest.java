package org.irods.jargon.dataone.events.defdb;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultEventServiceAOImplTest {

	@Test
	public void testLoad() throws Exception {
		IRODSAccount dummyAccount = IRODSAccount.instance("host", 1247, "user", "pwd", "", "zone", "");
		IRODSAccessObjectFactory iaf = Mockito.mock(IRODSAccessObjectFactory.class);
		PublicationContext context = new PublicationContext();
		context.setIrodsAccessObjectFactory(iaf);
		AccessLogDAO accessLogDAO = Mockito.mock(AccessLogDAO.class);
		DefaultEventServiceAOImpl defaultEventServiceAO = new DefaultEventServiceAOImpl(dummyAccount, context,
				accessLogDAO);
		Assert.assertNotNull("no event service returned", defaultEventServiceAO);
	}

}
