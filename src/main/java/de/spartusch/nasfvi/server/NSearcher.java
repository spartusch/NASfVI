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

package de.spartusch.nasfvi.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.similar.MoreLikeThis;

/**
 * Searches the index with a {@link NQuery}.
 * @author Stefan Partusch
 *
 */
public class NSearcher {
	private static final Logger LOGGER =
		Logger.getLogger(NSearcher.class.getName());

	/** Names of fields to perform similarity searches on. */
	private static final String[] SIMILARITY_FIELDS =
		new String[]{"titel", "beschreibung"};
	/** IndexSearcher that forms the basis of this searcher. */
	private final IndexSearcher searcher;

	/**
	 * Creates a new NSearcher based on a Lucene IndexSearcher.
	 * @param searcher IndexSearcher to form the basis of the new NSearcher
	 */
	public NSearcher(final IndexSearcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * Gets the values of fields required for creating an answer in
	 * natural language.
	 * @param nquery NQuery used to retrieve <code>result</code>
	 * @param result Documents matching <code>nquery</code>
	 * @param offset Index of the first document to process
	 * @return A mapping from the names of fields to sets of extracted values
	 * @throws IOException if there is an IOException when accessing the index
	 */
	public final Map<String, Set<String>> getAnswerValues(final NQuery nquery,
			final TopDocs result, int offset) throws IOException {
		Map<String, Set<String>> values = new HashMap<String, Set<String>>();
		Set<String> answerFields = nquery.getFieldsToAnswer();

		if (result.totalHits == 0 || answerFields.size() == 0) {
			return values;
		}
		
		if (offset >= result.scoreDocs.length) {
			offset = result.scoreDocs.length - 1;
		}

		String firstField = answerFields.iterator().next();

		if (answerFields.size() == 1 && !NQuery.isFieldToCollapse(firstField)) {
			// Aggregate all ScoreDocs (1 field, n documents)
			Set<String> hs = new HashSet<String>(result.scoreDocs.length);
			for (ScoreDoc sd : result.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				hs.addAll(extractValues(nquery, doc, firstField));
			}
			values.put(firstField, hs);
		} else {
			// Process first ScoreDoc only (n fields, 1 document)
			Document doc = searcher.doc(result.scoreDocs[offset].doc);
			for (String field : answerFields) {
				values.put(field, extractValues(nquery, doc, field));
			}
		}

		return values;
	}

	/**
	 * Extracts a field's values from a document. This method is aware of
	 * <i>collapsed</i> or <i>merged</i> fields and handles them properly. 
	 * @param nquery NQuery used for searching
	 * @param doc Document to extract the field's values from
	 * @param field Name of the field to extract values for
	 * @return Set of extracted values
	 */
	private Set<String> extractValues(final NQuery nquery, final Document doc,
			final String field) {
		Set<String> values = new HashSet<String>();

		if (NQuery.isFieldToCollapse(field)) {
			// process merged field
			String mfield = NQuery.getMergedField();
			QueryScorer scorer = new QueryScorer(nquery.getQuery(), mfield);
			Highlighter highlighter = new Highlighter(scorer);
			highlighter.setTextFragmenter(new NullFragmenter());

			try {
				Set<String> buffer = new HashSet<String>();

				for (Fieldable f : doc.getFieldables(mfield)) {
					String content = f.stringValue();
					String value =
						normalizeValue(NQuery.extractValue(field, content));

					// Test if the field was matched by the query
					TokenStream ts = TokenSources.getTokenStream(mfield,
							content, nquery.getAnalyzer());
					if (highlighter.getBestFragment(ts, content) != null) {
						values.add(value); 
					} else {
						// Buffer the value - in case no field matches
						buffer.add(value);
					}
				}
				
				if (values.isEmpty()) {
					// No field was matched by the query
					values.addAll(buffer);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (InvalidTokenOffsetsException e) {
				throw new RuntimeException(e);
			}
		} else {
			for (String v : doc.getValues(field)) {
				values.add(normalizeValue(v));
			}
		}

		return values;
	}

	/**
	 * Normalizes strings. This implementation normalizes whitespace characters.
	 * @param value String to normalize
	 * @return Normalized string
	 */
	private String normalizeValue(final String value) {
		return value.replaceAll("\\p{Z}+", " ").trim();
	}

	/**
	 * Searches the index using a Lucene query.
	 * @param query Query to search for
	 * @param maxHits Maximum number of documents to search for
	 * @return Matching documents
	 * @throws IOException if there is an IOException when accessing the index
	 */
	private TopDocs search(final Query query,
			final int maxHits) throws IOException {
		TopDocs result = searcher.search(query, maxHits);
		LOGGER.info("Search: " + query + ";\t(Hits: " + result.totalHits + ")");
		return result;
	}

	/**
	 * Searches the index using a {@link NQuery}.
	 * @param nquery Query to search for
	 * @param offset Offset to use for the search
	 * @return Matching documents
	 * @throws IOException if there is an IOException when accessing the index
	 */
	public final TopDocs search(final NQuery nquery,
			final int offset) throws IOException {
		Query q = nquery.getQuery();

		if (nquery.hasSimilarityQuery()) {
			Query similQuery = nquery.getSimilarityQuery();
			TopDocs similDocs = search(similQuery, 1);

			if (similDocs.totalHits == 0) {
				return new TopDocs(0, new ScoreDoc[0], 0f);
			}

			int similDocNum = similDocs.scoreDocs[0].doc;
			String similId = searcher.doc(similDocNum).get("id");
			Query exclude = new TermQuery(new Term("id", similId));
			// exclude the document compared to

			MoreLikeThis mlt = new MoreLikeThis(searcher.getIndexReader());
			mlt.setFieldNames(SIMILARITY_FIELDS);
			Query moreLikeQuery = mlt.like(similDocNum);

			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(q, BooleanClause.Occur.MUST);
			booleanQuery.add(moreLikeQuery, BooleanClause.Occur.MUST);
			booleanQuery.add(exclude, BooleanClause.Occur.MUST_NOT);

			q = booleanQuery;
		}

		return search(q, offset + 5); // return top 5 results
	}

	/**
	 * Creates a JSON representation of a {@link NQuery} and the results
	 * of a search.
	 * @param nquery NQuery to include in the JSON representation
	 * @param result Search results to include
	 * @param offset Offset to include
	 * @return JSON representation including <code>nquery</code>,
	 * <code>result</code> and <code>offset</code>
	 */
	public final String toJson(final NQuery nquery, final TopDocs result,
			final int offset) {
		StringBuilder sb = new StringBuilder();
		// {
		// "NQuery": nquery,
		// "Offset": offset,
		// "Hits": totalHits
		// }
		
		sb.append("{\n\"NQuery\": ").append(nquery.toString()).append(",\n");
		sb.append("\"Offset\": ").append(offset).append(",\n");
		sb.append("\"Hits\": ").append(result.totalHits).append("\n}");

		return sb.toString();
	}
}
