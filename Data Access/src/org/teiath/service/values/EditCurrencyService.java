package org.teiath.service.values;

import org.teiath.data.domain.act.Currency;
import org.teiath.service.exceptions.ServiceException;

public interface EditCurrencyService {
	public void saveCurrency(Currency currency)
			throws ServiceException;

	public Currency getCurrencyById(Integer currencyId)
			throws ServiceException;

	public void toggleDefaultCurrency(Currency currency)
			throws ServiceException;

	public Currency getDefaultCurrency()
			throws ServiceException;

}
