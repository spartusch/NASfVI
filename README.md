NASfVI - Natürlichsprachiges Anfragesystem für Vorlesungsverzeichnisse im Internet
==================================================================================

NASfVI ist ein natürlichsprachiges Anfragesystem für Vorlesungsverzeichnisse im Internet. Es versteht Anfragen in natürlichem Deutsch und beantwortet sie ebenfalls auf Deutsch. Das Anfragesystem besteht aus einem mit Java-Servlets entwickelten Server, der in einem beliebigen Servlet-Container ausgeführt werden kann. Der Server beinhaltet eine in Prolog geschriebene Sprachverarbeitungskomponente, die ein Fragment des Deutschen implementiert und ein auf der Optimalitätstheorie basierendes Verfahren benutzt, um eine freie Phrasenstellung zu ermöglichen. Die Sprachverarbeitung ist darauf ausgelegt, möglichst viele Stellungsvarianten verarbeiten zu können.

Der Server verwendet Apache Lucene als Datenspeicher. Aus den natürlichsprachigen Anfragen werden Suchanfragen für Apache Lucene berechnet und dessen Suchindex durchsucht. Aus den zu den Anfragen passenden Dokumenten werden die gesuchten Informationen extrahiert und für die Generierung einer natürlichsprachigen Antwort auf Deutsch genutzt. Der Server unterstützt die OpenSearch-Spezifikationen für Suchmaschinen und Suggestions. NASfVI kann daher in verschiedenen Browsern als Suchmaschine integriert werden.

Dem Quellcode liegen Vorlesungsdaten des Centrums für Informations- und Sprachverarbeitung der Luwdig-Maximilians-Universität München bei.

Voraussetzungen
---------------

- Java SDK (Version >= 1.6)
- Maven (Version >= 3.x)
- SWI-Prolog mit JPL-Unterstützung.
	Unter OS X kann SWI-Prolog mit [HomeBrew](http://mxcl.github.io/homebrew/) installiert werden:
	`brew install --with-jpl swi-prolog`

Verwendung
----------

Um die Java-Prolog-Schnittstelle und damit NASfVI verwenden zu können, muss die Umgebungsvariabel `MAVEN_OPTS` gesetzt werden:

	MAVEN_OPTS="-Djava.library.path=..."

Statt `...` muss der Pfad zu den dynamischen Bibliotheken von SWI-Prolog angegeben werden. Unter OS X kann das zum Beispiel so aussehen:

	export MAVEN_OPTS="-Djava.library.path=/usr/local/Cellar/swi-prolog/6.2.6/lib/swipl-6.2.6/lib/x86_64-darwin12.4.0/"

Dieser Pfad ist jedoch je nach System und Version von SWI-Prolog verschieden!

NASfVI selbst kann sehr einfach aus seinem Ordner heraus gestartet werden:

	mvn jetty:run-war

Sobald NASfVI erfolgreich gestartet worden ist, kann es im Browser aufgerufen werden:

[http://localhost:8080](http://localhost:8080)

Mit Ctrl+C kann es wieder beendet werden.
