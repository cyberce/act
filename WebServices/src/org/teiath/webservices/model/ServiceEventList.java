package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "events")
@JsonRootName(value = "events")
public class ServiceEventList {

	private List<ServiceEvent> events;

	public ServiceEventList() {

	}

	public ServiceEventList(List<ServiceEvent> events) {
		this.events = events;
	}

	@XmlElement(name = "event")
	public List<ServiceEvent> getServiceEvents() {
		return this.events;
	}

	public void setServiceEvents(List<ServiceEvent> events) {
		this.events = events;
	}
}
