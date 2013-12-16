package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.service.act.ViewFeedDataService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class ViewFeedDataVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewFeedDataVM.class.getName());

	@Wire("#feedDataViewWin")
	private Window win;

	@WireVariable
	private ViewFeedDataService viewFeedDataService;

	private FeedData feedData;

	@AfterCompose
	@NotifyChange("feedData")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		int feedDataId = (Integer) Executions.getCurrent().getArg().get("FEED_DATA_ID");

		try {
			feedData = viewFeedDataService.getFeedDataById(feedDataId);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ACTION_LIST);
		}
	}

	@Command
	public void onClose() {
		win.detach();
	}

	public FeedData getFeedData() {
		return feedData;
	}

	public void setFeedData(FeedData feedData) {
		this.feedData = feedData;
	}
}
