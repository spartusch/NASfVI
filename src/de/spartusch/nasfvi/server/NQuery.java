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

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.queryParser.standard.config.DefaultOperatorAttribute.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

import de.spartusch.StringMethods;

/**
 * A query for use with {@link NSearcher}.
 * @author Stefan Partusch
 *
 */
public class NQuery {
	/** The default search field in Lucene. */
	private static final String DEFAULT_SEARCH_FIELD = "titel";
	/** Fields in the original query to collapse. */
	private static final String[] FIELDS_TO_COLLAPSE =
		new String[]{"raum", "tag"};
	/** Name of the field to collapse fields to. */
	private static final String COLLAPSE_TO = "termin";
	/** Regular expression to extract day information
	 * from {@link collapseTo}. */
	private static final Pattern COLLAPSED_FIELD_TAG =
		Pattern.compile("(?:^| )?(mo|di|mi|do|fr|sa|so)\\b",
				Pattern.CASE_INSENSITIVE);
	/** Regular expression to extract location information
	 * from {@link collapseTo}. */
	private static final Pattern COLLAPSED_FIELD_RAUM =
		Pattern.compile("(Rechnerraum(?: \\w+)?)|Raum (.+)|(\\w+straße.*)",
				Pattern.CASE_INSENSITIVE);

	/** The primary query. */
	private Query query;
	/** The similarity query. */
	private Query similQuery;
	/** Names of fields with values for the answer in natural language. */
	private Set<String> answerFields;
	/** Analyzer used for creating Lucene queries. */
	private Analyzer analyzer;
	/** true if the user query contains a semester. */
	private boolean semesterQueried;
	/** Tense of the natural language question that forms the basis of
	 * this query. */
	private Grammar.Tense tense;

	/**
	 * Creates a new query.
	 * @param tense Grammatical tense of the question that forms the basis
	 * of the new query
	 * @param queryString Lucene query as returned by
	 * the {@link Grammar grammar}
	 * @param similQueryString Lucene query for similarity as returned
	 * by the grammar
	 * @param answerFields Names of fields with values for the answer
	 * to be generated
	 * @param analyzer Analyzer to use for handling Lucene queries
	 * @throws QueryNodeException If parsing the query strings fails
	 * @see {@link Grammar#parse(String, Analyzer) Grammar.parse}
	 */
	public NQuery(final Grammar.Tense tense, final String queryString,
			final String similQueryString, final String[] answerFields,
			final Analyzer analyzer) throws QueryNodeException {
		StandardQueryParser qp = new StandardQueryParser(analyzer);
		qp.setDefaultOperator(Operator.AND);
		QueryNodeProcessorPipeline processors =
			(QueryNodeProcessorPipeline) qp.getQueryNodeProcessor();
		processors.add(new FieldsCollapsingProcessor(FIELDS_TO_COLLAPSE,
						COLLAPSE_TO, 50));

		String qs = queryString.replaceAll("'", "");
		if (qs.isEmpty()) {
			query = new MatchAllDocsQuery();
		} else {
			query = qp.parse(qs, DEFAULT_SEARCH_FIELD);
		}

		String sqs = similQueryString.replaceAll("'", "");
		if (!sqs.isEmpty()) {
			similQuery = qp.parse(sqs, DEFAULT_SEARCH_FIELD);
		}
		
		if (queryString.contains("semester:\"")
				|| similQueryString.contains("semester:\"")) {
			semesterQueried = true;
		} else {
			semesterQueried = false;
			query = interpretTense(tense, query);
			if (similQuery != null) {
				similQuery = interpretTense(tense, similQuery);
			}
		}

		this.analyzer = analyzer;
		this.tense = tense;
		this.answerFields = new HashSet<String>();

		for (String answField : answerFields) {
			this.answerFields.add(mapFieldname(answField));
		}
	}

	public final Query getQuery() {
		return query;
	}

	public final Query getSimilarityQuery() {
		return similQuery;
	}

	public final boolean hasSimilarityQuery() {
		return similQuery != null;
	}

	public final Set<String> getFieldsToAnswer() {
		return answerFields;
	}

