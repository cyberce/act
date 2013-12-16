package org.teiath.web.servlet;

import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreviewEvent
		extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String code = request.getParameter("code");

		if (code != null) {
			Map map = (HashMap) request.getSession().getAttribute("_window-attributes_");

			if (map != null) {
				if (map.size() == 1) {
					Set keys = map.keySet();
					Map attrsMap;
					for (Object key : keys) {
						attrsMap = (HashMap) map.get(key);
						if (attrsMap.get("AUTH_USER") != null) {
							response.sendRedirect(request.getContextPath() + PageURL.ACTION_VIEW_SEARCH + "?ukeysession=" + key +  "&code=" + code);
						} else {
							request.getSession().setAttribute("EVENT_CODE", code);
							response.sendRedirect(request.getContextPath() + PageURL.INDEX);
						}
					}
				} else {
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter writer = response.getWriter();
					createHTMLMessage(writer,
							"Έχουν συνδεθεί στο σύστημα με τον ίδιο browser περισσότεροι από ένας χρήστες.<br/>Παρακαλώ συνδεθείτε με έναν χρήστη.");
				}
			} else {
				request.getSession().setAttribute("EVENT_CODE", code);
				response.sendRedirect(request.getContextPath() + PageURL.INDEX);
			}
		} else {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			createHTMLMessage(writer, "Πρόβλημα στις παραμέτρους κλήσης του module");
		}
	}

	private void createHTMLMessage(PrintWriter writer, String message) {
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title></title>");
		writer.println("</head>");
		writer.println("<body bgcolor=white>");

		writer.println("<table border=\"0\" cellpadding=\"10\">");
		writer.println("<tr>");
		writer.println("<td>");
		writer.println("<h1>" + message + "</h1>");
		writer.println("</td>");
		writer.println("</tr>");
		writer.println("</table>");
		writer.println("</body>");
		writer.println("</html>");
	}
}
