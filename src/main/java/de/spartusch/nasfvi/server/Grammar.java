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

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import jpl.Atom;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.core.QueryNodeException;

import de.spartusch.StringMethods;

/**
 * Interface to the natural language grammar. This class provides access to
 * the natural language grammar implemented in Prolog. This implementation
 * uses SWI-Prolog's JPL. The system property <code>java.library.path</code>
 * must include a path to SWI-Prolog for this implementation to work properly.
 * @author Stefan Partusch
 * @see <a href="http://www.swi-prolog.org/">www.swi-prolog.org</a>
 *
 */
public class Grammar {
	/** Grammatical tenses supported by the natural language grammar. */
	public enum Tense {
		pqperf, perf, praet, praes, fut1
	};

	private static final Logger LOGGER =
		Logger.getLogger(Grammar.class.getName());

	/**
	 * Loads the natural language grammar from a file. Consults a single
	 * file to an instance of SWI-Prolog. <code>file</code> must reference
	 * all required Prolog source files.
	 * @param file File to load the natural language grammar from 
	 */
	public Grammar(final File file)  { 
		LOGGER.info("Using grammar " + file);
		Query consult = new Query("consult",
				new Term[] { new Atom(file.getAbsolutePath()) });

		if (!consult.hasSolution()) {
			String msg = "Consulting " + file + " failed";
			LOGGER.severe(msg);
			throw new RuntimeException(msg);
		}
	}

	/**
	 * Solves a Prolog goal and logs this. Returns the bindings of the goal's 
	 * variables. Each binding maps a variable's name to its bound term.
	 * @param goal Goal to solve
	 * @return Bindings of the goal's variables
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Term> solve(final Query goal) {
		Map<String, Term> bindings = (Map<String, Term>) goal.oneSolution();

		if (LOGGER.isLoggable(Level.INFO)) {
			StringBuilder sb = new StringBuilder();

			sb.append(goal);

			if (bindings != null) {
				for (Map.Entry<String, Term> entry : bindings.entrySet()) {
					String value = entry.getValue().toString();
					if (value.length() < 100) {
						sb.append("\n\t").append(entry.getKey());
						sb.append(" = ").append(value);
					}
				}
			} else {
				sb.append("\nNo solution");
			}

			LOGGER.info(sb.toString());
		}

		return bindings;
	}

	/**
	 * Completes the input to sentences. This implementation calls
	 * <code>suggest/4</code> in the natural language grammar.
	 * @param input Input to suggest sentences for
	 * @return Suggestions for the input
	 */
	public final Set<String> suggest(final String input) {
		Set<String> suggestions = new TreeSet<String>();
		Term[] args = new Term[] {
			new Atom(input),
			new jpl.Integer(8),
			new Variable("Markiertheit"),
			new Variable("Vorschlaege")
		};

		Map<String, Term> bindings = solve(new Query("suggest", args));
		
		if (bindings == null) {
			return suggestions;
		}

		for (Term t : Util.listToTermArray(bindings.get("Vorschlaege"))) {
			String[] tokens = Util.atomListToStringArray(t);
			suggestions.add(fromProlog(tokens).toString());
		}

		return suggestions;
	}

	/**
	 * Analyzes a sentence and creates a {@link NQuery} accordingly. This
	 * implementation calls <code>parse/5</code> in the natural language
	 * grammar.
	 * @param input The sentence to analyze
	 * @param analyzer Analyzer to use when creating the <code>NQuery</code>
	 * @return A <code>NQuery</code> according to the <code>input</code>
	 * @throws QueryNodeException If creating the <code>NQuery</code> fails
	 */
	public final NQuery parse(final String input, final Analyzer analyzer)
			throws QueryNodeException {
		Term[] args = new Term[] {
				new Atom(input),
				new Variable("Tempus"),
				new Variable("Query"),
				new Variable("SimilQuery"),
				new Variable("Gesucht")
		};

		Map<String, Term> bindings = solve(new Query("parse", args));

		if (bindings == null) {
			return null;
		}

		String tempus = bindings.get("Tempus").toString();
		String query = bindings.get("Query").toString();
		String similQuery = bindings.get("SimilQuery").toString();
		String[] fields = Util.atomListToStringArray(bindings.get("Gesucht"));

		return new NQuery(Tense.valueOf(tempus), query, similQuery, fields,
				analyzer);
	}

