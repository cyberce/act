package org.teiath.service.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.SysParameterDAO;
import org.teiath.data.domain.sys.SysParameter;
import org.teiath.service.exceptions.ServiceException;

@Service("sysParameterService")
@Transactional
public class SysParameterServiceImpl implements SysParameterService {

	@Autowired
	private SysParameterDAO sysParameterDAO;

	public SysParameter fetchSystemParameters()
			throws ServiceException {
		try {
			return sysParameterDAO.findById(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

}
