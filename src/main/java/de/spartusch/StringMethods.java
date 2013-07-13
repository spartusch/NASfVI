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

package de.spartusch;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Methods dealing with strings.
 * @author Stefan Partusch
 *
 */
public final class StringMethods {
	private StringMethods() {
		throw new AssertionError();
	}

	/**
	 * Compares a string to other strings. This methods returns true if
	 * <code>input</code> equals one of the other strings.
	 * @param input String to compare
	 * @param compareTo Other strings to compare to
	 * @return true if the string equals one of the other strings,
	 * false otherwise
	 */
	public static boolean equalsOneOf(final String input,
			final String... compareTo) {

		for (String s : compareTo) {
			if (s.equals(input)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Joins a collection of Strings into one single String.
	 * @param strings Strings to join
	 * @param separator Separator to use
	 * @return All strings in one string separated by the separator
	 */
	public static String join(final Collection<String> strings,
			final String separator) {
		StringBuilder sb = new StringBuilder();

		for(String str : strings) {
			if(sb.length() > 0)
				sb.append(separator);
			sb.append(str);
		}

		return sb.toString();
	}

	/**
	 * Extracts a delimited substring. The substring is defined by a
	 * delimiter before the substring and a delimiter after the substring. 
	 * @param input Input to extract the substring from
	 * @param delimiterBefore Delimiter before the substring
	 * @param delimiterAfter Delimiter after the substring
	 * @return The string between (excluding) delimiterBefore and
	 * delimiterAfter or null if delimiterBefore or delimiterAfter can't be
	 * found in input
	 */
	public static String getDelimitedSubstring(final String input,
			final String delimiterBefore, final String delimiterAfter) {
		int start = input.indexOf(delimiterBefore);
		if (start != -1) {
			start += delimiterBefore.length();
			// search delimiterAfter after delimiterBefore
			int end = input.indexOf(delimiterAfter, start);
			if (end != -1) {
				return input.substring(start, end);
			}
			return null;
		}
		return null;
	}

	/**
	 * A callback class to process HTML using {@link
	 * javax.swing.text.html.HTMLEditorKit HTMLEditorKit}. This callback is
	 * used by {@link StringMethods#stripHTML(String) stripHTML}.
	 * @author Stefan Partusch
	 * @see {@link StringMethods#stripHTML(String) stripHTML}
	 *
	 */
	private static class StripHtmlParserCallback
				extends HTMLEditorKit.ParserCallback {
		/** Holds the processed text. */
		private StringBuffer sb;
		/** true if following text is supposed to be separated
		 * from preceding text. */
		private boolean newLine;
		/** true if to inline block elements. */
		private boolean inline;

		/**
		 * Creates a new
		 * {@link javax.swing.text.html.HTMLEditorKit.ParserCallback
		 * ParserCallback}.
		 * @param inline true to inline block elements, false to start new
		 * lines on block elements.
		 */
		public StripHtmlParserCallback(final boolean inline) {
			sb = new StringBuffer();
			newLine = true;
			this.inline = inline;
		}

		/**
		 * Tests if a tag starts a new line in the layout.
		 * @param tag Tag to test
		 * @return true if the tag starts a new line; false otherwise
		 */
		private boolean startsNewLine(final HTML.Tag tag) {
			if(tag == HTML.Tag.TD)
				return false;

			return tag.isBlock() || tag.breaksFlow();
		}

		@Override
		public void handleStartTag(final HTML.Tag t,
				final MutableAttributeSet a, final int pos) {
			if (startsNewLine(t)) {
				newLine = true;
			}
		}

		@Override
		public void handleEndTag(final HTML.Tag t, final int pos) {
			if (startsNewLine(t)) {
				newLine = true;
			}
		}

		@Override
		public void handleText(final char[] text, final int pos) {
			if (newLine) {
				if (sb.length() > 0) {
					sb.append(inline ? " " : "\n");
				}
				newLine = false;
			}
			sb.append(text);
		}

		@Override
		public void handleSimpleTag(final HTML.Tag t,
				final MutableAttributeSet a, final int pos) {
			if (startsNewLine(t)) {
				newLine = true;
			}
		}

		/**
		 * Returns the parsed text.
		 * @return The parsed text
		 */
		public String getText() {
			return sb.toString();
		}
	}

	/**
	 * Creates a textual presentation of strings containing HTML.
	 * This method removes all HTML tags, normalizes (expands) entities
	 * and can insert the alternative texts of images.
	 * @param input HTML to process
	 * @param inline true to inline block elements
	 * @return Resulting string or null if an exception occurred
	 */
	public static String stripHTML(final String input, final boolean inline) {
		ParserDelegator delegator = new ParserDelegator();
		StripHtmlParserCallback cb = new StripHtmlParserCallback(inline);
		try {
			delegator.parse(new StringReader(input), cb, true);
		} catch (IOException e) {
			return null;
		}
		return cb.getText();
	}
}
