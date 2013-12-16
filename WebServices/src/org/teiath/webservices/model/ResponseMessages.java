package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "response")
@JsonRootName(value = "response")
public class ResponseMessages {
	private List<String> message;

	public ResponseMessages() {
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	public void addMessage(String message) {
		if (this.message == null) {
			this.message = new ArrayList<>();
		}

		this.message.add(message);
	}

}