	/**
	 * Generates an answer to a question. The input is analyzed to obtain a
	 * skeleton to create the answer in natural language with by inserting
	 * the <code>answerValues</code>. The <code>answerValues</code> are a
	 * mapping from field names to the values of the fields. This
	 * implementation calls <code>beantworte/5</code> in the natural language
	 * grammar. 
	 * @param input The question in natural language to answer
	 * @param answerValues Values to insert into the answer
	 * @return Answer in natural language to <code>input</code>
	 */
	public final String generate(final String input,
			final Map<String, Set<String>> answerValues) {
		Term[] termValues = new Term[answerValues.size()];
		int i = 0;

		for (Map.Entry<String, Set<String>> e : answerValues.entrySet()) {
			String key = e.getKey();
			String[] arr = new String[e.getValue().size() + 1];
			Iterator<String> iter = e.getValue().iterator();

			arr[0] = key;
			for (int j = 1; iter.hasNext(); j++) {
				String value = iter.next();
				if (StringMethods.equalsOneOf(key,
						new String[]{"semester", "tag"})) {
					arr[j] = value.toLowerCase(Locale.GERMAN);
				} else {
					arr[j] = "\"" + value + "\"";
				}
			}

			termValues[i] = Util.stringArrayToList(arr);
			i++;
		}

		Term[] args = new Term[] {
				new Atom(input),
				Util.termArrayToList(termValues),
				new Variable("AnalyseAnfrage"),
				new Variable("AnalyseAntwort"),
				new Variable("Antwort")
		};

		Map<String, Term> bindings = solve(new Query("beantworte", args));

		if (bindings == null) {
			throw new AssertionError("No bindings received");
		}

		String analysisReq = prettyPrint(bindings.get("AnalyseAnfrage"));
		String analysisAns = prettyPrint(bindings.get("AnalyseAntwort"));
		String[] ansTokens =
			Util.atomListToStringArray(bindings.get("Antwort"));
		String answer = toJsonString(fromProlog(ansTokens), true);

		StringBuilder sb = new StringBuilder("{\n\"AnalysisReq\": ");
		sb.append(analysisReq).append(",\n\"AnalysisAns\": ");
		sb.append(analysisAns).append(",\n\"Answer\": ");
		sb.append(answer).append("\n}\n");

		return sb.toString();
	}

	/**
	 * Pretty-prints a Prolog list to a string. This implementation calls
	 * {@link #prettyPrint(Term)} on each item of <code>list</code>
	 * recursively and ignores the final empty list.
	 * @param list List to print
	 * @return  Pretty-printed Prolog list
	 */
	private String prettyPrintList(final Term list) {
		if (list.isCompound() && list.hasFunctor(".", 2)) {
			String head = prettyPrint(list.arg(1));
			String tail = prettyPrintList(list.arg(2));

			if (tail.equals("[]") || tail.isEmpty()) {
				return head;
			}

			return head + ", " + tail;
		}
		return "";
	}

	/**
	 * Pretty-prints a Prolog term to JSON. This implementation handles the
	 * operators defined in the natural language grammar ('?', '-', '>', '*',
	 * 'und', 'oder', 'lam', 'qu', 'ex') properly.
	 * @param term Term to pretty-print
	 * @return Pretty-printed term in JSON
	 */
	private String prettyPrint(final Term term) {
		String result = null;
		
		if (term.isCompound()) {
			if (term.hasFunctor(".", 2)) {
				String head = prettyPrint(term.arg(1));
				String tail = prettyPrintList(term.arg(2));

				if (tail.isEmpty()) {
					return "[" + head + "]";
				}

				return "[" + head + ", " + tail + "]";
				// no 'JSONification' because prolog lists are valid JSON arrays
			} else if (term.hasFunctor("?", 1)) {
				result = "?" + prettyPrint(term.arg(1));
			} else if (term.hasFunctor("-", 2)) {
				result = prettyPrint(term.arg(1)) + "-"
				+ prettyPrint(term.arg(2));
			} else if (term.arity() == 2) {
				String op = term.name();
				Term t1 = term.arg(1);
				Term t2 = term.arg(2);
				
				if (StringMethods.equalsOneOf(op,
						new String[]{"und", "oder", "*"})) {
					result = prettyPrint(t1) + " " + op + " " + prettyPrint(t2);
				} else if (StringMethods.equalsOneOf(op,
						new String[]{"lam", "qu", "ex"})) {
					result = op + "(" + t1.toString() + ", "
						+ prettyPrint(t2) + ")";
				} else if (op.equals(">")) {
					result = prettyPrint(t1) + op + prettyPrint(t2);
				}
			}
		}
		
		if (result == null) {
			result = term.toString();
		}

		return toJsonString(result, false);
	}

