/*
 * Copyright 2011 Stefan Partusch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.spartusch.nasfvi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.spartusch.StringMethods;

/**
 * Extracts data of events from the university calendar of the University of
 * Munich. This data may be ingested into the
 * {@link de.spartusch.nasfvi.server.XmlIndex index}. This class is designed
 * as a stand-alone application. Start it from the command line and feed it
 * URLs pointing directly to the details of an event. The URLs are read from
 * the standard input. The extracted data is written to the standard output.
 * <p>The XML format written by this class is like this:</p>
 * <pre>
 * &lt;veranstaltung>
 * 	&lt;semester>...&lt;/semester>
 * 	&lt;titel>...&lt;/titel>
 * 	&lt;dozent>...&lt;/dozent>
 * 	&lt;typ>...&lt;/typ>
 * 	&lt;termin>...&lt;/termin>
 * 	&lt;beschreibung>...&lt;/beschreibung>
 * &lt;/veranstaltung>
 * </pre> 
 * @author Stefan Partusch
 * @see <a href="https://lsf.verwaltung.uni-muenchen.de/qisserver/rds?state=user&type=0">University calendar, University of Munich</a>
 *
 */
public final class EventExtractor {
	/** Default charset for webpages and for output. */
	private static final String DEFAULT_CHARSET = "UTF-8";
	/** Regular expression to extract the type, title, semester and
	 * description of the event. */
	private static final Pattern FIELDS =
		Pattern.compile("<th[^>]*>(\\p{L}+)</th>\\s*<td[^>]*>(.+?)</td",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	/** Regular expression to split the value of the semester field. */
	private static final Pattern SEMESTER =
		Pattern.compile("^(WS|SS) (\\d\\d)(\\d\\d)$");
	/** A map from the name of a field to the corresponding XML tag. */
	private static final Map<String, String> FIELDS_TO_TAGS;
	static {
		FIELDS_TO_TAGS = new HashMap<String, String>();
		FIELDS_TO_TAGS.put("Veranstaltungsart", "typ");
		FIELDS_TO_TAGS.put("Langtext", "titel");
		FIELDS_TO_TAGS.put("Semester", "semester");
		FIELDS_TO_TAGS.put("Kommentar", "beschreibung");
	}

	private EventExtractor() {
		throw new AssertionError();
	}

	/**
	 * Loads the content of an URL using a HTTP GET request.
	 * @param url URL to load
	 * @return Content of <code>url</code>
	 * @throws IOException If some IO problem occurs
	 */
	private static String load(final String url) throws IOException {
		HttpURLConnection conn =
			(HttpURLConnection) new URL(url).openConnection();

		conn.setDoInput(true);
		conn.setDoOutput(false);
		conn.setRequestMethod("GET");
		conn.connect();

		int respCode = conn.getResponseCode();
		if (respCode != HttpURLConnection.HTTP_OK) {
			throw new IOException(conn.getResponseMessage());
		}

		String contentType =
			conn.getHeaderField("Content-Type").toUpperCase(Locale.ENGLISH);
		String csName = DEFAULT_CHARSET;
		int csPos = contentType.lastIndexOf("CHARSET=");
		if (csPos != -1) {
			csName = contentType.substring(csPos + "CHARSET=".length()).trim();
		}
		Charset charset = Charset.forName(csName);

		InputStream is = conn.getInputStream();
		if (is == null) {
			throw new IOException("No input stream");
		}
		InputStreamReader isr = new InputStreamReader(is, charset);
		StringBuilder sb = new StringBuilder();
		try {
			int c;
			while ((c = isr.read()) != -1) {
				sb.appendCodePoint(c);
			}
		} finally {
			isr.close();
		}

		return sb.toString();
	}

	/**
	 * Extracts the type, title, semester and description of the event. The
	 * extracted values and corresponding tags are added to the StringBuilder.
	 * @param xml StringBuilder to add the extracted data to
	 * @param input Source code of the webpage
	 */
	private static void addFields(StringBuilder xml, final String input) {
		Matcher m = FIELDS.matcher(input);

		while (m.find()) {
			String tag = FIELDS_TO_TAGS.get(m.group(1));

			if (tag != null) {
				String value = m.group(2);

				// test for semester
				Matcher semMatcher = SEMESTER.matcher(value);
				if (semMatcher.find()) {
					if (semMatcher.group(1).equals("WS")) {
						value = "20" + semMatcher.group(2)
							+ "/20" + semMatcher.group(3);
					} else {
						value = semMatcher.group(2) + semMatcher.group(3);
					}
				}

				addTag(xml, tag, value);
			}
		}
	}

	/**
	 * Extracts the names of the lecturers of the event. The extracted values
	 * and corresponding tags are added to the StringBuilder.
	 * @param xml StringBuilder to add the extracted data to
	 * @param input Source code of the webpage
	 * @throws IOException If an IO exception occurs
	 */
	private static void addLecturers(StringBuilder xml,
			final String input) throws IOException {
		String table = StringMethods.getDelimitedSubstring(input,
				"<table summary=\"Verantwortliche Dozenten\">",
				"</table>");
		BufferedReader br = new BufferedReader(
				new StringReader(StringMethods.stripHTML(table, false)));

		String line;
		boolean first = true; // ignore the caption
		while ((line = br.readLine()) != null) {
			if (first) {
				first = false;
			} else {
				addTag(xml, "dozent", line);
			}
		}
	}

	/**
	 * Extracts the dates and rooms of the event. The extracted values and
	 * corresponding tags are added to the StringBuilder.
	 * @param xml StringBuilder to add the extracted data to
	 * @param input Source code of the webpage
	 */
	private static void addDates(StringBuilder xml, final String input) {
		String table = StringMethods.getDelimitedSubstring(input,
				"<table summary=\"Übersicht über alle Veranstaltungstermine\">",
				"</table>");
		
		if(table == null)
			return;
		
		String[] rows = table.split("</?tr>");
		
		for (int i = 2; i < rows.length; i++) {
			// ignore first row
			String[] cells = rows[i].split("</?td>");
			
			if (cells.length > 4) {
				addTag(xml, "termin", cells[0] + "<br>" + cells[4]);
			}
		}
	}

	/**
	 * Adds a tag to the StringBuilder.
	 * @param xml StringBuilder to add the tag to
	 * @param name Name of the tag
	 * @param value Value of the tag
	 */
	private static void addTag(StringBuilder xml, final String name,
			final String value) {
		String v = StringMethods.stripHTML(value, true);
		
		v = v.replace("&", "&amp;");
		v = v.replace("<", "&lt;").replace(">", "&gt;");
		v = v.replaceAll("\\s+", " ");

		xml.append("\t<").append(name).append(">");
		xml.append(v.trim());
		xml.append("</").append(name).append(">\n");
	}

	/**
	 * Extracts all relevant data from the event.
	 * @param input Source code of the webpage with all details of the event
	 * @return Extracted data in the required XML format
	 * @throws IOException If an IO error occurs
	 */
	private static String parse(final String input) throws IOException {
		StringBuilder xml = new StringBuilder("<veranstaltung>\n");
		addFields(xml, input);
		addLecturers(xml, input);
		addDates(xml, input);
		return xml.append("</veranstaltung>").toString();
	}

	/**
	 * Entry point of the stand-alone application.
	 * @param args Not used
	 * @throws IOException If an IO error occurs
	 */
	public static void main(final String[] args) throws IOException {
		BufferedReader in =
			new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = new PrintStream(System.out, true, DEFAULT_CHARSET);

		String line;
		while ((line = in.readLine()) != null) {
			out.println(parse(load(line)));
		}
	}
}
