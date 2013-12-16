package org.teiath.web.vm.act.system;

import org.apache.log4j.Logger;
import org.teiath.data.domain.sys.SysParameter;
import org.teiath.service.act.system.EditSysParameterService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class EditSysParameterVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditSysParameterVM.class.getName());

	@WireVariable
	private EditSysParameterService editSysParameterService;

	private SysParameter sysParameter;

	@AfterCompose
	@NotifyChange("sysParameter")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		sysParameter = new SysParameter();

		try {
			sysParameter = editSysParameterService.getSysParameterById(1);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("system")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.INDEX);
		}
	}

	@Command
	public void onSave() {
		try {
			editSysParameterService.saveSysParameter(sysParameter);
			Messagebox.show(Labels.getLabel("common.messages.confirm"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("system")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.ACTION_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public SysParameter getSysParameter() {
		return sysParameter;
	}

	public void setSysParameter(SysParameter sysParameter) {
		this.sysParameter = sysParameter;
	}

}
