package org.teiath.service.values;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.CurrencyDAO;
import org.teiath.data.dao.EventCategoryDAO;
import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listValuesService")
@Transactional
public class ListValuesServiceImpl
		implements ListValuesService {

	@Autowired
	EventCategoryDAO eventCategoryDAO;
	@Autowired
	CurrencyDAO currencyDAO;

	@Override
	public Collection<EventCategory> getEventCategories()
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteEventCategory(EventCategory eventCategory)
			throws ServiceException, DeleteViolationException {
		try {
			eventCategoryDAO.delete(eventCategory);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findByParentId(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public EventCategory getEventCategoryByParentId(Integer parentId)
			throws ServiceException {
		EventCategory rootCategory;

		try {
			rootCategory = eventCategoryDAO.findById(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return rootCategory;
	}


	@Override
	public SearchResults<Currency> searchCurrenciesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException {
		SearchResults<Currency> results;

		try {
			results = currencyDAO.search(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<Currency> getCurrencies()
			throws ServiceException {
		Collection<Currency> currencies;

		try {
			currencies = currencyDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currencies;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCurrency(Currency currency)
			throws ServiceException, DeleteViolationException {
		try {
			currencyDAO.delete(currency);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
