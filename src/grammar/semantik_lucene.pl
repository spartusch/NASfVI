
	% Copyright 2011 Stefan Partusch
	%
	% Licensed under the Apache License, Version 2.0 (the "License");
	% you may not use this file except in compliance with the License.
	% You may obtain a copy of the License at
	%
	%     http://www.apache.org/licenses/LICENSE-2.0
	%
	% Unless required by applicable law or agreed to in writing, software
	% distributed under the License is distributed on an "AS IS" BASIS,
	% WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	% See the License for the specific language governing permissions and
	% limitations under the License.


:- op(600, yfx, und).
:- op(650, yfx, oder).

/*	Alle Praedikate

	1-stellig:
		ort(X), zeit(X)

	2-stellig:
		dozent(X, Y), dozent_titel(X, Y)
		veranstaltung(X, Y)
		thema(X, Y)
		typ(X, Y)
		raum(X, Y)
		tag(X, Y),
		semester(X, Y)

	3-stellig:
		aehneln(X, Y, D)
	
	4-stellig:
		halten(X, Y, L, D)
*/


	% uebersetze_semantik(+Semantik, ?Query, ?SimilQuery, ?Gesucht)
	% Uebersetzt die praedikatenlogische Semantik in Lucene-Suchanfragen. Gibt ausserdem die Felder an, nach denen gefragt wird.
	% Query = Suchanfrage in Lucene
	% SimilQuery = Aehnlichkeitssuchanfrage in Lucene
	% Gesucht = Liste der Felder nach denen gesucht wird

uebersetze_semantik([Semantik, SemantikAngabe], Query, SimilQuery, Gesucht) :-
	setze_simil(Semantik),
	uebersetze(Semantik, Q, SQ, Gesucht-D),
	uebersetze(SemantikAngabe, AQ, _, D-[]),
	atom_concat(Q, AQ, Query),
	(
		(SQ \= '')
		-> atom_concat(SQ, AQ, SimilQuery)
		; SimilQuery = ''
	),
	!.


	% setze_simil(+Term)
	% Durchsucht rekursiv den Term nach dem Verb "aehneln" und setzt die durch das Dativobjekt gebundene Variabel auf den Wert 'simil'.
	% Dieser Wert dient der Unterscheidung bei feld_daten/4.

setze_simil(qu(_, R)) :- !, setze_simil(R).
setze_simil(ex(_, R)) :- !, setze_simil(R).
setze_simil(A und B) :- !, setze_simil(A), setze_simil(B).
setze_simil(A oder B) :- !, setze_simil(A), setze_simil(B).
setze_simil(aehneln(_, simil, _)) :- !.
setze_simil(_).


	% lucenefeld(?SemantikPraedikat, ?LuceneFeld)
	% Liefert das Lucene-Feld zu dem Semantik-Praedikat.

lucenefeld(veranstaltung, titel) :- !.
lucenefeld(thema, beschreibung) :- !.
lucenefeld(ort, raum) :- !.
lucenefeld(dozent_titel, dozent) :- !.
lucenefeld(F, F).


	% feld_daten(+Term, ?Feld, -Wert, ?Simil)
	% Liefert den Feldnamen bei Lucene und den Wert des Terms. Ist der Term durch ein Atom gebunden, wird dieses Atom in Simil zurueckgegeben, andernfalls ''.

feld_daten(Term, Feld, Wert, Simil) :-
	Term =.. [Praed, X, Wert], !,	% 2-stellige Praedikate
	(
		var(X) -> Simil = '' ; Simil = X
	),
	!, lucenefeld(Praed, Feld).

feld_daten(Term, Feld, Wert, '') :-
	Term =.. [Praed, Wert],		% 1-stellige Praedikate
	lucenefeld(Praed, Feld).


	% uebersetze(+Term, ?Query, ?SimilQuery, ?Gesucht)
	% Uebersetzt rekursiv Term zu Lucene-Suchanfragen. Ermittelt ebenfalls die interrogativ quantifizierten Variabeln und gibt die entsprechenden Lucene-Felder aus.
	% Term = Semantik-Term
	% Query = Suchanfrage fuer Lucene
	% SimilQuery = Aehnlichkeitssuchanfrage fuer Lucene
	% Gesucht = Differenzliste der Lucene-Felder nach denen gesucht wird


	% Nicht uebersetzen, d. h. ignorieren:
