package org.teiath.data.dao;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.pdf.EventPDF;

import java.util.Collection;

public interface EventPDFDAO {

	//	public ApplicationImage findByUser(User user);

	public void save(EventPDF eventPDF);

	public Collection<EventPDF> findByEvent(Event event);

	public void deleteAll(Event event);

	//	public void delete(ApplicationImage applicationImage);
}
