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
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.spartusch.FileMethods;

/**
 * An index that ingests XML data.
 * When ingesting XML node names become field names. The XML format expected
 * by this class is described briefly at
 * {@link de.spartusch.nasfvi.EventExtractor EventExtractor}. 
 * @author Stefan Partusch 
 *
 */
public class XmlIndex {
	private static final Logger LOGGER =
		Logger.getLogger(XmlIndex.class.getName());

	/** The actual index. */
	private final Directory index;
	/** Searcher opened on the index. */
	private NSearcher searcher;
	/** Configuration of the index. */
	private IndexWriterConfig config;
	/** A set of all semesters in the index. */
	private SortedSet<String> semesters;

	/**
	 * Creates a new index and writes it to the file system or opens an index
	 * from the file system.
	 * @param dir Directory of the index
	 * @param newIndex true to create a new index, i.e. to delete
	 * <code>dir</code> before opening an index in <code>dir</code>
	 * @param analyzer Analyzer to use for ingestions
	 * @throws IOException If an IO error occurs
	 */
	public XmlIndex(final File dir, final boolean newIndex,
			final Analyzer analyzer) throws IOException {
		if (newIndex && dir.exists()) {
			LOGGER.info("Deleting " + dir);
			FileMethods.delete(dir);
		}
		index = FSDirectory.open(dir);
		config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		semesters = new TreeSet<String>();
	}

	/**
	 * Creates a new index in memory only. This index is not written
	 * to the file system and is thus not persistent.
	 * @param analyzer Analyzer to use for ingestions
	 * @throws IOException If an IO error occurs
	 */
	public XmlIndex(final Analyzer analyzer) throws IOException {
		index = new RAMDirectory();
		config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		config.setOpenMode(OpenMode.CREATE);
		semesters = new TreeSet<String>();
	}

	/**
	 * Returns an opened searcher on the index.
	 * @return Searcher on the index
	 */
	public final synchronized NSearcher getSearcher() {
		return searcher;
	}

	/**
	 * Returns the analyzer used for ingestions.
	 * @return Analyzer used for ingestions
	 */
	public final Analyzer getAnalyzer() {
		return config.getAnalyzer();
	}

	/**
	 * Returns a sorted set of all semesters in the index.
	 * @return Sorted set of indexed semesters
	 */
	public final SortedSet<String> getIndexedSemesters() {
		return new TreeSet<String>(semesters);
	}

	/**
	 * Ingests an XML source into the index.
	 * @param xmlSource Source to ingest
	 * @throws SAXException If parsing fails
	 * @throws IOException If an IO error occurs
	 */
	public final synchronized void ingest(final InputStream xmlSource)
			throws SAXException, IOException {
		LOGGER.info("Starting ingestion");

		XMLReader xr = XMLReaderFactory.createXMLReader();
		XmlIndexHandler handler = new XmlIndexHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(new InputSource(xmlSource));

		LOGGER.info(handler.documentsAdded() + " documents added");
		searcher = new NSearcher(
			new IndexSearcher(IndexReader.open(index, true))
		);
	}

	/**
	 * A SAX2 event handler for parsing and ingesting XML documents.
	 * @author Stefan Partusch
	 *
	 */
	private final class XmlIndexHandler extends DefaultHandler {
		/** Tag on which to start a new document. */
		private static final String NEW_DOC_TAG = "veranstaltung";
		/** Name of the root tag. */
		private static final String ROOT_TAG = "veranstaltungen";

		/** The document currently under construction. */
		private Document doc;
		/** Name of the current field/tag. */
		private StringBuilder currentField;
		/** Writes data to the index. */
		private IndexWriter writer;
		/** Number of documents added to the index. */
		private int docsAdded;

		public XmlIndexHandler() throws IOException {
			writer = new IndexWriter(index, config);
		}

		@Override
		public void startElement(final String uri, final String localName,
				final String qName, final Attributes atts)
				throws SAXException {
			if (NEW_DOC_TAG.equals(localName)) {
				doc = new Document();
				currentField = null;
			} else if (!ROOT_TAG.equals(localName)) {
				if (doc == null) {
					throw new RuntimeException("newDocumentTag missing");
				}
				currentField = new StringBuilder();
			}
		}

		@Override
		public void characters(final char[] ch,
				final int start, final int length) throws SAXException {
			if (currentField == null) {
				return;
			}
			for (int i = start; i < start + length; i++) {
				currentField.append(ch[i]);
			}
		}

		@Override
		public void endElement(final String uri, final String localName,
				final String qName) throws SAXException {
			if (NEW_DOC_TAG.equals(localName)) {
				try {
					addDocumentId();
					addSemesterBeginEnd();
					writer.addDocument(doc);
					docsAdded++;
					semesters.add(doc.get("semester"));
				} catch (IOException e) {
					LOGGER.severe(e.toString());
				}
			} else if (!ROOT_TAG.equals(localName)) {
				String value = currentField.toString();
				Field.TermVector storeVector = Field.TermVector.NO;
				Field.Store storeField = Field.Store.YES;
				float boost = 1.0f;

				if ("titel".equals(localName)) {
					storeVector = Field.TermVector.YES;
					boost = 2.5f;
				} else if ("beschreibung".equals(localName)) {
					storeVector = Field.TermVector.YES;
					storeField = Field.Store.NO;
					boost = 1.5f;
				}

				Field field =
					new Field(localName, value, storeField,
							Field.Index.ANALYZED, storeVector);
				field.setBoost(boost);

				doc.add(field);
			}
		}

		@Override
		public void endDocument() throws SAXException {
			try {
				writer.commit();
			} catch (CorruptIndexException e) {
				LOGGER.severe(e.toString());
				throw new RuntimeException(e);
			} catch (IOException e) {
				LOGGER.severe(e.toString());
			} finally {
				try {
					writer.close(true);
				} catch (IOException e) {
					LOGGER.severe(e.toString());
				}
			}
		}

		@Override
		public void error(final SAXParseException e) throws SAXException {
			LOGGER.severe(e.toString());
			throw e;
		}

		@Override
		public void fatalError(final SAXParseException e) throws SAXException {
			LOGGER.severe(e.toString());
			throw e;
		}

		@Override
		public void warning(final SAXParseException e) throws SAXException {
			LOGGER.warning(e.toString());
		}

		/**
		 * Returns the number of documents added using this handler.
		 * @return Number of documents added
		 */
		public int documentsAdded() {
			return docsAdded;
		}

		/**
		 * Creates and sets an ID field in the current document.
		 * @throws IOException If an IO error occurs
		 */
		private void addDocumentId() throws IOException {
			String id = String.valueOf(writer.numDocs());

			Field field =
				new Field("id", id, Field.Store.YES,
						Field.Index.NOT_ANALYZED);
			doc.add(field);
		}

		/**
		 * Adds the fields <code>semester_beg</code> and
		 * <code>semester_end</code> to the current document. These fields
		 * indicate the beginning and the end of the document's semester to
		 * make these dates searchable.
		 */
		private void addSemesterBeginEnd() {
			Semester sem = new Semester(doc.get("semester"));

			Field field = new Field("semester_beg", sem.getBegin(),
					Field.Store.NO, Field.Index.NOT_ANALYZED);
			doc.add(field);

			field = new Field("semester_end", sem.getEnd(),
					Field.Store.NO, Field.Index.NOT_ANALYZED);
			doc.add(field);
		}
	}
}
