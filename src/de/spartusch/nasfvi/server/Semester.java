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

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Central unit of time. Each event either happens in a winter semester or in
 * a summer semester. This class handles these units of time and provides the
 * ability to retrieve the current semester.
 * Following dates are assumed:
 * <ul>
 * <li>Winter semester: October, 1st - March, 31th<br>
 * Lectures: October, 15th - February, 2nd</li>
 * <li>Summer semester: April, 1st - September, 30th<br>
 * Lectures: April, 15th - July, 15th</li>
 * </ul>
 * @author Stefan Partusch
 *
 */
public class Semester {
	/** Date of the semester's beginning. Format: yyyyMMdd */
	private String begin;
	/** Date of the semester's end. Format: yyyyMMdd */
	private String end;
	/** Canonical short form to designate the semester. */
	private String canonical;
	/** true if the semester is a winter semester. */
	private boolean isWinter;

	/**
	 * Retrieves the current semester.
	 */
	public Semester() {
		Date nowDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		String now = formatter.format(nowDate);

		formatter.applyPattern("yyyy");
		String year = formatter.format(nowDate);
		
		String summerBegin = year + "0222"; // 22.02.
		String summerEnd = year + "0721"; // 21.07.

		// Winter semester
		isWinter = true;
		if (now.compareTo(summerBegin) < 0) {
			String yearBegin =
				Integer.toString(Integer.parseInt(year) - 1);
			begin = yearBegin + "0722";
			end = year + "0221";
			canonical = yearBegin + "/" + year;
		} else if (now.compareTo(summerEnd) > 0) {
			String yearEnd =
				Integer.toString(Integer.parseInt(year) + 1);
			begin = year + "0722";
			end = yearEnd + "0221";
			canonical = year + "/" + yearEnd;
		} else {
			// Summer semester
			begin = summerBegin;
			end = summerEnd;
			canonical = year;
			isWinter = false;
		}
	}

	/**
	 * Create a new semester according to a canonical representation,
	 * that designates a semester.
	 * Winter semesters are represented by year/year+1, e.g. 2007/2008.
	 * Summer semesters are represented by year, e.g. 2008. 
	 * @param canonical Canoncial representation of a semester
	 */
	public Semester(final String canonical) {
		// canonical == 2007/2008 or 2008
		String[] year = canonical.split("/"); 
		if (year.length == 2) {
			// Winter semester
			begin = year[0] + "0722"; // 22.07.
			end = year[1] + "0221"; // 21.02.
			isWinter = true;
		} else {
			// Summer semester
			begin = year[0] + "0222"; // 22.02.
			end = year[0] + "0721"; // 21.07.
			isWinter = false;
		}
		this.canonical = canonical;
	}

	/**
	 * Gets the beginning of the semester.
	 * This string is suitable for lexicographical comparisons.
	 * @return Beginning of the semester
	 */
	public final String getBegin() {
		return begin;
	}

	/**
	 * Gets the end of the semester.
	 * This string is suitable for lexicographical comparisons.
	 * @return End of the semester
	 */
	public final String getEnd() {
		return end;
	}

	/**
	 * Gets the canonical representation of the semester.
	 * @return Canoncical representation of the semester
	 * @see {@link #Semester(String)}
	 */
	public final String getCanonical() {
		return canonical;
	}

	/**
	 * Tests if the semester is a winter semester.
	 * @return true if the semester is a winter semester, false otherwise
	 */
	public final boolean isWinterSemester() {
		return isWinter;
	}

	@Override
	public int hashCode() {
		return canonical.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Semester other = (Semester) obj;
		if (!canonical.equals(other.canonical)) {
			return false;
		}
		return true;
	}
}
