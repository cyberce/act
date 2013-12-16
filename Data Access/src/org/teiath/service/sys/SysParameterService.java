package org.teiath.service.sys;

import org.teiath.data.domain.sys.SysParameter;
import org.teiath.service.exceptions.ServiceException;

public interface SysParameterService {

	public SysParameter fetchSystemParameters()
			throws ServiceException;

}
