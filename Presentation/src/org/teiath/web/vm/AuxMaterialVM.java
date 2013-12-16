package org.teiath.web.vm;

import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

public class AuxMaterialVM {


	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void api() {
		ZKSession.sendRedirect("/zul/aux_material/api.zul");
	}

	@Command
	public void manual() {
		ZKSession.sendRedirect("/zul/aux_material/manual.zul");
	}

	@Command
	public void presentation() {
		ZKSession.sendRedirect("/zul/aux_material/presentation.zul");
	}
}
