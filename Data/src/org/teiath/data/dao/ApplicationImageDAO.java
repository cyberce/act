package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.image.ApplicationImage;

import java.util.Collection;

public interface ApplicationImageDAO {

	public ApplicationImage findByUser(User user);

	public void save(ApplicationImage applicationImage);

	public void delete(ApplicationImage applicationImage);

	public Collection<ApplicationImage> findByEvent(Event event);

	public void deleteAll(Event event);
}
