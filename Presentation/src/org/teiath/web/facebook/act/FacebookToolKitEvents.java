package org.teiath.web.facebook.act;

import com.visural.common.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Konstantinos Efthymiou
 * Date: 2/5/2013
 * Time: 10:28 πμ
 * To change this template use File | Settings | File Templates.
 */
public class FacebookToolKitEvents {

	private static final String api_key = "618503234845255";
	private static final String secret = "6075e979091bc235b50b0273e2cd953b";

	// set this to your servlet URL for the authentication servlet/filter
	private static final String redirect_uri = "https://educult.teiath.gr/fbauthevents";
	/// set this to the list of extended permissions you want
	private static final String[] perms = new String[] {"publish_stream, offline_access"};

	public static String getAPIKey() {
		return api_key;
	}

	public static String getSecret() {
		return secret;
	}

	public static String getLoginRedirectURL() {
		return "https://www.facebook.com/dialog/oauth?client_id=" +
				api_key + "&redirect_uri=" +
				redirect_uri + "&scope=" + StringUtil.delimitObjectsToString(",", perms);
	}

	public static String getAuthURL(String authCode) {
		return "https://graph.facebook.com/oauth/access_token?client_id=" +
				api_key + "&redirect_uri=" +
				redirect_uri + "&client_secret=" + secret + "&code=" + authCode;
	}
}
