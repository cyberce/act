package org.teiath.webservices.authentication;

import org.springframework.security.crypto.codec.Base64;

import java.io.UnsupportedEncodingException;

public class Utils {

	public static String[] decodeHeader(String authorization) {

		try {
			byte[] decoded = Base64.decode(authorization.substring(6).getBytes("UTF-8"));
			String credentials = new String(decoded);
			return credentials.split(":");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
