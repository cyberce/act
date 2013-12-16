package org.teiath.data.dao;

import org.teiath.data.domain.act.Currency;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;

import java.util.Collection;

public interface CurrencyDAO {

	public Collection<Currency> findAll();

	public void save(Currency currency);

	public void delete(Currency currency);

	public Currency findDefaultCurrency();

	public Currency findById(Integer id);

	public SearchResults<Currency> search(SearchCriteria criteria);

}
