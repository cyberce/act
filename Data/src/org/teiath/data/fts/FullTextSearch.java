package org.teiath.data.fts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.StringTokenizer;

public class FullTextSearch {

	public Collection<String> transformKeyword(String keyword) {
		Collection<String> res = new ArrayList<>();

		keyword = keyword.toLowerCase(Locale.getDefault());
		keyword = this.mapCharacters(keyword, false);

		StringTokenizer stringTokenizer = new StringTokenizer(keyword, " ");
		String token = null;
		while (stringTokenizer.hasMoreTokens()) {
			token = stringTokenizer.nextToken();
			if (! isStopWord(token)) {
				res.add(token);
			}
		}
		System.out.println();

		return res;
	}

	public String mapCharacters(String keyword, boolean ommitFinalS) {

		keyword = keyword.replaceAll("ά", "α");
		keyword = keyword.replaceAll("έ", "ε");
		keyword = keyword.replaceAll("ή", "η");
		keyword = keyword.replaceAll("ί", "ι");
		keyword = keyword.replaceAll("ό", "ο");
		keyword = keyword.replaceAll("ύ", "υ");
		keyword = keyword.replaceAll("ώ", "ω");
		keyword = keyword.replaceAll("ϊ", "ι");
		keyword = keyword.replaceAll("ϋ", "υ");
		if (ommitFinalS) {
			keyword = keyword.replaceAll("ς", "");
		} else {
			keyword = keyword.replaceAll("ς", "σ");
		}
		keyword = keyword.replaceAll("!", "");
		keyword = keyword.replaceAll("@", "");
		keyword = keyword.replaceAll("#", "");
		keyword = keyword.replaceAll("$", "");
		keyword = keyword.replaceAll("%", "");
		keyword = keyword.replaceAll("^", "");
		keyword = keyword.replaceAll("&", "");
		keyword = keyword.replaceAll("\\Q*", "");
		keyword = keyword.replaceAll("\\Q(", "");
		keyword = keyword.replaceAll("\\Q)", "");
		keyword = keyword.replaceAll("\\Q+", "");
		keyword = keyword.replaceAll("=", "");
		keyword = keyword.replaceAll("\\Q{", "");
		keyword = keyword.replaceAll("}", "");
		keyword = keyword.replaceAll("\\Q[", "");
		keyword = keyword.replaceAll("]", "");
		keyword = keyword.replaceAll("\\Q\\", "");
		keyword = keyword.replaceAll("\\Q|", "");
		keyword = keyword.replaceAll(";", "");
		keyword = keyword.replaceAll(":", "");
		keyword = keyword.replaceAll("'", "");
		keyword = keyword.replaceAll("\"", "");
		keyword = keyword.replaceAll("/", "");
		keyword = keyword.replaceAll("<", "");
		keyword = keyword.replaceAll(">", "");
		keyword = keyword.replaceAll("\\Q.", "");
		keyword = keyword.replaceAll(",", "");
		keyword = keyword.replaceAll("\\Q?", "");
		keyword = keyword.replaceAll("~", "");
		keyword = keyword.replaceAll("`", "");
		keyword = keyword.replaceAll("΄", "");

		return keyword;
	}

	private boolean isStopWord(String keyword) {
		return InitService.stopWords.contains(keyword);
	}
}
