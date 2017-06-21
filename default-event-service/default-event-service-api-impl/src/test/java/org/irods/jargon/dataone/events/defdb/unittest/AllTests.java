package org.irods.jargon.dataone.events.defdb.unittest;

import org.irods.jargon.dataone.events.defdb.DefaultEventServiceAOImplTest;
import org.irods.jargon.dataone.events.defdb.DefaultEventServiceFactoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DefaultEventServiceAOImplTest.class, DefaultEventServiceFactoryTest.class })
public class AllTests {

}
