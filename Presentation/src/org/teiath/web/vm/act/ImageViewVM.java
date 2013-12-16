package org.teiath.web.vm.act;

import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Image;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class ImageViewVM {

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		Image image = new Image();

		if (ZKSession.getAttribute("aImage") != null) {
			AImage aImage = (AImage) ZKSession.getAttribute("aImage");
			image.setContent(aImage);
		}

		if (ZKSession.getAttribute("image") != null) {
			Image clickedImage = (Image) ZKSession.getAttribute("image");
			image.setContent(clickedImage.getContent());
		}

		Window w = (Window) view;
		w.setWidth(image.getContent().getWidth() + "px");
		w.setHeight(image.getContent().getHeight() + 50 + "px");
		w.setPosition("center");
		w.appendChild(image);
		w.doModal();
	}

	@Command
	public void onCancel(
			@BindingParam("cmp")
			Window win) {
		win.detach();
	}
}
