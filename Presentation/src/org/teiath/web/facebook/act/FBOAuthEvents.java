package org.teiath.web.facebook.act;

import com.visural.common.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Konstantinos Efthymiou
 * Date: 2/5/2013
 * Time: 10:51 πμ
 * To change this template use File | Settings | File Templates.
 */
public class FBOAuthEvents
		implements Filter {

	public void init(FilterConfig fc)
			throws ServletException {
	}

	public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) sr;
		HttpServletResponse res = (HttpServletResponse) sr1;
		String code = sr.getParameter("code");
		String state = sr.getParameter("state");
		if (StringUtil.isNotBlankStr(code)) {
			String authURL = FacebookToolKitEvents.getAuthURL(code);
			URL url = new URL(authURL);
			try {
				String result = readURL(url);
				String accessToken = null;
				Integer expires = null;
				String[] pairs = result.split("&");
				for (String pair : pairs) {
					String[] kv = pair.split("=");
					if (kv.length != 2) {
						throw new RuntimeException("Unexpected auth response");
					} else {
						if (kv[0].equals("access_token")) {
							accessToken = kv[1];
						}
						if (kv[0].equals("expires")) {
							expires = Integer.valueOf(kv[1]);
						}
					}
				}
				if (accessToken != null && expires != null) {
					res.sendRedirect(
							"https://educult.teiath.gr/zul/social/facebookShareEvent.zul?token=" + accessToken + "&state=" +
									state);
				} else {
					throw new RuntimeException("Access token and expires not found");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private String readURL(URL url)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		int r;
		while ((r = is.read()) != - 1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	public void destroy() {
	}
}
