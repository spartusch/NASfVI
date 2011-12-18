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

package de.spartusch.nasfvi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * EntryPoint of the client.
 * @author Stefan Partusch
 *
 */
public class Main extends Widget implements EntryPoint {
	/** URL to retrieve suggestions. */
	private static final String SUGGESTIONS_URL =
		GWT.getHostPageBaseURL() + "suggest?q=";
	/** URL to retrieve parsing results. */
	private static final String PARSE_URL =
		GWT.getHostPageBaseURL() + "parse?q=";

	/** Some examples for queries. */
	private static final String[] EXAMPLES = {
		"Wann fand im Raum 1.05 Semantik statt?",
		"Wann wird Syntax von wem gehalten?",
		"Wann wurde Syntax montags gehalten?",
		"Wo fand am Montag Syntax statt?",
		"Wo fand in welchem Semester Syntax statt?",
		"Wer h\u00e4lt welches Seminar \u00fcber Grammatikimplementierung?",
		"Wer hielt in welchem Semester etwas \u00fcber Implementierungen?",
		"Wer hielt was im Wintersemester 2005/2006?",
		"Was wurde im Sommersemester 2008 oder im Wintersemester 2007/08"
			+ " von Herrn Schulz gehalten?",
		"Was ist von Herrn Lei\u00df oder Herrn Schulz im Semester 2006 gehalten"
			+ " worden?",
		"Was ist von Herrn Lei\u00df \u00fcber Agenten gehalten worden?",
		"Was ist von Herrn Lei\u00df und Herrn Hadersbeck gehalten worden?",
		"Wird etwas \u00fcber HPSG gehalten?",
		"Welche Hauptseminare finden statt?",
		"Welche Veranstaltungen h\u00e4lt Herr Lei\u00df?",
		"Welche Seminare haben im Raum 1.14 stattgefunden?",
		"Welche Kurse sind im Raum 1.13 von welchen Dozenten wann montags"
			+ " gehalten worden?",
		"Welche Vorlesungen handeln von Prolog?",
		"Welche Vorlesung \u00e4hnelt dem Proseminar Syntax?",
		"Welches Seminar \u00e4hnelt einer Veranstaltung \u00fcber Prolog?",
		"Von wem war Semantik gehalten worden?",
		"In welchem Semester wurde etwas \u00fcber Unix gegeben?",
		"Gibt es Herrn Schulz?",
		"Gibt es ein Seminar \u00fcber HPSG?",
		"Hat es eine Veranstaltung \u00fcber HPSG gegeben?",
		"Hat ein Seminar \u00fcber Logik stattgefunden?",
		"Herr Guenthner h\u00e4lt ein Hauptseminar \u00fcber \"Lokale Grammatiken\"?",
		"Herr Schulz h\u00e4lt ein Hauptseminar \u00fcber \"Information Retrieval\"?",
		"Syntax findet wann statt?",
		"\u00c4hnelt ein Hauptseminar einem Proseminar \u00fcber Prolog?"
	};

	@Override
	public final void onModuleLoad() {
		MainWidget main = new MainWidget(EXAMPLES, SUGGESTIONS_URL, PARSE_URL);
		History.addValueChangeHandler(main);
		RootPanel.get("content").add(main);

		String state = Window.Location.getHash();
		if (state != null) {
			String parts[] = state.split("&");
			if (parts.length == 2) {
				main.setHistoryState(URL.decodeQueryString(parts[0]),
						Integer.valueOf(parts[1]));
			}
		}
	}

	/**
	 * Displays a {@link MessageBox}.
	 * @param caption Caption of the MessageBox or null
	 * @param htmlText HTML code to use as the content of the MessageBox or null
	 */
	public static void displayError(String caption, String htmlText) {

		if (caption == null || caption.isEmpty()) {
			caption = "Fehler";
		}

		if (htmlText == null || htmlText.isEmpty()) {
			htmlText = "Ein unbekannter Fehler ist aufgetreten.";
		}

		new MessageBox(caption, new HTML(htmlText));
	}
}
