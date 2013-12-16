package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.service.act.SearchFeedDataService;
import org.teiath.service.aggregator.AggregatorService;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListFeedsService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class CleanFeedDataVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CleanFeedDataVM.class.getName());

	@Wire("#cleanFeedDataPopupWin")
	private Window win;
	@Wire("#dateFromBox")
	private Datebox dateFromBox;
	@Wire("#dateToBox")
	private Datebox dateToBox;

	@WireVariable
	private SearchFeedDataService searchFeedDataService;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onClean() {

		Messagebox.show(Labels.getLabel("feedData.deleteQuestion"),
				Labels.getLabel("feeData.clean"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
			public void onEvent(org.zkoss.zk.ui.event.Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						try {
							searchFeedDataService.deleteFeedDataByDate(dateFromBox.getValue(), dateToBox.getValue());
							Messagebox.show(Labels.getLabel("feedData.cleaned"),
									Labels.getLabel("common.messages.confirm"), Messagebox.OK,
									Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
								public void onEvent(org.zkoss.zk.ui.event.Event evt) {
									BindUtils.postGlobalCommand(null, null, "repopulate", null);
									win.detach();
								}
							});
							break;
						} catch (ServiceException e) {
							log.error(e.getMessage());
							Messagebox.show(MessageBuilder
									.buildErrorMessage(e.getMessage(), Labels.getLabel("feedData.$")),
									Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
									Messagebox.ERROR);
						}
					case Messagebox.NO:
						win.detach();
						break;
				}
			}
		});
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						win.detach();
					case Messagebox.NO:
						break;
				}
			}
		});
	}
}
