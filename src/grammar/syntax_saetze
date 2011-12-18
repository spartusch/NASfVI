
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
:- op(100, xfx, '>').
:- dynamic suggest_modus/1.

	% angaben(A)
	% A = Liste der fakulativen Angaben eines jeden Satzes

angaben([ ?[_, [dat], semester, _, Sem, _] ], Sem).

	% s([?Markiertheit, [?Infinitiv, ?GenusVerbi, ?Verbstellung, ?Tempus], ?Felderstruktur, ?Semantik], ?Atome)
	% Erkennt einen Satz.
	% Markiertheit = Markiertheit des Satzes
	% Infinitiv = Infinitiv des Semantik-tragenden Verbs
	% GenusVerbi = aktiv oder passiv
	% Verbstellung = Stellung des Verbs im Satz (v2, v1)
	% Tempus = Tempus des finiten Verbs im Satz (pqperf, perf, praes, fut1)
	% Felderstruktur = [Liste der Phrasen des Vorfelds, des Mittelfelds, des Nachfelds]
	% Semantik = Semantik des Satzes
	% Atome = Atome des Satzes

s(Analyse, A1) -->
		{Analyse = [Markheit, [Inf, GV, v2, Pers, Num, Temp], [P1, P2, []], [SemV, SemA]]},
	vorfeld(_, P1-[], A1-A2, _),
	\+generierung(suggest),		% Alles bis und inkl lsk muss instanziiert sein!
	lsk([GV, v2, Pers, Num, Temp], Verbval, SemV, Inf, LV, LA, A2-A3),
		{angaben(An, SemA),
		gesamtvalenz(Verbval, Val-An),
		entferne_liste(P1, Val, PhM)
		},
	mittelfeld(PhM, P2-[], A3-A4, PhN),
	rsk([GV, v2, Pers, Num, Temp], LV, LA, A4-[]),
	nachfeld,
		{leere_semantik(PhN),
		unifiziere_objval([P1, P2, []]),
		eval(Analyse, A1, Markheit)
		}.

s(Analyse, A1) -->
		{Analyse = [Markheit, [Inf, GV, v1, Pers, Num, Temp], ['', P1, []], [SemV, SemA]]},
	lsk([GV, v1, Pers, Num, Temp], Verbval, SemV, Inf, LV, AV, A1-A2),
	\+generierung(suggest),
		{angaben(An, SemA),
		gesamtvalenz(Verbval, Val-An)},
	mittelfeld(Temp, GV, Val, P1-[], A2-A3, PhN),
	rsk([GV, v1, Pers, Num, Temp], LV, AV, A3-[]),
	nachfeld,
		{leere_semantik(PhN),
		unifiziere_objval([[], P1, []]),
		eval(Analyse, A1, Markheit)
		}.


	% s_gen(+Phrasen, ?Analyse, ?Atome)
	% Generiert Saetze aufgrund der uebergebenen Phrasen und der Analyse.
	% Erzeugt nur Verbzweitsaetze, laesst Stellungsvarianten zu, sucht die optimale.

s_gen(Phrasen, Analyse, A1) -->
		{Analyse = [Markheit, [Inf, GV, v2, Pers, Num, Temp], [P1, P2, []], [SemV, SemA]]},
	vorfeld(Phrasen, P1-[], A1-A2, PhM),
	lsk([GV, v2, Pers, Num, Temp], Verbval, SemV, Inf, LV, LA, A2-A3),
		{angaben(An, SemA),
		gesamtvalenz(Verbval, Val-An),
		unifiziere_zwingend(Phrasen, Val),
		entferne_liste(P1, Phrasen, PhM)
		},
	mittelfeld(PhM, P2-[], A3-A4, []),
	rsk([GV, v2, Pers, Num, Temp], LV, LA, A4-[]),
	nachfeld,
		{entferne_liste(Phrasen, Val, Fakultative),
		leere_semantik(Fakultative),
		unifiziere_objval([P1, P2, []]),
		eval(Analyse, A1, Markheit)
		}.


	% Allgemeine Hilfspraedikate

	% gesamtvalenz(+Verbvalenz, -Gesamtvalenz)
	% Kopiert die Verbvalenz und fuegt die Valenz der Nominalphrasen hinzu. Beruecksichtigt jedoch nur die Nominalphrasen, die direkt aus der Verbvalenz stammen. Eine Annahme ist, dass ein semantischer Typ immer dieselbe Valenz hat.
	% Verbvalenz = Differenzliste mit der Valenz des Verbes
	% Gesamtvalenz = Differenzliste aus Verbvalenz und Nominalvalenzen

nomenvalenz_ohne_cut(Typ, Val) :- (POS = n>_ ; POS = pro>_), vollform(POS, _, _, Typ, _, Val, _, _, _).
nomenvalenz(Typ, Val) :- nomenvalenz_ohne_cut(Typ, Val), !.

gesamtvalenz(V-V, X-X) :- var(V), !.

gesamtvalenz([H|T]-V1, [H|VN]-V3) :-
	normobjekt(H, [np, _, Typ, _, _, _, _]),
	nonvar(Typ), nomenvalenz(Typ, VN-V2), !, % Effizienz + einmalige Loesungen
	gesamtvalenz(T-V1, V2-V3).

