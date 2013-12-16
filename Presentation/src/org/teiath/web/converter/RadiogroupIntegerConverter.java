package org.teiath.web.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import java.io.Serializable;

public class RadiogroupIntegerConverter
		implements Converter, Serializable {

	private static final long serialVersionUID = 200808191534L;

	@Override
	public Object coerceToUi(Object val, Component comp, BindContext bindContext) {
		if (val != null) {

			//iterate to find the selected radio via the value
			for (final Component child : comp.getChildren()) {
				if (child instanceof Radio) {
					if (val.toString().equals(((Radio) child).getValue())) {
						return child;
					}
				} else if (! (child instanceof Radiogroup)) { //skip nested radiogroup
					//bug 2464484
					final Object value = coerceToUi(val, child, bindContext); //recursive
					if (value != null) {
						return value;
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object coerceToBean(Object val, Component comp, BindContext bindContext) {
		return val != null ? Integer.parseInt(((Radio) val).getValue().toString()) : null;
	}
}
