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
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to visualize {@link NResponse} objects.
 * @author Stefan Partusch
 *
 */
public class NResponseWidget extends Composite {
	interface MyUiBinder extends UiBinder<Widget, NResponseWidget> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	/**
	 * Labels to use when creating {@link
	 * com.google.gwt.user.client.ui.TreeItem TreeItems} for the analyses.
	 * The dimensions resemble the position of each subtree in the server's
	 * response. If a label is null the text content of the subtree's first
	 * child is used as a label. 
	 */
	private static final String[][] LABELS = {
			{"Verbinformationen", "Felderstruktur", "Semantik"},
			{"Vorfeld", "Mittelfeld"},
			{null}, // label of each phrase
			{"Formmerkmale", "Token"},
			{null} // label of each token
	};

	@UiField InlineLabel query;
	@UiField InlineLabel similQuery;
	@UiField InlineLabel hits;
	@UiField InlineLabel fields;
	@UiField InlineLabel answer;
	@UiField Anchor link;
	@UiField Tree analysisReq;
	@UiField Tree analysisAns;

	/** HistoryStateSetter to use for setting the offset value. */
	private HistoryStateSetter stateSetter;
	/** Offset to set when the "more" link is clicked. */
	private int nextOffset;

	/**
	 * Creates a visual representation of the response. This widget includes
	 * a link to request more results. This is achieved through
	 * <code>stateSetter</code>.
	 * @param response Response to visualize
	 * @param stateSetter HistoryStateSetter to use for setting new
	 * offset values
	 */
	public NResponseWidget(final NResponse response,
			final HistoryStateSetter stateSetter) {
		this.stateSetter = stateSetter;

		if (response.isAggregated()) {
			nextOffset = response.getOffset() + 5;
		} else {
			nextOffset = response.getOffset() + 1;
		}

		initWidget(uiBinder.createAndBindUi(this));

		answer.setText(response.getAnswer());
		query.setText(response.getQuery());
		hits.setText(String.valueOf(response.getHits()));

		if (response.getFields().length() == 0) {
			fields.setText("keine");
			link.setVisible(false);
		} else {
			fields.setText(response.getFields().toString());
			link.setVisible(nextOffset < response.getHits());
		}

		if (response.getSimilarityQuery().isEmpty()) {
			similQuery.setText("keine");
		} else {
			similQuery.setText(response.getSimilarityQuery());
		}

		analysisReq.addItem(getAnalysis(response.getAnalysisRequest(),
				"Analyse der Anfrage"));
		analysisAns.addItem(getAnalysis(response.getAnalysisAnswer(),
				"Analyse der Antwort"));
	}

	@UiHandler("link")
	protected final void nextResult(final ClickEvent event) {
		stateSetter.setHistoryState(null, nextOffset);
	}

	/**
	 * Creates and returns the visual representation of an analysis.
	 * @param data Data of the analysis
	 * @param text Label to set for the analysis
	 * @return Visual representation of <code>data</code>
	 */
	private TreeItem getAnalysis(final JsArrayMixed data, final String text) {
		TreeItem root = getSubtree(new JSONArray(data), 0);
		root.setText(text);
		
		return root;
	}

	/**
	 * Creates and returns a visual representation for a subtree.
	 * @param data Data of the subtree
	 * @param depth Depth of the subtree in the analysis
	 * @return Visual representation of <code>data</code>
	 */
	private TreeItem getSubtree(final JSONArray data, final int depth) {
 		TreeItem root = new TreeItem();
 		int subtrees = 0;

 		for (int i = 0; i < data.size(); i++) {
 			JSONArray array = data.get(i).isArray();

 			if (array == null) {
 				// just a string
 				root.addItem(data.get(i).isString().stringValue());
 			} else if (array.size() == 1 && array.get(0).isString() != null) {
 				// array with a single string as an element
 				root.addItem(array.get(0).isString().stringValue());
 				subtrees++;
 			} else {
 	 			// arrays
 				TreeItem item = getSubtree(array, depth + 1);

 				int x = (depth < LABELS.length) ? depth : LABELS.length - 2;
 				int y = (subtrees < LABELS[x].length)
 					? subtrees : LABELS[x].length - 1; 

 				String label = LABELS[x][y];

 				if (label == null && item.getChildCount() > 0) {
 					TreeItem first = item.getChild(0);
 					label = first.getText();
 					item.removeItem(first);
 				}

 				item.setText(label);
 				root.addItem(item);
 				subtrees++;
 			}
 		}

		return root;
	}
}