uebersetze(X, '', '', D-D) :- var(X), !.
uebersetze(halten(_,_,_,_), '', '', D-D) :- !.
uebersetze(aehneln(_,_,_), '', '', D-D) :- !.
uebersetze(ort(_), '', '', D-D) :- !.
uebersetze(zeit(_), '', '', D-D) :- !.
uebersetze(P, '', '', D-D) :- P =.. [_, _, ''], !. % jedes "leere" Praedikat

	% qu-Quantor (interrogativ)

uebersetze(qu(X, A1 und B1), Query, SimilQuery, Gesucht-D) :- !,
	uebersetze(qu(X, A1), Q1, SQ1, Gesucht-G),
	uebersetze(B1, Q2, SQ2, G-D),
	atom_concat(Q1, Q2, Query),
	atom_concat(SQ1, SQ2, SimilQuery).

uebersetze(qu(_, Praedikat), '', '', [Gesucht|D]-D) :- !, feld_daten(Praedikat, Gesucht, _, _).

	% ex-Quantor

uebersetze(ex(_, Lambda), Query, SimilQuery, Gesucht) :- !, uebersetze(Lambda, Query, SimilQuery, Gesucht).

	% Koordination

	% Spezialfall

uebersetze(dozent(X, Name) und dozent_titel(X, Titel), Query, '', D-D) :-
	var(X), !, concat_atom(['dozent:("', Titel, '" +', Name, ') '], Query), !.

	% Allgemein

uebersetze(A1 und B1, Query, SimilQuery, Gesucht-D) :-
	uebersetze(A1, Q1, SQ1, Gesucht-G),
	uebersetze(B1, Q2, SQ2, G-D),
	atom_concat(Q1, Q2, Query),
	atom_concat(SQ1, SQ2, SimilQuery).

uebersetze(A1 oder B1, Query, SimilQuery, Gesucht-D) :-
	% A1 oder B1 -> (A2 OR B2)
	uebersetze(A1, Q1, SQ1, Gesucht-G),
	uebersetze(B1, Q2, SQ2, G-D),
	concat_atom(['(', Q1, 'OR ', Q2, ') '], Query),
	atom_concat(SQ1, SQ2, SimilQuery).

	% Praedikate

	% thema(X, Thema) --> titel:Thema beschreibung:Thema
uebersetze(thema(X, Thema), Query, SimilQuery, D-D) :- !,
	uebersetze_praedikat(titel(X, Thema), Titel, Simil),
	uebersetze_praedikat(beschreibung(X, Thema), Beschreibung, Simil),
	(
		(Simil = '')
			-> concat_atom(['(', Titel, 'OR ',  Beschreibung, ') '], Query), SimilQuery = ''
			; concat_atom(['(', Titel, 'OR ',  Beschreibung, ') '], SimilQuery), Query = ''
	).

uebersetze(Praed, Query, SimilQuery, D-D) :-
	uebersetze_praedikat(Praed, TQ, Simil),
	(
		(Simil = '')
			-> Query = TQ, SimilQuery = ''
			; SimilQuery = TQ, Query = ''
	).


	% uebersetze_praedikat(+Praed, ?TermQuery, ?Simil)
	% Uebersetzt Praedikate zu Suchanfragen in Lucene.

uebersetze_praedikat(Praed, TermQuery, Simil) :-
	feld_daten(Praed, Feldname, Wert, Simil),
	(
		atom_concat(_, '"', Wert)	% Falls Wert nicht mit " endet, dann " hinzufuegen
			-> Sep = ''
			; Sep = '"'
	),
	concat_atom([Feldname, ':', Sep, Wert, Sep, ' '], TermQuery).
