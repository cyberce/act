package org.teiath.web.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import java.io.Serializable;

public class RadiogroupBooleanConverter
		implements Converter, Serializable {

	private static final long serialVersionUID = - 7692059172828069699L;

	public Object coerceToUi(Object val, Component comp, BindContext bindContext) {
		if (val != null && val instanceof Boolean) {
			Boolean bval = (Boolean) val;

			//iterate to find the selected radio via the value
			for (final Component child : comp.getChildren()) {
				if (child instanceof Radio) {
					if (bval.toString().equalsIgnoreCase(((Radio) child).getValue().toString())) {
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

	public Object coerceToBean(Object val, Component comp, BindContext bindContext) { //save
		return val != null ? Boolean.parseBoolean(((Radio) val).getValue().toString()) : null;
	}
}
