package org.teiath.data.dao;

import org.teiath.data.domain.Assessment;

public interface AssessmentDAO {

	public Assessment findById(Integer id);

	public void save(Assessment assessment);

	public void delete(Assessment assessment);
}
