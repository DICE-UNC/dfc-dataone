package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.Ping;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "node")
@XmlType(propOrder = { "identifier", "name", "description", "baseURL", "services", "synchronization", "ping", "subject",
		"contactSubject" })
public class MNNode {

	private final String serviceKey = "irods.dataone.service.";
	private final String serviceKeyName = ".name";

	@XmlAttribute
	private String replicate;

	@XmlAttribute
	private String synchronize;

	@XmlAttribute
	private String type;

	@XmlAttribute
	private String state;

	private String identifier; // MN NodeReference type
	private String name;
	private String description;
	private String baseURL;
	private List<MNService> services;
	private MNSynchronization synchronization;
	// private NodeReplicationPolicy nodeReplicationPolicy;
	private MNPing ping;
	private List<String> subject;
	private List<String> contactSubject;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public MNNode() {
		initializeProperties();
	}

	private void initializeProperties() {
		PropertiesLoader loader = new PropertiesLoader();
		Properties prop = loader.getProperties();

		replicate = prop.getProperty("irods.dataone.replicate");
		synchronize = prop.getProperty("irods.dataone.synchronize");
		type = prop.getProperty("irods.dataone.type");
		// TODO: make sure proper exceptions are caught and handled

		identifier = prop.getProperty("irods.dataone.identifier");
		name = prop.getProperty("irods.dataone.name");
		description = prop.getProperty("irods.dataone.description");
		baseURL = prop.getProperty("irods.dataone.baseurl");
		services = initServices(prop);
		synchronization = initSychronization(prop);
		// this.subject = initSubjects(prop);
		// this.contactSubject = initContactSubjects(prop);

	}

	private List<String> initContactSubjects(final Properties prop) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> initSubjects(final Properties prop) {
		// TODO Auto-generated method stub
		return null;
	}

	private MNSynchronization initSychronization(final Properties prop) {
		MNSynchronization mnSynchronization = new MNSynchronization();

		return mnSynchronization;
	}

	private List<MNService> initServices(final Properties prop) {
		List<MNService> services = new ArrayList<>();

		Enumeration<?> keys = prop.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if ((key.startsWith(serviceKey)) && (key.endsWith(serviceKeyName))) {
				String serviceName = prop.getProperty(key);
				MNService service = new MNService();
				service.setName(serviceName);
				String isServiceAvailableKey = serviceKey + serviceName.toLowerCase();
				String isServiceAvailableValue = prop.getProperty(isServiceAvailableKey);
				service.setAvailable(Boolean.parseBoolean(isServiceAvailableValue));
				String verKey = isServiceAvailableKey + ".version";
				service.setVersion(prop.getProperty(verKey));
				services.add(service);
			}
		}

		return services;
	}

	// public NodeReference getIdentifier() {
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(final String baseURL) {
		this.baseURL = baseURL;
	}

	@XmlElementWrapper(name = "services")
	@XmlElement(name = "service")
	public List<MNService> getServices() {
		return services;
	}

	public void setServices(final List<MNService> services) {
		this.services = services;
	}

	public MNSynchronization getSynchronization() {
		return synchronization;
	}

	public void setSynchronization(final MNSynchronization synchronization) {
		this.synchronization = synchronization;
	}

	public MNPing getPing() {
		return ping;
	}

	public void setPing(final MNPing ping) {
		this.ping = ping;
	}

	// public MNSubject getSubject() {
	// return subject;
	// }
	// public void setSubject(MNSubject subject) {
	// this.subject = subject;
	// }
	//
	// public MNSubject getContactSubject() {
	// return contactSubject;
	// }
	// public void setContactSubject(MNSubject contactSubject) {
	// this.contactSubject = contactSubject;
	// }

	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(final List<String> subject) {
		this.subject = subject;
	}

	public List<String> getContactSubject() {
		return contactSubject;
	}

	public void setContactSubject(final List<String> contactSubject) {
		this.contactSubject = contactSubject;
	}

	// public NodeReplicationPolicy getNodeReplicationPolicy() {
	// return nodeReplicationPolicy;
	// }
	//
	// public void setNodeReplicationPolicy(NodeReplicationPolicy
	// nodeReplicationPolicy) {
	// this.nodeReplicationPolicy = nodeReplicationPolicy;
	// }

	public void copy(final Node node) {

		if (node == null) {
			throw new IllegalArgumentException("MNNode::copy - Node is null");
		}

		replicate = Boolean.toString(node.isReplicate());
		synchronize = Boolean.toString(node.isSynchronize());

		if (node.getType() != null) {
			type = node.getType().name();
		}

		if (node.getState() != null) {
			state = node.getState().xmlValue();
		}

		if (node.getIdentifier() != null) {
			identifier = node.getIdentifier().getValue();
		}

		if (node.getName() != null) {
			name = node.getName();
		}

		if (node.getDescription() != null) {
			description = node.getDescription();
		}

		if (node.getBaseURL() != null) {
			baseURL = node.getBaseURL();
		}

		if (node.getServices() != null) {
			Services services = node.getServices();
			List<Service> serviceList = services.getServiceList();
			this.services = new ArrayList<>();

			for (Service s : serviceList) {
				MNService mnService = new MNService();
				mnService.copy(s);
				this.services.add(mnService);
			}
		}

		if (node.getSynchronization() != null) {
			Synchronization synch = node.getSynchronization();
			synchronization = new MNSynchronization();
			synchronization.copy(synch);
		}

		if (node.getPing() != null) {
			Ping ping = node.getPing();
			this.ping = new MNPing();
			this.ping.copy(ping);
		}

		if (node.getSubjectList() != null) {
			List<Subject> subjectList = node.getSubjectList();
			subject = new ArrayList<>();

			for (Subject s : subjectList) {
				subject.add(s.getValue());
			}
		}

		if (node.getContactSubjectList() != null) {
			List<Subject> subjectList = node.getContactSubjectList();
			contactSubject = new ArrayList<>();

			for (Subject s : subjectList) {
				contactSubject.add(s.getValue());
			}
		}
	}
}
