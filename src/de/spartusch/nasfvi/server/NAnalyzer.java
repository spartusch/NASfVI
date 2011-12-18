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
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import de.spartusch.Resources;
import de.spartusch.StringMethods;

/**
 * Analyzes content as a natural language text, a simple text or as a single
 * token according to field names.
 * @author Stefan Partusch
 *
 */
public class NAnalyzer extends Analyzer {
	/** Names of fields that are analyzed as natural language texts. */
	private static final String[] NATURAL_TEXT_FIELDS
		= new String[] {"titel", "beschreibung", "typ"};

	/** List of stop words. */
	private Set<String> stopWords;
	/** List of words for compounds. */
	private Set<String> compounds;

	/**
	 * Creates a new NAnalyzer.
	 * @param res Resources to use to locate stop words and compounds 
	 */
	public NAnalyzer(final Resources res) {
		try {
			File swFile =
				res.getFile("nasfvi.StopWords", "/WEB-INF/stopwords.txt");
			if (swFile == null) {
				throw new IllegalArgumentException("Stop words not found");
			}

			File cFile =
				res.getFile("nasfvi.Compounds", "/WEB-INF/komposita.txt");
			if (cFile == null) {
				throw new IllegalArgumentException("Compounds not found");
			}
			
			stopWords = Resources.getWordlist(swFile, "UTF-8");
			compounds = Resources.getWordlist(cFile, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Analyzes the input as a text with natural language. In this
	 * implementation natural language refers to German only.
	 * @param reader Input to analyze
	 * @return Analyzed and processed tokens from <code>reader</code>
	 */
	private TokenStream naturalText(final Reader reader) {
		TokenStream result = new StandardTokenizer(Version.LUCENE_33, reader);

		result = new StandardFilter(Version.LUCENE_33, result);
		result = new LowerCaseFilter(Version.LUCENE_33, result);
		result = new StopFilter(Version.LUCENE_33, result, stopWords);
		result = new DictionaryCompoundWordTokenFilter(Version.LUCENE_33, result, compounds);
		result = new SnowballFilter(result, "German2");

		return result;		
	}

	/**
	 * Returns the content of the input as a single token.
	 * @param reader Input to analyze
	 * @return Content of <code>reader</code> as a single token
	 */
	private TokenStream singleToken(final Reader reader) {
		return new KeywordTokenizer(reader);
	}

	/**
	 * Analyzes the input as a simple text. In this implementation this
	 * includes stemming.
	 * @param reader Input to analyze
	 * @return Analyzed and processed tokens from <code>reader</code>
	 */
	private TokenStream simpleText(final Reader reader) {
		TokenStream result = new StandardTokenizer(Version.LUCENE_33, reader);

		result = new StandardFilter(Version.LUCENE_33, result);
		result = new LowerCaseFilter(Version.LUCENE_33, result);
		result = new StopFilter(Version.LUCENE_33, result, stopWords);
		result = new SnowballFilter(result, "German2");

		return result;	
	}

	@Override
	public final int getPositionIncrementGap(final String fieldName) {
		return 100;
	}

	@Override
	public final TokenStream tokenStream(final String fieldName,
			final Reader reader) {
		if (StringMethods.equalsOneOf(fieldName, NATURAL_TEXT_FIELDS)) {
			return naturalText(reader);
		} else if ("semester".equals(fieldName)) {
			return singleToken(reader);
		} else {
			return simpleText(reader);
		}
	}
}
