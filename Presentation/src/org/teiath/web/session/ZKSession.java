package org.teiath.web.session;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import java.io.Serializable;
import java.util.Map;

public class ZKSession {

	private static String WINDOW_ATTR_KEY = "_window-attributes_";
	private static String WINDOW_ATTR_PARAM_NAME = "ukeysession";

	public static void register() {
		// if we have a parameter in the URL, use it; else, create a new one
		String windowID = Executions.getCurrent().getParameter(WINDOW_ATTR_PARAM_NAME);
		if (windowID == null) {
			windowID = getNewWindowID();
		}
		Executions.getCurrent().getDesktop().setAttribute(WINDOW_ATTR_KEY, windowID);
	}

	private static String getNewWindowID() {
		long t = new java.util.Date().getTime();
		int r = (int) (Math.random() * 10000);
		return t + "" + r;
	}

	public static String getPWSParams() {
		return WINDOW_ATTR_PARAM_NAME + "=" + getCurrentPWSID();
	}

	public static void fireNewWindow(String destination) {
		Executions.getCurrent()
				.sendRedirect(destination + "?" + WINDOW_ATTR_PARAM_NAME + "=" + getNewWindowID(), "_blank");
	}

	public static void sendRedirect(String destination) {
		Executions.getCurrent().sendRedirect(destination + "?" + getPWSParams());
	}

	public static void sendRedirect(Execution e, String destination) {
		if (e != null) {
			e.sendRedirect(destination + "?" + getPWSParams());
		} else {
			Executions.getCurrent().sendRedirect(destination + "?" + getPWSParams());
		}
	}

	public static void sendRedirect(String destination, String context) {
		Executions.getCurrent().sendRedirect(destination + "?" + getPWSParams(), context);
	}

	public static void sendPureRedirect(String destination) {
		Executions.getCurrent().sendRedirect(destination);
	}

	public static void sendPureRedirect(String destination, String context) {
		Executions.getCurrent().sendRedirect(destination, context);
	}

	private static Map<String, Map<String, Serializable>> getAttributeMap() {
		Map<String, Map<String, Serializable>> attrMap;
		attrMap = (Map<String, Map<String, Serializable>>) Executions.getCurrent().getDesktop().getSession()
				.getAttribute(WINDOW_ATTR_KEY);
		if (attrMap == null) {
			attrMap = new java.util.HashMap<String, Map<String, Serializable>>();
			Executions.getCurrent().getDesktop().getSession().setAttribute(WINDOW_ATTR_KEY, attrMap);
		}
		return attrMap;
	}

	public static String getCurrentWinID() {
		Desktop d = Executions.getCurrent().getDesktop();
		String winID = (String) d.getAttribute(WINDOW_ATTR_KEY);
		if (winID == null) {
			register();
			winID = (String) d.getAttribute(WINDOW_ATTR_KEY);
		}
		return winID;
	}

	private static String getCurrentPWSID() {
		Desktop d = Executions.getCurrent().getDesktop();
		String winID = (String) d.getAttribute(WINDOW_ATTR_KEY);
		if (winID == null) {
			register();
			winID = (String) d.getAttribute(WINDOW_ATTR_KEY);
		}
		return winID;
	}

	private static Map<String, Serializable> getPWSAttributeMap(String winID) {
		Map<String, Map<String, Serializable>> attrMap = getAttributeMap();
		Map<String, Serializable> attrWindowMap = attrMap.get(winID);
		if (attrWindowMap == null) {
			attrWindowMap = new java.util.HashMap<String, Serializable>();
			attrMap.put(winID, attrWindowMap);
		}
		return attrWindowMap;
	}

	public static void setAttribute(String name, Serializable obj) {
		String winID = getCurrentPWSID();
		getPWSAttributeMap(winID).put(name, obj);
	}

	public static void removeAttribute(String name) {
		String winID = getCurrentPWSID();
		getPWSAttributeMap(winID).remove(name);
	}

	public static void removeAttribute(String winID, String name) {
		getPWSAttributeMap(winID).remove(name);
	}

	public static Serializable getAttribute(String name) {
		String winID = getCurrentPWSID();
		return getPWSAttributeMap(winID).get(name);
	}

	public static Serializable getAttribute(String winID, String name) {
		return getPWSAttributeMap(winID).get(name);
	}

	public static void invalidate() {
		String winID = getCurrentPWSID();
		Map<String, Map<String, Serializable>> attrMap = getAttributeMap();
		synchronized (Executions.getCurrent().getDesktop().getSession()) {
			if (attrMap != null) {
				if (attrMap.size() == 1 && attrMap.get(winID) != null) {
					attrMap.clear();
					Executions.getCurrent().getDesktop().getSession().invalidate();
				} else {
					attrMap.remove(winID);
				}
			}
		}
	}
}