	/**
	 * Converts a string to a JSON string.
	 * @param string String to convert
	 * @param escape true to escape quotation marks, false to remove
	 * quotation marks
	 * @return A JSON string
	 */
	public static String toJsonString(final String string,
			final boolean escape) {
		return toJsonString(new StringBuilder(string), escape);
	}

	/**
	 * Creates a JSON string from a StringBuilder. The data of the
	 * StringBuilder is converted in-place to JSON.
	 * @param builder StringBuilder to use
	 * @param escape true to escape quotation marks, false to remove
	 * quotation marks
	 * @return A JSON string
	 */
	public static String toJsonString(final StringBuilder builder,
			final boolean escape) {
		int pos = -1;

		while ((pos = builder.indexOf("\"", pos + 1)) != -1) {
			if (escape && (pos == 0 || (pos > 1 && builder.charAt(pos - 1)
					!= '\\'))) {
				builder.insert(pos, '\\');
			} else if (!escape) {
				builder.deleteCharAt(pos);
			}
		}

		builder.insert(0, '"');
		builder.append('"');

		return builder.toString();
	}

	/**
	 * Processes a natural language sentence returned from Prolog. This method
	 * concatenates each token, restores German umlauts, quotes blackboxes and
	 * converts the first character to uppercase.
	 * @param tokens Tokens of the sentence
	 * @return Processed sentence
	 * @see {@link #toProlog(String) toProlog}
	 */
	private static StringBuilder fromProlog(final String[] tokens) {
		StringBuilder sb = new StringBuilder();

		for (String tok : tokens) {
			if (tok.charAt(0) != '"') {	// Token is no black box
				tok = tok.replace("Ae", "\u00C4");
				tok = tok.replace("Oe", "\u00D6");
				tok = tok.replace("Ue", "\u00DC");
				tok = tok.replace("ae", "\u00E4");
				tok = tok.replace("oe", "\u00F6");
				tok = tok.replaceAll("ue(?!n\\b)", "\u00FC");
				tok = tok.replace("ss", "\u00DF");
				sb.append(tok).append(" ");
			} else { // Token is black box
				boolean quote = tok.indexOf(' ') != -1;
				// test if multiple words
				int start = 1;

				if (tok.charAt(start) == '#') {
					start++; // skip #
				}

				Character upper = Character.toUpperCase(tok.charAt(start));
				if (quote) {
					sb.append('"');
				}
				sb.append(upper);
				sb.append(tok.substring(start + 1, tok.length() - 1));
				if (quote) {
					sb.append('"');
				}
				sb.append(" ");
			}
		}
		
		Character first = sb.charAt(0);
		sb.deleteCharAt(0);
		sb.insert(0, Character.toUpperCase(first));

		return sb;
	}

	/**
	 * Normalizes input for use with the natural language grammar in Prolog.
	 * This method converts the input to lowercase, encodes German umlauts
	 * with ASCII characters (ae, oe, ue, ...) and removes meta-characters not
	 * suitable for processing by the natural language grammar. 
	 * @param input Input to normalize
	 * @return Normalized <code>input</code>
	 * @see {@link #fromProlog(String[]) fromProlog}
	 */
	public static String toProlog(final String input) {
		StringBuilder sb = new StringBuilder();
		boolean inBlackBox = false;

		for (int i = 0; i < input.length(); i++) {
			int ch = input.codePointAt(i);
			if (ch != '"') {
				if (!inBlackBox) {
					ch = Character.toLowerCase(ch);
					switch(ch) {
						case '\u00E4':
							sb.append("ae");
							break;
						case '\u00F6':
							sb.append("oe");
							break;
						case '\u00FC':
							sb.append("ue");
							break;
						case '\u00DF':
							sb.append("ss");
							break;
						case '\\':
							sb.append("/");
							break;
						default:
							if (Character.isLetterOrDigit(ch)
									|| ch == '/' || ch == '.' || ch == '-') {
								sb.appendCodePoint(ch);
							} else {
								sb.append(" ");
							}
					}
				} else {
					sb.appendCodePoint(ch);
				}
			} else {
				inBlackBox = !inBlackBox;
				sb.append('"');
			}
		}

		return sb.toString().trim();
	}
}
