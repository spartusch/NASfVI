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
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.search.TopDocs;

/**
 * Analyzes and answers questions. The response format to HTTP GET requests is
 * JSON. The parameter "q" is required for the question. A parameter "offset"
 * is optional.
 * @author Stefan Partusch
 *
 */
public class Parselet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected final void doGet(final HttpServletRequest req,
			final HttpServletResponse res)
			throws ServletException, IOException {
		String q = req.getParameter("q");
		int offset = 0;
		
		try {
			String offsetStr = req.getParameter("offset");
			if (offsetStr != null) {
				offset = Integer.parseInt(offsetStr);
			}
		} catch (NumberFormatException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (q != null) {
			GrammarManager manager =
				Init.getGrammarManager(getServletContext());
			NSearcher searcher = Init.getSearcher(getServletContext());
			
			NQuery nquery;

			try {
				nquery = manager.parse(q);
				if (nquery == null) {
					res.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
			} catch (QueryNodeException e) {
				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			TopDocs result = searcher.search(nquery, offset);

			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_OK);

			PrintWriter out = res.getWriter();

			Map<String, Set<String>> vals =
				searcher.getAnswerValues(nquery, result, offset);
			String response = manager.generate(q, vals);

			out.print("[\n");
			out.print(searcher.toJson(nquery, result, offset));
			out.print(",\n");
			out.print(response);
			out.print("]");		
		}
	}
}
