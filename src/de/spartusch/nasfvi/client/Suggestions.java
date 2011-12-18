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

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Provides suggestions to a {@link com.google.gwt.user.client.ui.SuggestBox
 * SuggestBox} by extending {@link com.google.gwt.user.client.ui.SuggestOracle
 * SuggestOracle}. This class queries the server to retrieve suggestions using
 * {@link SuggestRequest SuggestRequests}.
 * @author Stefan Partusch
 * @see {@link de.spartusch.nasfvi.server.Suggestlet Suggestlet}
 *
 */
public class Suggestions extends SuggestOracle {
	/**
	 * Parses a JSON string to provide its value as a single suggestion.
	 * @author Stefan Partusch
	 *
	 */
	protected static class Sentence implements SuggestOracle.Suggestion {
		private String sentence;

		/**
		 * Parses a JSON string and creates a new suggestion using its value.
		 * @param json JSON string to parse
		 */
		public Sentence(final JSONValue json) {
			sentence = json.isString().stringValue();
		}

		@Override
		public final String getDisplayString() {
			return sentence;
		}

		@Override
		public final String getReplacementString() {
			return sentence;
		}
	}

	/** URL to query suggestions from. */
	private String url;
	/** The current request for suggestions. */
	private SuggestRequest suggestRequest;

	/**
	 * @param url The url to query suggestions from
	 */
	public Suggestions(final String url) {
		this.url = url;
	}

	@Override
	public final void requestSuggestions(final SuggestOracle.Request soRequest,
			final SuggestOracle.Callback soCallback) {
		if (suggestRequest != null) {
			suggestRequest.cancel();
		}

		if (soRequest.getQuery().trim().contains(" ")) {
			// Test for minimum of two words
			suggestRequest = new SuggestRequest(url, soRequest, soCallback);
			suggestRequest.schedule(400);
		} else {
			suggestRequest = null;
		}
	}

	@Override
	public final boolean isDisplayStringHTML() {
		return false;
	}
}
