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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.lucene.analysis.Analyzer;
import org.xml.sax.SAXException;

import de.spartusch.Resources;
import de.spartusch.StringMethods;

/**
 * Initializes the index and the grammar.
 * @author Stefan Partusch
 *
 */
public final class Init implements ServletContextListener {
	private static final Logger LOGGER =
		Logger.getLogger(Init.class.getName());

	/** Name of the servlet container attribute for
	 * the {@link XmlIndex} used. */
	private static String luceneIndex = "nasfvi.lucene.index";
	/** Name of the servlet container attribute for
	 * the {@link GrammarManager} used. */
	private static String grammarManager = "nasfvi.grammar.manager";

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		Resources res = new Resources(context);
		Analyzer analyzer = new NAnalyzer(res);

		context.setAttribute(grammarManager, new GrammarManager(res, analyzer));

		try {
			//java.io.File dir = new java.io.File("/Users/stefan/NASfVI/debug-index");
			//XmlIndex index = new XmlIndex(dir, true, analyzer);
			XmlIndex index = new XmlIndex(analyzer);
			InputStream xmlToAdd = res.getAsStream("nasfvi.IndexFile",
					"/WEB-INF/index.xml");

			if (xmlToAdd == null) {
				throw new IllegalArgumentException("index.xml not found");
			} else {
				index.ingest(xmlToAdd);
			}

			context.setAttribute(luceneIndex, index);
			
			SortedSet<String> semesters = index.getIndexedSemesters();
			
			LOGGER.info("Indexed semesters: "
					+ StringMethods.join(semesters, ", "));

			if (!semesters.contains(new Semester().getCanonical())) {
				LOGGER.warning("The current semester is not indexed!");
			}
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent context) {
		;
	}

	/**
	 * Retrieves the {@link GrammarManager} from a servlet's context.
	 * @param context Context to use
	 * @return GrammarManager set in the context
	 * @see {@link #grammarManager}
	 * @throws AssertionError if no index is set in <code>context</code>
	 */
	public static GrammarManager
			getGrammarManager(final ServletContext context) {
		GrammarManager manager =
			(GrammarManager) context.getAttribute(grammarManager);

		if (manager == null) {
			throw new AssertionError(grammarManager + " not set");
		}

		return manager;
	}

	/**
	 * Retrieves a {@link NSearcher} from the index in a servlet's context.
	 * @param context Context to use
	 * @return Searcher for the index in <code>context</code>
	 * @see {@link #luceneIndex}
	 * @throws AssertionError if no index is set in <code>context</code>
	 */
	public static NSearcher getSearcher(final ServletContext context) {
		XmlIndex index = (XmlIndex) context.getAttribute(luceneIndex);

		if (index == null) {
			throw new AssertionError(luceneIndex + " not set");
		}

		return index.getSearcher();
	}
}
