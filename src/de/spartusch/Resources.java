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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletContext;

/**
 * Provides methods to load resources.
 * @author Stefan Partusch
 *
 */
public class Resources {
	/** ServletContext to load resources from. */
	private ServletContext context;

	/**
	 * @param scontext ServletContext to load resources from.
	 */
	public Resources(final ServletContext scontext) {
		this.context = scontext;
	}

	/**
	 * Gets a file using a system property or from the {@link
	 * javax.servlet.ServletContext#getResource ServletContext's resources}.
	 * This method returns the file from the path in the system property if
	 * the system property is set. If the system property is not set the
	 * ServletContext is used. Thus <code>systemProperty</code> may
	 * be used to "overwrite" <code>loadPath</code>.
	 * @param systemProperty Property to get the path of the file from
	 * @param loadPath Path to load from the ServletContext
	 * @return The file requested or null if the file is not found
	 */
	public final File getFile(final String systemProperty,
			final String loadPath) {
		File file = null;
		String sysProp = System.getProperty(systemProperty);

		if (sysProp != null) {
			file = new File(sysProp);
		} else {
			try {
				URL url = context.getResource(loadPath);
				if (url != null) {
					file = new File(url.toURI());
				}
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}

		if (file == null || !file.isFile()) {
			return null;
		}

		return file;
	}

	/**
	 * Gets a file as a stream using a system property or from the {@link
	 * javax.servlet.ServletContext#getResourceAsStream ServletContext's
	 * resources}. This method returns the stream of a file in the system
	 * property if the system property is set. If the system property is not
	 * set the ServletContext is used. Thus <code>systemProperty</code> may
	 * be used to "overwrite" <code>loadPath</code>.
	 * @param systemProperty Property to get the path of the file from
	 * @param loadPath Path to load from the ServletContext
	 * @return The InputStream of the file requested or null if the file is
	 * not found
	 */
	public final InputStream getAsStream(final String systemProperty,
			final String loadPath) {
		InputStream is = null;

		try {
			String sysProp = System.getProperty(systemProperty);
			if (sysProp != null) {
				is = new FileInputStream(sysProp);
			} else {
				is = context.getResourceAsStream(loadPath);
			}
		} catch (FileNotFoundException e) {
			return null;
		}

		return is;
	}

	/**
	 * Reads a word list from a file. A word list is a text file with a word
	 * on each line and this method returns a set of the <code>file</code>'s
	 * lines.
	 * @param file File to read
	 * @param charset Charset of the file
	 * @return A thread-safe word list.
	 * @throws IOException if some IO fails
	 */
	public static Set<String> getWordlist(final File file,
			final String charset) throws IOException {
		Set<String> set = new CopyOnWriteArraySet<String>();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), charset));

			String line;
			while ((line = reader.readLine()) != null) {
				set.add(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return set;
	}
}
