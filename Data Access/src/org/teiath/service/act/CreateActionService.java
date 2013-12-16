package org.teiath.service.act;

import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

public interface CreateActionService {

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;

	public void saveEvent(Event event, ListModelList<AImage> uploadedImages, ListModelList<EventPDF> uploadedPDFs)
			throws ServiceException;

	public void saveEvent(Event event) throws ServiceException;

	public void sendNotifications(Event event) throws ServiceException;

	public ListModelList<String> getPopularLocations()
			throws ServiceException;

	public FeedData getFeedDataById(Integer eventId)
			throws ServiceException;

	public void saveFeedData(FeedData feedData)
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;

	public Currency getDefaultCurrency()
			throws ServiceException;
}