gesamtvalenz([H|T]-V1, [H|VN]-V3) :-
	normobjekt(H, [np, _, Typ, _, _, _, _]),
	var(Typ), semantiktyp(Typ), nomenvalenz(Typ, VN-V2),
	gesamtvalenz(T-V1, V2-V3).

gesamtvalenz([H|T]-V1, [H|T2]-V2) :- gesamtvalenz(T-V1, T2-V2).

	% nur_fakultative(+Liste)
	% Ueberprueft, ob in Liste nur '?'/1-Phrasen oder keine Phrasen vorkommen.

nur_fakultative([]).
nur_fakultative(['?'(_)|T]) :- nur_fakultative(T).

	% leere_semantik(+Phrasen)
	% Setzt eine leere oder neutrale Semantik bei jedem Element von Phrasen. Eine leere oder neutrale Semantik ist eine Semantik, die sich bei Beta-Reduzierung auf eine freie Variabel oder '_' reduziert.
	% Phrasen = Liste mit fakultativen Phrasen

leere_semantik([]).
leere_semantik([?[np, _, _, _, _, lam(P, P*'_'), _]|T]) :- !, leere_semantik(T).
leere_semantik([?[pp, _, semester, _, _, _]|T]) :- !, leere_semantik(T).
leere_semantik([?[_, _, _, _, lam(P, P*'_'), _]|T]) :- leere_semantik(T).

	% unifiziere_objval(+Felder)
	% Unifiziert die Valenz der Satz-Objekte mit den Objekten im Satz. Da gesamtvalenz/2 die Valenzen der Phrasen allgemein und im Voraus berechnet, muss durch unifiziere_objval/1 die unspezifische Semantik der allgemeinen Phrasen mit der Semantik der real geparsten Objekte unifiziert werden. Das ist ueber die Valenz moeglich, da Valenz und Semantik im Grundformen-Lexikon zusammenhaengen.
	% Felder = Liste mit drei Elementen fuer je ein Feld: Vorfeld, Mittelfeld, Nachfeld.

unifiziere_objval([VF, MF, NF]) :-
	suggest_modus(aus), !,
	append(VF, MF, Z),
	append(Z, NF, Alle),
	unifiziere_objval(Alle, Rest),
	foreach(Rest, keine_objvalenz).

unifiziere_objval(_) :- \+suggest_modus(aus).

keine_objvalenz([Ph, Form, Typ|_]) :- nomenvalenz_ohne_cut(_, Val-[]), member_normobjekt([Ph, Form, Typ|_], Val), !, fail.
keine_objvalenz(_). 


	% unifiziere_objval(+Phrasen, -Rest)
	% Unifiziert die Objektvalenzen aller Phrasen. Nicht unifizierte Phrasen werden als Rest zurueckgegeben.
	
unifiziere_objval(Phrasen, Rest) :- unifiziere_objval(Phrasen, Phrasen, Rest).

unifiziere_objval([], X, X).

unifiziere_objval([H|T], Alle, RestT) :-
	normobjekt(H, [np, _, _, _, Val-[], _, _]),
	nonvar(Val), !,
	unifiziere_valenz(Val, Alle, Rest),
	unifiziere_objval(T, Rest, RestT).

unifiziere_objval([_|T], Alle, Rest) :- unifiziere_objval(T, Alle, Rest).


	% unifiziere_valenz(+Valenz, +Phrasen, -Rest)
	% Versucht jedes Element der Valenz mit genau einer Phrase in Phrasen zu unifizieren. Beim Backtracking werden Unifizierungen uebersprungen.
	% Jede Phrase, die nicht unifiziert worden ist, wird als Rest zurueckgegeben.

unifiziere_valenz([], X, X).

unifiziere_valenz([H|T], Alle, RestT) :-
	normobjekt(H, Obj),
	entferne(Obj, Alle, 1, Rest),
	unifiziere_valenz(T, Rest, RestT).

unifiziere_valenz([_|T], Alle, Rest) :- unifiziere_valenz(T, Alle, Rest).


	% feldobjekt(?Objekt, +Felder)
	% Ueberprueft, ob Objekt eine Phrase in einem der Felder ist.
	% Felder = Liste mit drei Elementen fuer je ein Feld: Vorfeld, Mittelfeld, Nachfeld.


feldobjekt(Obj_, [VF, MF, NF]) :-
	normobjekt(Obj_, Obj), (member(Obj, VF) ; member(Obj, MF) ; member(Obj, NF)).


	% unifiziere_zwingend(Liste1, Liste2)
	% Unifiziert alle Elemente der Liste1 mit Elementen der Liste2. Schlaegt fehl, wenn ein Element aus Liste1 nicht mit einem aus Liste2 unifiziert werden kann. Jedes Element wird mit normobjekt/2 normalisiert.

unifiziere_zwingend([], _).
unifiziere_zwingend([H|T], L) :- member_normobjekt(H, L), unifiziere_zwingend(T, L).


	% member_normobjekt(Element, Liste)
	% Unifiziert Element mit einem Element aus Liste. Schlaegt fehl, wenn dies nicht moeglich ist. Alle Elemente werden fuer den Vergleich mit normobjekt/2 normalisiert.

member_normobjekt(A, [B|_]) :- normobjekt(A, A2), normobjekt(B, B2), A2 = B2.
member_normobjekt(H, [_|T]) :- member_normobjekt(H, T).
