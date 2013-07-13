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

import java.util.ArrayList;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * A request for suggestions from the server. When run this class requests
 * suggestions from the server and provides the suggestions to a {@link
 * com.google.gwt.user.client.ui.SuggestOracle SuggestOracle}.
 * @author Stefan Partusch
 * @see {@link Suggestions}
 *
 */
public class SuggestRequest extends Timer implements RequestCallback {
	private String url;
	private SuggestOracle.Request soRequest;
	private SuggestOracle.Callback soCallback;

	/**
	 * Creates a new request for suggestions from the server.
	 * @param url URL to request suggestions from
	 * @param soRequest Query to request suggestions for
	 * @param soCallback Callback to provide retrieved suggestions
	 * to a SuggestOracle
	 */
	public SuggestRequest(final String url,
			final SuggestOracle.Request soRequest,
			final SuggestOracle.Callback soCallback) {
		this.url = url;
		this.soRequest = soRequest;
		this.soCallback = soCallback;
	}

	@Override
	public final void run() {
		try {
			String getUrl = url + URL.encodeQueryString(soRequest.getQuery());
			RequestBuilder builder =
				new RequestBuilder(RequestBuilder.GET, getUrl);
			builder.setCallback(this);
			builder.send();
		} catch (RequestException e) {
			Main.displayError(e.getLocalizedMessage(), e.toString());
		}
	}

	@Override
	public final void onError(final Request request,
			final Throwable exception) {
		Main.displayError(exception.getLocalizedMessage(),
				exception.toString());
	}

	@Override
	public final void onResponseReceived(final Request request,
			final Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			ArrayList<Suggestions.Sentence> suggestions =
				new ArrayList<Suggestions.Sentence>();

			JSONArray arr = JSONParser.parseLenient(response.getText())
				.isArray();
			JSONArray sentences = arr.get(1).isArray();

			for (int i = 0; i < sentences.size(); i++) {
				suggestions.add(new Suggestions.Sentence(sentences.get(i)));
			}

			soCallback.onSuggestionsReady(soRequest,
					new SuggestOracle.Response(suggestions));
		}
	}
}
