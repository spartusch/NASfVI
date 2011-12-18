
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


	% Operator-Definition fuer '?'/1

:- op(100, fx, '?').
'?'(X) :- call(X).
	% Bei bspw. GNU Prolog, SWI-Prolog geht auch call/3: '?'(X) --> call(X).
	% Aber ISO ist nur call/1:
'?'(X, A, B) :- X =.. XL, append(XL, [A, B], PL), P =.. PL, call(P).

	% Operator-Definition fuer die Wortarten (v>v, v>aux, p>p, p>def, ...)

:- op(100, xfx, '>').

	% Operator-Definitionen fuer die Semantik

:- op(600, yfx, und).
:- op(650, yfx, oder).

	% Suggest-Modus

:- dynamic suggest_modus/1.


	% Merkmale

person(1). person(2). person(3).
numerus(sg). numerus(pl).
casus(X) :- member(X, [nom, gen, dat, akk]).

	% Praesens und Praeteritum
einfache_zeiten(X) :- X = praes ; X = praet.

	% Perfekt, Plusquamperfekt und Futur 1
zusammengesetzte_zeiten(X) :- member(X, [perf, pqperf, fut1]).

	% Partizip II und Infinitv
indefinite(X) :- X = part2 ; X = inf.

	% Zuordnung der Zeitformen des Hilfsverbs zur Zeitform des Satzes
zeiten_aux-partizip(praes, perf). % hat stattgefunden (= Perfekt)
zeiten_aux-partizip(praet, pqperf). % hatte stattgefunden (= Plusquamperfekt)

genusverbi(aktiv).
genusverbi(passiv).

semantiktyp(Typ) :- member(Typ, [hum, event, thema, expl, loc, semester, temp_d]).

	% verbstellung(?Syntaktisch, ?Lexikalisch)
	% Bildet v1, v2 auf v1/v2, sowie vl auf vl ab und generiert v1/v2 und vl.
	% Syntaktisch = In der Syntax verwendete Verbstellungen (v1, v2, vl)
	% Lexikalisch = Im Lexkion verwendete Verbstellungen (v1/v2, vl)

verbstellung(Syn, Lex) :- var(Syn), !, (Lex = v1/v2 ; Lex = vl).
verbstellung(v1, v1/v2).
verbstellung(v2, v1/v2).
verbstellung(vl, vl).

	% alphabet(-Liste)
	% Alle in der Eingabe zulaessigen Buchstaben.

alphabet([a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z]).

	% ziffern(-Liste)
	% Alle in der Eingabe zulaessigen Ziffern.

ziffern(['0','1','2','3','4','5','6','7','8','9']).
ziffer(Z) :- ziffern(Zs), member(Z, Zs).

	% whitespace(-Liste)
	% Alle in der Eingabe zulaessige Whitespace-Zeichen.

whitespace([' ',\t,\n]).


	% Allgemeine Hilfspraedikate (u. a. bekannte Praedikate, die aber kein ISO sind)


member(X, [X|_]).
member(X, [_|L]) :- member(X, L).

append([], L, L).
append([H|L1], L2, [H|L12]) :- append(L1, L2, L12).

writeln(T) :- write(T), nl.

concat_atom([], '').
concat_atom([H|T], A) :-
	number(H), !, number_chars(H, Cs), atom_chars(H2, Cs), concat_atom(T, A2), atom_concat(H2, A2, A).
concat_atom([H|T], A) :- concat_atom(T, A2), atom_concat(H, A2, A).

sumlist([], 0).
sumlist([H|T], Sum) :- sumlist(T, Sum2), Sum is Sum2 + H.


	% zahl(?Zahl)
	% Erzeugt eine Zahl groesser gleich 0. Das Praedikat erzeugt unendlich viele Zahlen.

zahl(0).
zahl(Zahl) :- zahl(Z), Zahl is Z + 1.


	% intervall(+Basis, +Bereich, ?Zahl)
	% Erzeugt eine Zahl im Intervall [Basis; Basis+Bereich].

intervall(Basis, Bereich, Zahl) :-
	integer(Basis),
	integer(Bereich),
	zahl(Z),
	(Z =< Bereich -> true ; !, fail),
	Zahl is Basis + Z.


	% foreach(+Liste, +Praedikat)
	% Fuehrt fuer jedes Element von Liste Praedikat aus. Verwendet die jeweiligen Elemente der Liste als erstes Argument von Praedikat.
	% Schlaegt fehl, wenn auf ein Element das Praedikat nicht angewendet werden kann.
	% Praedikat = Praedikat, welches fuer jedes Element aufgerufen wird. Die Stelligkeit ist beliebig.

foreach([], _) :- !.
foreach([H|T], C_) :-
	C_ =.. [Funktor|Args],
	C =.. [Funktor, H|Args],
	call(C),
	foreach(T, C_).


	% finde_paar(+Liste, ?A, ?B)
	% Ermittelt die beiden in der Liste aufeinanderfolgenden Elemente A und B.

finde_paar([A, B|_], A, B).
finde_paar([_|T], A, B) :- finde_paar(T, A, B).


	% zu_kleinbuchst(+A, ?B)
	% Konvertiert den Buchstaben A zu einem Kleinbuchstaben.
	% B = Kleinbuchstabe zu A

