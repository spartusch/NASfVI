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
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.core.QueryNodeException;

import de.spartusch.Resources;
import de.spartusch.SoftCache;

/**
 * Convenient manager for a {@link Grammar}. This class takes care of
 * {@link Grammar#toProlog(String) normalizations} of input and maintains a
 * cache for suggestions to improve performance.
 * @author Stefan Partusch
 *
 */
public class GrammarManager {
	private SoftCache<String, Set<String>> suggestionsCache;
	private Grammar grammar;
	private Analyzer analyzer;

	/**
	 * Initializes a new {@link Grammar} and a new cache.
	 * @param res Resources to locate the Grammar with
	 * @param analyzer Analyzer to use for creating queries
	 */
	public GrammarManager(final Resources res, final Analyzer analyzer) {
		File startFile = res.getFile("nasfi.StartFile",
				"/WEB-INF/classes/grammar/start_vf");

		if (startFile == null) {
			throw new IllegalArgumentException("Start file not found");
		}

		grammar = new Grammar(startFile);
		suggestionsCache = new SoftCache<String, Set<String>>();
		this.analyzer = analyzer;
	}

	/**
	 * Generates suggestions. This method maintains a cache with suggestions
	 * to improve its performance.
	 * @param input Input to generate suggestions for
	 * @return Suggestions for <code>input</code>
	 * @see {@link Grammar#suggest(String) Grammar.suggest}
	 */
	public final Set<String> suggest(final String input) {
		String normInput = Grammar.toProlog(input);
		Set<String> suggestions = suggestionsCache.get(normInput);

		if (suggestions != null) {
			return suggestions;
		} else {
			suggestions = grammar.suggest(normInput);
			if (suggestions != null) {
				suggestionsCache.put(normInput, suggestions);
			}
			return suggestions;
		}
	}

	/**
	 * Creates a {@link NQuery} according to <code>input</code>.
	 * @param input Input to parse
	 * @return Query according to <code>input</code>
	 * @throws QueryNodeException If parsing fails
	 * @see {@link Grammar#parse(String, Analyzer) Grammar.parse}
	 */
	public final NQuery parse(final String input) throws QueryNodeException {
		return grammar.parse(Grammar.toProlog(input), analyzer);
	}

	/**
	 * Generates an answer in natural language.
	 * @param input Question to answer
	 * @param values Values to insert into the answer
	 * @return Natural language answer
	 * @see {@link Grammar#generate(String, Map) Grammar.generate}
	 */
	public final String generate(final String input,
			final Map<String, Set<String>> values) {
		return grammar.generate(Grammar.toProlog(input), values);
	}
}
