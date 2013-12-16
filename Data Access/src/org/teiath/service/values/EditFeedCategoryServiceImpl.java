package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedCategoryDAO;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.ServiceException;

@Service("editFeedCategoryService")
@Transactional
public class EditFeedCategoryServiceImpl
		implements EditFeedCategoryService {

	@Autowired
	FeedCategoryDAO feedCategoryDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFeedCategory(FeedCategory feedCategory)
			throws ServiceException {
		try {
			feedCategoryDAO.save(feedCategory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public FeedCategory getFeedCategoryById(Integer feedCategoryId)
			throws ServiceException {
		FeedCategory feedCategory;
		try {
			feedCategory = feedCategoryDAO.findById(feedCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feedCategory;
	}
}
