package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "eventCategories")
@JsonRootName(value = "eventCategories")
public class ServiceCategoryList {
	private List<ServiceCategory> serviceCategories;

	public ServiceCategoryList() {
	}

	public ServiceCategoryList(List<ServiceCategory> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	@XmlElement(name = "category")
	public List<ServiceCategory> getServiceCategories() {
		return this.serviceCategories;
	}

	public void setServiceCategories(List<ServiceCategory> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}
}
