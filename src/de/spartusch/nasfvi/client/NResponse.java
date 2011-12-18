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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

/**
 * A parsed response from the server. The server returns its results in JSON
 * format which can be parsed as objects. Server responses for queries can be
 * parsed as objects of this class. Some convenience methods are provided, too.
 * @author Stefan Partusch
 * @see {@link de.spartusch.nasfvi.server.Parselet Parselet}
 *
 */
public class NResponse extends JavaScriptObject {
	protected NResponse() {}

	/**
	 * Parses JSON data to create a new object of this class.
	 * @param json JSON to parse
	 * @return A new NResponse
	 */
	public static NResponse parse(final String json) {
		JSONArray array = JSONParser.parseLenient(json).isArray();

		if (array == null || array.size() != 2) {
			throw new IllegalArgumentException();
		}

		return (NResponse) array.getJavaScriptObject();
	}

	/**
	 * Tests if the request was successful. A request is considered as
	 * successful if it results in at least one hit or if no fields were
	 * requested.
	 * @return true if the request is considered as successful, false otherwise
	 */
	public final boolean isSuccess() {
		if (getHits() > 0) {
			return true;
		}
		// accept no hits only if no fields are requested (-> answer = "No.")
		return getFields().length() == 0;
	}

	/**
	 * Tests if the response is aggregated. A response is aggregated when the
	 * server collapses several documents into one result. 
	 * @return true if the response is aggregated, false otherwise
	 * @see {@link de.spartusch.nasfvi.server.NSearcher#getAnswerValues
	 * NSearcher}
	 */
	public final boolean isAggregated() {
		return getFields().length() == 1;
	}

	/**
	 * Returns a more elaborated version of the German answer.
	 * @return Elaborated version of the German answer
	 */
	public final String getAnswer() {
		String answer = getPlainAnswer().trim();

		if (getFields().length() == 0) { // is statement
			if (getHits() > 0) {
				answer = "Ja. " + answer;
			} else {
				answer = "Nein. " + answer;
				if (answer.endsWith(" statt")) {
					answer = answer.replaceAll(" statt$", " nicht statt");
				} else {
					answer = answer + " nicht";
				}
			}
		}

		return answer + ". ";
	}

	/**
	 * Returns the query used for searching the index.
	 * @return Query used for searching the index
	 */
	public final native String getQuery() /*-{
		return this[0].NQuery.Query;
	}-*/;

	/**
	 * Returns the similarity query used for restricting the query.
	 * @return Similarity query used for restricting the query
	 */
	public final native String getSimilarityQuery() /*-{
		return this[0].NQuery.SQuery;
	}-*/;

	/**
	 * Returns the name of the fields requested by the natural language query.
	 * @return Names of the fields requested by the natural language query
	 */
	public final native JsArrayString getFields() /*-{
		return this[0].NQuery.Fields;
	}-*/;

	/**
	 * Returns the current offset used for the matching documents.
	 * @return Offset of the documents processed in this response
	 */
	public final native int getOffset() /*-{
		return this[0].Offset;
	}-*/;

	/**
	 * Returns the number of hits the {@link #getQuery() query} resulted in. 
	 * @return Number of hits
	 */
	public final native int getHits() /*-{
		return this[0].Hits;
	}-*/;

	/**
	 * Returns the German answer strictly as returned by the server. You should
	 * always prefer {@link #getAnswer() getAnswer()} over this method.
	 * @return German answer as returned by the server 
	 */
	public final native String getPlainAnswer() /*-{
		return this[1].Answer;
	}-*/;

	/**
	 * Returns the linguistic analysis of the German request as returned by
	 * the server.
	 * @return Linguistic analysis of the German request
	 */
	public final native JsArrayMixed getAnalysisRequest() /*-{
		return this[1].AnalysisReq;
	}-*/;

	/**
	 * Returns the linguistic analysis of the German answer as returned by the
	 * server. Modifications of {@link #getAnswer() getAnswer()} are not
	 * reflected in this analysis.
	 * @return Linguistic analysis of the German answer
	 */
	public final native JsArrayMixed getAnalysisAnswer() /*-{
		return this[1].AnalysisAns;
	}-*/;
}
