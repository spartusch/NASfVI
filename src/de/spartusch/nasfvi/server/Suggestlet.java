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
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns suggestions for sentences. The response format to HTTP GET
 * requests is JSON. The parameter "q" is required for the input.
 * @author Stefan Partusch
 *
 */
public class Suggestlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected final void doGet(final HttpServletRequest req,
			final HttpServletResponse res)
			throws ServletException, IOException {
		String q = req.getParameter("q");

		if (q != null) {
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/x-suggestions+json");
			//res.setContentType("text/plain");

			PrintWriter out = res.getWriter();
			GrammarManager manager =
				Init.getGrammarManager(getServletContext());

			out.print("[");
			out.print(Grammar.toJsonString(q, false));
			out.print(", [");

			if (q.indexOf(' ') != -1) { // Minimum two tokens
				Set<String> suggestions = manager.suggest(q);
				Iterator<String> i = suggestions.iterator();
				
				while (i.hasNext()) {
					String s = i.next();
					out.print(Grammar.toJsonString(s, true));
					if (i.hasNext()) {
						out.print(", ");
					}
				}
			}

			out.print("]]");
		}
	}
}
