package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.CurrencyDAO;
import org.teiath.data.domain.act.Currency;
import org.teiath.service.exceptions.ServiceException;

@Service("editCurrencyService")
@Transactional
public class EditCurrencyServiceImpl
		implements EditCurrencyService {

	@Autowired
	CurrencyDAO currencyDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveCurrency(Currency currency)
			throws ServiceException {
		try {
			currencyDAO.save(currency);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Currency getCurrencyById(Integer currencyId)
			throws ServiceException {
		Currency currency;
		try {
			currency = currencyDAO.findById(currencyId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currency;
	}

	@Override
	public void toggleDefaultCurrency(Currency currency)
			throws ServiceException {
		try {
			currency.setDefaultCurrency(false);
			currencyDAO.save(currency);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Currency getDefaultCurrency()
			throws ServiceException {
		Currency currency;
		try {
			currency = currencyDAO.findDefaultCurrency();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currency;
	}
}