package org.teiath.data.dao;

import org.teiath.data.domain.act.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserActionSearchCriteria;

public interface UserActionDAO {

	public UserAction findById(Integer id);

	public SearchResults<UserAction> search(UserActionSearchCriteria criteria);

	public void save(UserAction userAction);

}
