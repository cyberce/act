package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.service.act.SearchFeedDataService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class DeleteFeedVM
		extends BaseVM {

	static Logger log = Logger.getLogger(DeleteFeedVM.class.getName());

	@Wire("#deleteFeedPopupWin")
	private Window win;

	@WireVariable
	private SearchFeedDataService searchFeedDataService;

	private Boolean deleteFeedData;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		deleteFeedData = false;
	}

	@Command
	public void onConfirm() {

	    if (deleteFeedData)
		    BindUtils.postGlobalCommand(null, null, "deleteFeedData", null);
		else
		    BindUtils.postGlobalCommand(null, null, "deleteFeed", null);

		win.detach();
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

	public Boolean getDeleteFeedData() {
		return deleteFeedData;
	}

	public void setDeleteFeedData(Boolean deleteFeedData) {
		this.deleteFeedData = deleteFeedData;
	}
}
