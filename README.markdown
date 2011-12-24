NASfVI - Natürlichsprachiges Anfragesystem für Vorlesungsverzeichnisse im Internet
==================================================================================

NASfVI ist ein natürlichsprachiges Anfragesystem für Vorlesungsverzeichnisse im Internet. Es versteht Anfragen in natürlichem Deutsch und beantwortet sie ebenfalls auf Deutsch. Das Anfragesystem besteht aus einem mit Java-Servlets entwickelten Server, der in einem beliebigen Servlet-Container ausgeführt werden kann. Der Server beinhaltet eine in Prolog geschriebene Sprachverarbeitungskomponente, die ein Fragment des Deutschen implementiert und ein auf der Optimalitätstheorie basierendes Verfahren benutzt, um eine freie Phrasenstellung zu ermöglichen. Die Sprachverarbeitung ist darauf ausgelegt, möglichst viele Stellungsvarianten verarbeiten zu können.

Der Server verwendet Apache Lucene als Datenspeicher. Aus den natürlichsprachigen Anfragen werden Suchanfragen für Apache Lucene berechnet und dessen Suchindex durchsucht. Aus den zu den Anfragen passenden Dokumenten werden die gesuchten Informationen extrahiert und für die Generierung einer natürlichsprachigen Antwort auf Deutsch genutzt. Der Server unterstützt die OpenSearch-Spezifikationen für Suchmaschinen und Suggestions. NASfVI kann daher in verschiedenen Browsern als Suchmaschine integriert werden.

Dem Quellcode liegen Vorlesungsdaten des Centrums für Informations- und Sprachverarbeitung der Luwdig-Maximilians-Universität München bei.

Voraussetzungen
---------------

- Eine Laufzeitumgebung für Java 6 muss vorhanden sein
- SWI-Prolog muss installiert sein (http://www.swi-prolog.org/)
- Für die Installation mit Apache Ant muss Apache Ant installiert sein (http://ant.apache.org/)

Installation
------------

1. Apache Lucene 3.5 herunterladen (http://lucene.apache.org/java/docs/index.html)
	- folgende Dateien werden benötigt:
		- lucene-analyzers-3.5.0.jar
		- lucene-core-3.5.0.jar
		- lucene-highlighter-3.5.0.jar
		- lucene-memory-3.5.0.jar
		- lucene-queries-3.5.0.jar
		- lucene-queryparser-3.5.0.jar
	- die benötigten Jar-Dateien in das Verzeichnis war/WEB-INF/lib kopieren

2. jetty herunterladen (http://www.eclipse.org/jetty/)
    - jetty entpacken und in das Verzeichnis von NASfVI verschieben
    - das Verzeichnis von jetty in "jetty" umbenennen

3. Das Google Web Toolkit SDK (GWT SDK) herunterladen (http://code.google.com/intl/de/webtoolkit/download.html)
    - das GWT SDK entpacken und in das Verzeichnis von NASfVI verschieben
    - das Verzeichnis des GWT SDK in "gwt" umbenennen

4. In das Verzeichnis von NASfVI wechseln und Apache Ant mit dem Befehl "ant" oder "ant install" aufrufen.

Starten von NASfVI
------------------

Um NASfVI zu starten, muss in das Verzeichnis von jetty gewechselt werden.
jetty und NASfVI können mit dem folgenden Befehl gestartet werden:

	java -server -Djava.library.path=PFAD_JPL -jar start.jar

Dabei muss PFAD_JPL durch den absoluten Pfad zur Installation von SWI-Prolog ersetzt werden.
