
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


:- op(100, fx, '?').

	% expandiere_valenz(+GenusVerbi, +synSemValenz, ?synValenz, +FormSubj)
	% Expandiert die syntaktisch-semantische Valenz zur syntaktischen Valenz indem alle Rollen durch syntaktische Phrasen ersetzt werden. Erzeugt aus synSemValenz die Differenzliste synValenz.
	% GenusVerbi = aktiv oder passiv
	% synSemValenz = syntaktisch-semantische Valenz, d. h. Liste mit den Rollen agens, patiens, expletiv, thema, loc
	% synValenz = Differenzliste nur aus syntaktischen Phrasen bestehend
	% FormSubj = Formmerkmale des Subjekts (fuer Kongruenz mit dem finiten Verb)

expandiere_valenz(GenusVerbi, SynSemValenz, SynValenz, FormSubj) :-
	expandiere_valenz(GenusVerbi, SynSemValenz, SynValenz, FormSubj, anf(A,B), gef(C,D), _SubjRolle),
	(
		(nonvar(A), A = 0)
			-> (var(C) ; C = 0)
			; true
	),
	(
		(nonvar(A), A = 1)
			-> nonvar(C), A = C
			; true
	),
	(
		(nonvar(B), B = 1)
			-> nonvar(D), B = D
			; true
	), !.

	% expandiere_valenz(+GenusVerbi, +synSemValenz, ?synValenz, +FormSubj, -Anforderung, -Gefunden, -SubjRolle)
	% Anforderung = anf(AgensVorhanden, PatiensVorhanden) - jeweils boolesche Werte (0/1)
	% Gefunden = gef(AgensVorhanden, PatiensVorhanden)
	% SubjRolle = Semantische Rolle, die das Subjekt bildet

expandiere_valenz(_, [], D-D, _, _, _, _) :- var(D), !.
expandiere_valenz(_, [], []-[], _, _, _, _) :- !.

expandiere_valenz(GenusVerbi, [H|T], [H2|T2]-D, FormSubj, Anf, Gef, SubjRolle) :-
	expandiere_rolle(GenusVerbi, H, H2, FormSubj, Anf, Gef, SubjRolle),
	expandiere_valenz(GenusVerbi, T, T2-D, FormSubj, Anf, Gef, SubjRolle), !.

	% expandiere_rolle(?GenusVerbi, ?Rolle, ?Phrase, ?FormSubj, ?Anforderung, ?Gefunden, ?SubjRolle)
	% Uebersetzt die Rolle unter Beruecksichtigung von GenusVerbi und Anforderung/Gefunden zur Phrase.
	% Rolle = semantische Rolle, wie sie im Grundformen-Lexikon angegeben ist
	% Phrase = syntaktische Phrase, wie sie in der Syntax verwendet wird

	% Agens

expandiere_rolle(aktiv,
	agens(Typ, Sem), [np, [Pers, Num, nom], Typ, _, _, Sem, _], [Pers, Num],
	anf(1,_), gef(1,_), agens).

expandiere_rolle(passiv,
	agens(Typ, Sem), ?[pp, [dat], Typ, _, Sem, _], _,
	anf(1,1), gef(1,_), patiens).


	% Patiens, aktiv, intransitiv (kein Agens vorhanden)

expandiere_rolle(aktiv,
	patiens(Typ, Sem), [np, [Pers, Num, nom], Typ, _, _, Sem, _], [Pers, Num],
	anf(0,1), gef(_,1), patiens).

	% Patiens, aktiv, transitiv (Agens vorhanden)

expandiere_rolle(aktiv,
	patiens(Typ, Sem), [np, [_, _, akk], Typ, _, _, Sem, _], _,
	anf(1,1), gef(_,1), _).

	% Patiens, passiv, nur moeglich, wenn Agens vorhanden

expandiere_rolle(passiv,
	patiens(Typ, Sem), [np, [Pers, Num, nom], Typ, _, _, Sem, _], [Pers, Num],
	anf(1,1), gef(_,1), patiens).


	% Expletiv

expandiere_rolle(aktiv,
	expletiv, [np, [3, sg, nom], expl, expl, _, '', _], [3, sg],
	anf(1,_), gef(1,_), expletiv).


	% Sonstige Rollen

expandiere_rolle(_, thema(Cas, Sem), [pp, [Cas], thema, _, Sem, _], _, _, _, _).
expandiere_rolle(_, loc(Sem), [_, [dat], loc, _, Sem, _], _, _, _, _).
expandiere_rolle(_, temp_d(Sem), [_, [dat], temp_d, _, Sem, _], _, _, _, _).

expandiere_rolle(GV, ?X, ?Y, FormSubj, Anf, Gef, SubjRolle) :-
	expandiere_rolle(GV, X, Y, FormSubj, Anf, Gef, SubjRolle).


	% Objekte

expandiere_rolle(_, nom(Typ, Sem), [np, [Pers, Num, nom], Typ, _, _, Sem, _], [Pers, Num], _, _, _).
expandiere_rolle(_, dat(Typ, Sem), [np, [_, _, dat], Typ, _, _, Sem, _], _, _, _, _).