zu_kleinbuchst(B, B) :- alphabet(A), member(B, A), !.
zu_kleinbuchst(A, B) :-
	char_code(A, ACode),
	BCode is ACode + 32,
	char_code(B, BCode),
	alphabet(Alphas), member(B, Alphas).


	% nur_kleinbuchstaben(+Liste1, -Liste2)
	% Konvertiert alle Zeichen in Liste1 zu Kleinbuchstaben.
	% Liste2 = Ergebnis der Umwandlung

nur_kleinbuchstaben([], []) :- !.
nur_kleinbuchstaben([A|T], [B|T2]) :-
	zu_kleinbuchst(A, B),
	nur_kleinbuchstaben(T, T2).


	% normobjekt(+Obj, -NormObj)
	% Entfernt eventuelle ?-Operatoren von Obj.

normobjekt(?Obj, Obj) :- !.
normobjekt(Obj, Obj).


	% erzeuge_diffliste(+Liste, -Differenzliste)
	% Erzeugt aus einer normalen Liste eine Differenzliste. Ueberfluessig durch expandiere_valenz/5.
	% Liste = Normale Prolog-Liste
	% Differenzliste = Liste-Diff = Liste mit freier Variabel Diff als Restliste

erzeuge_diffliste(L, Diff-Diff) :- nonvar(L), L = [], !.
erzeuge_diffliste([H|T], [H|T2]-Diff) :- erzeuge_diffliste(T, T2-Diff).


	% entferne(+Element, +Liste, ?Anzahl, ?Ergebnis)
	% Entfernt Element aus Liste Anzahl-mal. Liste darf maximal am Ende eine freie Variabel besitzen. Unifiziert Element mit einem Element von Liste.
	% Ergebnis = Ergebnis der Entfernung und ohne moeglicher freier End-Variabel von Liste

entferne(_, B, 0, []) :- var(B), !.
entferne(_, [], 0, []) :- !.

entferne(X, [Y_|B], H1, C) :- normobjekt(Y_, Y), nonvar(Y), X = Y, entferne(X, B, H2, C), H1 is H2 + 1.
entferne(X, [Y|B], H, [Y|C]) :- nonvar(Y), entferne(X, B, H, C).


	% entferne_d(+Element, +Differenzliste, ?Anzahl, ?ErgebnisDifferenzliste)
	% Analog entferne/4 - nur mit Differenzlisten statt normaler Listen.
	% Anders als entferne/4 wird jedoch nicht immer das erste Auftreten entfernt: entferne_d/4 ist "backtrackbar". Wichtig fuer ersetze_interrogativ/4.

entferne_d(_, D1-D1, 0, D2-D2) :- var(D1), var(D2), !.

entferne_d(X, [Y_|B]-D1, H1, C-D2) :-
	normobjekt(Y_, Y), nonvar(Y), X = Y, entferne_d(X, B-D1, H2, C-D2), H1 is H2 + 1.
entferne_d(X, [Y|B]-D1, H, [Y|C]-D2) :- nonvar(Y), entferne_d(X, B-D1, H, C-D2).


	% entferne_liste(+Liste1, +Liste2, ?Ergebnis)
	% Entfernt alle Elemente von Liste1 aus Liste2 exakt einmal. Schlaegt fehl, wenn nicht alle Elemente von Liste1 entfernt werden koennen. Freie Variabeln am Ende von Liste1 und Liste2 werden ignoriert. Unifiziert uebrige Elemente der Liste1 mit den Elementen der Liste2.
	% Ergebnis = Ergebnis der Entfernung und ohne moeglicher freier End-Variabeln

entferne_liste(A, B, B) :- var(A), !.
entferne_liste([], B, B) :- !.
entferne_liste([X|A], B, D) :- nonvar(X), entferne(X, B, 1, C), !, entferne_liste(A, C, D).


	% generierung(suggest)
	% Erlaubt es, zu erkennen, ob eine Phrase generiert, ergaenzt oder geparset  werden soll. Steuert zudem die Generierung der Platzhalter im Suggest-Modus.
	% Wahr, wenn das naechste Token generiert wird. False, wenn das naechste Token ein bestehendes Token ist.
	% Ist suggest_modus(aus) gesetzt, schlaegt generierung(suggest) immer fehl.

generierung(suggest) --> {suggest_modus(aus), !, fail}.
generierung(suggest) --> wird_generiert.

wird_generiert --> [VF], {nonvar(VF), !, fail}.
wird_generiert --> {true}.


	% aktiviere_suggest/0
	% deaktiviere_suggest/0
	% Setzt oder loescht suggest_modus/1 als Markierung in der Datenbank.

suggest_modus(an).

deaktiviere_suggest :- suggest_modus(aus), !.
deaktiviere_suggest :- retract(suggest_modus(_)), asserta(suggest_modus(aus)).

aktiviere_suggest :- suggest_modus(an), !.
aktiviere_suggest :- retract(suggest_modus(_)), asserta(suggest_modus(an)).
