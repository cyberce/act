package org.teiath.data.dao;

import org.teiath.data.domain.sys.SysParameter;

public interface SysParameterDAO {

	public SysParameter findById(Integer id);

	public void save(SysParameter sysParameter);

}
