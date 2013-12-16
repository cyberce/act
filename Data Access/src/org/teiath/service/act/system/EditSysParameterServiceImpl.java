package org.teiath.service.act.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.SysParameterDAO;
import org.teiath.data.domain.sys.SysParameter;
import org.teiath.service.exceptions.ServiceException;

@Service("editSysParameterService")
@Transactional
public class EditSysParameterServiceImpl
		implements EditSysParameterService {

	@Autowired
	SysParameterDAO sysParameterDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveSysParameter(SysParameter sysParameter)
			throws ServiceException {

		try {
			sysParameterDAO.save(sysParameter);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public SysParameter getSysParameterById(Integer sysParameterId)
			throws ServiceException {
		SysParameter sysParameter;
		try {
			sysParameter = sysParameterDAO.findById(sysParameterId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return sysParameter;
	}
}
