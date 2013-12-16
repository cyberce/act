package org.teiath.service.values;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedCategoryDAO;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
@Service("listCategoriesFeedsService")
@Transactional
public class ListCategoriesFeedsServiceImpl
		implements ListCategoriesFeedsService {

	@Autowired
	FeedCategoryDAO feedCategoryDAO;

	@Override
	public Collection<FeedCategory> getFeedCategories()
			throws ServiceException {
		Collection<FeedCategory> categories;

		try {
			categories = feedCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteFeedCategory(FeedCategory feedCategory)
			throws ServiceException, DeleteViolationException {
		try {
			feedCategoryDAO.delete(feedCategory);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

}
