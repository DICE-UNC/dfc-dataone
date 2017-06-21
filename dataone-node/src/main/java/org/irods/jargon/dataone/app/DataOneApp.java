package org.irods.jargon.dataone.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/dataone-node/")
public class DataOneApp extends Application {

}
