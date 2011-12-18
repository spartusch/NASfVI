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

/**
 * Sets a history token as used in NASfVI.
 * @see {@link com.google.gwt.user.client.History History}
 * @see {@link MainWidget#onValueChange}
 * @author Stefan Partusch
 *
 */
public interface HistoryStateSetter {
	/**
	 * Sets a history token encoding a query and an offset. The token's
	 * format is an {@link
	 * com.google.gwt.http.client.URL#encodeQueryString URL-encoded}
	 * query followed by an ampersand and the offset.
	 * @param query Query to encode
	 * @param offset Offset to encode
	 */
	void setHistoryState(String query, int offset);
}
