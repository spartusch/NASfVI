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

import java.io.File;

/**
 * Methods dealing with files.
 * @author Stefan Partusch
 *
 */
public final class FileMethods {
	private FileMethods() {
		throw new AssertionError();
	}

	/**
	 * Deletes a path with all files and folders in it recursively.
	 * @param path Path to delete
	 * @return true if path doesn't exist after calling this method
	 */
	public static boolean delete(final File path) {
		File[] files = path.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (!delete(files[i])) {
					return false;
				}
			}
		}

		if (!path.exists()) {
			return true;
		}

		return path.delete();
	}
}
