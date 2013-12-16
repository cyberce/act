package org.teiath.service.values;

import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListValuesService {

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public void deleteEventCategory(EventCategory eventCategory)
			throws ServiceException, DeleteViolationException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;

	public EventCategory getEventCategoryByParentId(Integer parentId)
			throws ServiceException;

	public SearchResults<Currency> searchCurrenciesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;

	public void deleteCurrency(Currency currency)
			throws ServiceException, DeleteViolationException;
}

