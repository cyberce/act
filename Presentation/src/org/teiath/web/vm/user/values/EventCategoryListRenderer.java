package org.teiath.web.vm.user.values;

import org.teiath.data.domain.act.EventCategory;
import org.zkoss.zul.*;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 23/9/2013
 * Time: 12:32 μμ
 * To change this template use File | Settings | File Templates.
 */
public class EventCategoryListRenderer
		implements TreeitemRenderer<DefaultTreeNode<EventCategory>> {

	@Override
	public void render(Treeitem treeitem, DefaultTreeNode<EventCategory> data, int i)
			throws Exception {

		EventCategory category = data.getData();
		Treerow tr = new Treerow();
		tr.appendChild(new Treecell(category.getName()));
		treeitem.appendChild(tr);
		treeitem.setOpen(true);
		treeitem.setTooltiptext(data.getData().getDescription());
	}
}
