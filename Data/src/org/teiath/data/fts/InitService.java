package org.teiath.data.fts;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class InitService {

	public static Collection<String> stopWords = new ArrayList<>();

	public void initService() {
		ClassPathResource cpr = new ClassPathResource("org/teiath/data/fts/stopwords_el.txt");
		try {
			InputStream is = cpr.getInputStream();

			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String theString = writer.toString();

			StringTokenizer stringTokenizer = new StringTokenizer(theString, "\n\r");
			while (stringTokenizer.hasMoreTokens()) {
				stopWords.add(stringTokenizer.nextToken());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdownService() {
	}
}