	public final Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Maps "virtual" field names to actual field names. That is "zeit" to
	 * "semester" or, if the grammatical tense of the question is present or
	 * the query contains a semester, to "tag" and "ort" to "raum". 
	 * @param field Field name to map
	 * @return Mapped field name
	 */
	private String mapFieldname(final String field) {
		if ("zeit".equals(field)) {
			if (semesterQueried || Grammar.Tense.praes.equals(tense)
					|| answerFields.contains("semester")) {
				return "tag";
			}
			return "semester";
		} else if ("ort".equals(field)) {
			return "raum";
		}
		return field;
	}

	/**
	 * Returns a representation of this query in JSON.
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		// {
		//	"Query": query,
		//	"SQuery": similQuery,
		//	"Fields": [answerFields]
		// }

		sb.append("{\n\"Query\": ");
		sb.append(Grammar.toJsonString(query.toString(), true));
		sb.append(",\n\"SQuery\": ");

		if (hasSimilarityQuery()) {
			sb.append(Grammar.toJsonString(similQuery.toString(), true));
		} else {
			sb.append("\"\"");
		}

		sb.append(",\n\"Fields\": [");
		boolean first = true;
		for (String f : answerFields) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("\"").append(f).append("\"");
		}
		sb.append("]\n}");

		return sb.toString();
	}

	/**
	 * Extracts values for collapsed fields. Some fields are collapsed, i.e.
	 * are merged into a single field. This methods extracts values for these
	 * fields from the single field.
	 * @param field Name of a collapsed field 
	 * @param value Value of the single field to extract from
	 * @return Extracted value
	 * @throws AssertionError if <code>field</code> is not a collapsed field
	 */
	public static String extractValue(final String field, final String value) {
		Pattern pattern = null;

		if ("tag".equals(field)) {
			pattern = COLLAPSED_FIELD_TAG;
		} else if ("raum".equals(field)) {
			pattern = COLLAPSED_FIELD_RAUM;
		} else {
			throw new AssertionError();
		}

		Matcher m = pattern.matcher(value);
		if (m.find()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				String match = m.group(i);
				if (match != null) {
					return match;
				}
			}
		}

		return "(Unbekannt)";
	}

	/**
	 * Tests if a field is to be collapsed. Such fields are merged with
	 * other fields into a single field.
	 * @param field Name of the field to test
	 * @return true if <code>field</code> names a field to collapse
	 */
	public static boolean isFieldToCollapse(final String field) {
		return StringMethods.equalsOneOf(field, FIELDS_TO_COLLAPSE);
	}

	/**
	 * Returns the name of the single field other fields are collapsed
	 * to, i.e. are merged into.
	 * @return Name of the field other fields are collapsed to
	 */
	public static String getMergedField() {
		return COLLAPSE_TO;
	}

	/**
	 * Interprets the grammatical tense and extends the <code>query</code>
	 * accordingly.
	 * @param tense Grammatical tense to interpret
	 * @param query Query to extend
	 * @return Extended query according to the grammatical tense
	 */
	private static Query interpretTense(final Grammar.Tense tense,
			final Query query) {
		Semester now = new Semester();
		Query tenseQuery;

		switch(tense) {
			case pqperf:
				int year =
					new GregorianCalendar().get(GregorianCalendar.YEAR) - 1;
				tenseQuery = new TermRangeQuery("semester_end", "19700101",
						Integer.toString(year) + "0221", true, false);
				break;
			case perf:
				tenseQuery = new TermRangeQuery("semester_beg", "19700101",
						now.getBegin(), true, false);
				break;
			case praet:
				tenseQuery = new TermRangeQuery("semester_beg", "19700101",
						now.getBegin(), true, true);
				break;
			case praes:
				tenseQuery = new TermQuery(new Term("semester",
						now.getCanonical()));
				break;
			case fut1:
				tenseQuery = new TermRangeQuery("semester_end", now.getEnd(),
						"29991231", false, true);
				break;
			default:
				throw new AssertionError();
		}

		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query, BooleanClause.Occur.MUST);
		booleanQuery.add(tenseQuery, BooleanClause.Occur.MUST);

		return booleanQuery;
	}
}
