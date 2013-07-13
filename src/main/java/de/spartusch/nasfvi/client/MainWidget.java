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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * The main widget of the client containing most of the UI. This class
 * implements the {@link HistoryStateSetter} interface and queries are only
 * sent to the server when a history token is set using this interface.
 * Suggestions on the other hand are processed automatically whenever the
 * user enters text in the widget's <code>SuggestBox</code>.
 * @author Stefan Partusch
 *
 */
public class MainWidget extends Composite implements RequestCallback,
	ValueChangeHandler<String>, HistoryStateSetter, KeyUpHandler {
	interface MyUiBinder extends UiBinder<Widget, MainWidget> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	/** Regular expression to detect placeholders in queries. */
	private static final String PLACEHOLDERS = ".*\\([A-Z][a-z]+\\).*";
	/** Examples of queries. */
	private final String[] examples;
	/** URL stubs to use. */
	private final String suggestionsUrl, parseUrl;

	@UiField SuggestBox suggestBox;
	@UiField SimplePanel responsePanel;
	@UiField DisclosurePanel hints;
	@UiField HTML exampleItems;

	/**
	 * @param examples Examples of queries to display in the main widget
	 * @param suggestionsUrl URL stub for retrieving suggestions
	 * @param parseUrl URL stub for parsing
	 */
	public MainWidget(final String[] examples, final String suggestionsUrl,
			final String parseUrl) {
		this.examples = examples;
		this.suggestionsUrl = suggestionsUrl;
		this.parseUrl = parseUrl;

		initWidget(uiBinder.createAndBindUi(this));

		setExamples();

		suggestBox.addKeyUpHandler(this);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				suggestBox.setFocus(true);
			}
		});
	}

	@UiFactory
	protected final SuggestBox createSuggestBox() {
		TextArea textArea = new TextArea();

		textArea.setCharacterWidth(50);
		textArea.setVisibleLines(4);

		return new SuggestBox(new Suggestions(suggestionsUrl), textArea);
	}

	/**
	 * Selects some examples randomly and displays them.
	 */
	private void setExamples() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			int index = Random.nextInt(examples.length);
			sb.append("<li>").append(examples[index]).append("</li>");
		}
		
		exampleItems.setHTML(sb.toString());
	}

	@UiHandler("moreExamples")
	protected final void moreExamplesClicked(final ClickEvent event) {
		setExamples();
	}

	@UiHandler("sendButton")
	protected final void sendButtonClicked(final ClickEvent event) {
		setHistoryState(null, 0);
	}

	/**
	 * Displays a message within the main widget.
	 * @param message Message to display
	 * @param cssClass CSS class to use for styling
	 * @see #clearMessage
	 */
	private void setMessage(final String message, final String cssClass) {
		String msg = message;

		if (cssClass != null && !cssClass.isEmpty()) {
			msg = "<span class='" + cssClass + "'>" + message + "</span>";
		}

		responsePanel.setWidget(new HTML(msg));
	}

	/**
	 * Clears a message displayed in the main widget.
	 * @see #setMessage
	 */
	private void clearMessage() {
		responsePanel.clear();
	}

	@Override
	public final void onError(final Request request, final Throwable exception) {
		Main.displayError(exception.getLocalizedMessage(),
				exception.toString());
	}

	@Override
	public final void onResponseReceived(final Request request,
			final Response response) {
		int sc = response.getStatusCode();
		String respText = response.getText();

		if (sc == Response.SC_OK) {
			NResponse nresp = NResponse.parse(respText);
			
			if (nresp.isSuccess()) {
				hints.setOpen(false);
				NResponseWidget widget = new NResponseWidget(nresp, this);
				responsePanel.setWidget(widget);
			} else {
				setMessage("Ihre Anfrage wurde verstanden und verarbeitet."
						+ " Doch das Vorlesungsverzeichnis enthält keine Daten"
						+ " zur Beantwortung der Anfrage.", null);
			}
		} else if (sc == Response.SC_BAD_REQUEST) {
			setMessage("Die Anfrage konnte nicht verarbeitet werden."
					+ " Bitte formulieren Sie um!", "error");
		} else {
			Main.displayError(response.getStatusText(), respText);
		}
	}

	@Override
	public final void setHistoryState(final String query, final int offset) {
		String q = query;

		if (q == null || q.isEmpty()) {
			q = suggestBox.getText();
		}

		History.newItem(URL.encodeQueryString(q) + "&"
				+ String.valueOf(offset), true);
	}
	
	@Override
	public final void onValueChange(final ValueChangeEvent<String> event) {
		String[] state = event.getValue().split("&", -1);

		if (state.length != 2) {
			throw new AssertionError();
		}

		String q = URL.decodeQueryString(state[0])
			.replaceAll("\\s+", " ").trim();
		String offset = state[1];

		suggestBox.setText(q);

		if (q.isEmpty()) {
			setMessage("Bitte geben Sie eine Anfrage in das Textfeld ein.",
					"error");
		} else if (q.matches(PLACEHOLDERS)) {
			setMessage("Bitte ersetzen Sie sämtliche Platzhalter durch"
					+ " konkrete Werte!", "error");
		} else {
			clearMessage();
			try {
				String getUrl = parseUrl + URL.encodeQueryString(q)
					+ "&offset=" + offset;
				RequestBuilder builder =
					new RequestBuilder(RequestBuilder.GET, getUrl);
				builder.setCallback(this);
				builder.send();
			} catch (RequestException e) {
				Main.displayError(e.getLocalizedMessage(), e.toString());
			}
		}
	}

	@Override
	public final void onKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			setHistoryState(null, 0);
		}
	}
}
