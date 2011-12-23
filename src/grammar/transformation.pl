
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


	% typ_entspr(SemantikTyp, LuceneFeld)
	% Entsprechungen von Semantik-Typen zu Feldernamen in Lucene.

typ_entspr(hum, dozent).
typ_entspr(event, titel).
typ_entspr(thema, beschreibung).
typ_entspr(semester, semester).
typ_entspr(loc, raum).		% "virtuelles" Feld
typ_entspr(temp_d, tag).	% "virtuelles" Feld

	% transform_analyse(+Analyse, -Phrasen)
	% Ueberfuehrt die Felder in eine Liste und transformiert Passiv zu Aktiv.
	% Phrasen = Differenzliste mit den in der Analyse vorkommenden, transformierten Phrasen

transform_analyse([_, [_Inf, GV|_], [VF, MF, NF], _], Ph-D) :-
	transform_liste(GV, VF, VF2-MF2, Subj),
	transform_liste(GV, MF, MF2-NF2, Subj),
	transform_liste(GV, NF, NF2-D, Subj),
	( (GV = passiv, var(Subj))
		% kein Aktiv-Subjekt vorhanden -> "jemand" einfuegen
		-> Ph = [[np, [3, sg, nom], hum, qu, _, _, [[pro>qu, [sg, nom], hum, _, _, _, 'jemand', _]]]|VF2]
		; Ph = VF2
	).


	% werte_einsetzen(+Phrasen, +Werte, -Ergebnis)
	% Interrogative Phrasen werden durch Phrasen mit den uebergebenen Blackbox-Werten ersetzt.
	% Werte = Blackbox-Werte. Liste aus Lucene-Feldbezeichnungen und den einzusetzenden Werten.

werte_einsetzen(X, [], X).

werte_einsetzen(Phrasen, [Wert|T], Ergebnis) :-
	ersetze_interrogativ(Phrasen, Wert, Erg_),
	werte_einsetzen(Erg_, T, Ergebnis).


	% ersetze_interrogativ(+Phrasen, +Wert, -Ergebnis)
	% Entfernt eine passende interrogative Phrase und fuegt stattdessen an das ENDE der Phrasenliste eine neue Phrase mit einer Blackbox und Atom als Token.
	% Die Einfuegung erfolgt am Ende, um das Rhema nach hinten und das Thema nach vorne zu stellen.
	% Phrasen = Differenzliste mit den zu bearbeitenden Phrasen
	% Wert = Liste aus [LuceneFeld, Atom1, Atom2, ...]

ersetze_interrogativ(Phrasen, [LuceneFeld|Werte], Rest-D2) :-
	typ_entspr(Typ, LuceneFeld),
	Ph = [_, _, Typ, qu|_],				% irgendeine Interrogativ-Phrase
	entferne_d(Ph, Phrasen, 1, Rest-D),	% unifiziere mit vorhandener Phrase und entferne selbige
	komplexe_phrase(Ph, Werte, D, D2).	% erzeuge neue Phrase und setze sie ein

komplexe_phrase([advp, Syn, Typ|_], Werte, D, D2) :-
	komplexe_phrase([pp, Syn, Typ|_], Werte, D, D2).

komplexe_phrase(Ph, [Atom], D, D2) :-
	einfache_phrase(Ph, Atom, Ph2),
	D = [Ph2|D2].

	% qu wegen komplexer Phrasen nicht vorgegeben!

	% Die Dozenten X und Y
komplexe_phrase([np, [Pers, _, Cas], Typ|_], Werte, D, D2) :-
	Typ \= event,	% Reine Veranstaltungstitel schoener
	komplexe_phrase([blackbox, Typ, _, _], Werte, [D0], []),
	CPh = [np, [Pers, pl, Cas], Typ, def, _, _, [_Art, _N, D0]],
	!, D = [CPh|D2].

komplexe_phrase([blackbox, Typ, _, _], [Atom|Weiter], D, D2) :-
	PhraseA = [blackbox, Typ, Atom],
	komplexe_phrase([blackbox, Typ, _, _], Weiter, [PhraseB], []),
	CPh = [blackbox, Typ, _, [PhraseA, _, PhraseB]],
	D = [CPh|D2].

	% Dozent X und Dozent Y
komplexe_phrase([np, [Pers, _, Cas], Typ|_], [Atom|Weiter], D, D2) :-
	einfache_phrase([np, [Pers, sg, Cas], Typ, def|_], Atom,   PhraseA),
	komplexe_phrase([np, [Pers, _,  Cas], Typ, def|_], Weiter, [PhraseB], []),
	CPh = [np, [Pers, pl, Cas], Typ, def, _, _, [PhraseA, _, PhraseB]],
	D = [CPh|D2].

	% am Montag und am Dienstag
komplexe_phrase([pp, [Cas], Typ|_], [Atom|Weiter], D, D2) :-
	einfache_phrase([pp, [Cas], Typ, def|_], Atom,   PhraseA),
	komplexe_phrase([pp, [Cas], Typ, def|_], Weiter, [PhraseB], []),
	CPh = [pp, [Cas], Typ, def, _, [PhraseA, _, PhraseB]],
	D = [CPh|D2].

	% in dem Raum A und dem Raum B
komplexe_phrase([pp, [Cas], Typ|_], Werte, D, D2) :-
	komplexe_phrase([np, [_, _, Cas], Typ, def|_], Werte, [NP], []),
	CPh = [pp, [Cas], Typ, def, _, [_P, NP]],
	D = [CPh|D2].

einfache_phrase([np, [Pers, _, Cas], Typ|_], Atom, [np, [Pers, sg, Cas], Typ, def, _, _, Baum2]) :-
	np([np, [Pers, sg, Cas], Typ, def, _, _, Baum], _, _, []),
	member([blackbox, Typ, Atom], Baum), entferne_semantik(Baum, Baum2).

einfache_phrase([pp, Syn, temp_d|_], Atom, [pp, Syn, temp_d, def, _, [_, InfoTag]]) :-
	InfoTag = [n>n, _, temp_d, _, _, lam(X, tag(X, Atom)), _, _],
	pp([pp, Syn, temp_d, def, _, [_, InfoTag]], _, _, []).

einfache_phrase([pp, Syn, semester|_], Atom, [pp, Syn, semester, def, _, Baum2]) :-
	pp([pp, Syn, semester, def, _, Baum], [_,_,Atom]-[], _, []),
	member([angabe, semester>_, Atom], Baum), entferne_semantik(Baum, Baum2).

einfache_phrase([pp, Syn, Typ|_], Atom, [pp, Syn, Typ, def, _, Baum2]) :-
	pp([pp, Syn, Typ, def, _, Baum], _, _, []),
	member([blackbox, Typ, Atom], Baum), entferne_semantik(Baum, Baum2).

einfache_phrase([blackbox, Typ, _, _], Atom, [blackbox, Typ, _, Baum2]) :-
	cb([blackbox, Typ, _, Baum], _, _, []),
	member([blackbox, Typ, Atom], Baum), entferne_semantik(Baum, Baum2).


	% transform_liste(+GenusVerbi, +EingabeFeld, -AusgabeFeld, -Subjekt)
	% Fuehrt fuer jede Phrase des Feldes eine Transformation mit transform/3 durch.
	% EingabeFeld = Liste der Phrasen des zu verarbeitenden Feldes
	% AusgabeFeld = Differenzliste der transformierten Phrasen
	% Subjekt = 1, wenn Subjekt aus Passiv erzeugt; freie Variabel andernfalls

transform_liste(_, '', D-D, _).	% kein VF
transform_liste(_, [], D-D, _) :- (var(D) ; D = []), !.
transform_liste(GV, [H|T], [H2|T2]-D, Subj) :- transform(GV, H, H2, Subj), transform_liste(GV, T, T2-D, Subj).


	% transform(+GenusVerbi, +Eingabe, -Ausgabe, -Subjekt)
	% Entfernt die Valenz und Semantik der Phrasen, transformiert Passiv-Phrasen zu Aktiv-Phrasen.
	% Eingabe = Phrase, die transformiert werden soll
	% Ausgabe = Transformierte Phrase
	% Subjekt = 1, wenn Subjekt aus Passiv erzeugt; freie Variabel andernfalls

transform(passiv, [pp, [dat], _, _, _, [[p>p|_], NP]], [np, [Pers, Num, nom], hum, Def, _, _, Baum2], 1) :-
	NP = [np, [Pers, Num, dat], hum, Def, _, _, Baum], !, setze_kasus(nom, Baum, Baum2).

transform(passiv, [np, [Pers, Num, nom], Typ, Def, _, _, Baum], [np, [Pers, Num, akk], Typ, Def, _, _, Baum2], _) :-
	!, setze_kasus(akk, Baum, Baum2).

transform(_, [np, Syn, Typ, Def, _, _, Baum], [np, Syn, Typ, Def, _, _, Baum], _).
transform(_, [Ph, Syn, Typ, Def, _, Baum], [Ph, Syn, Typ, Def, _, Baum], _).


	% setze_kasus(+Kasus, +Eingabe, -Ausgabe)
	% Geht den Syntaxbaum von Eingabe durch und kopiert die Eintraege, setzt jedoch immer Kasus als Kasus.
	% Entfernt auch Valenz und Semantik der Eintrage.

setze_kasus(_, [], []) :- !.

	% Komplexe NPs
setze_kasus(Cas, [[np, [Pers, Num, _], Typ, Def, _, _, Baum]|T],
	[[np, [Pers, Num, Cas], Typ, Def, _, _, Baum2]|T2]) :-
	!, setze_kasus(Cas, Baum, Baum2),
	setze_kasus(Cas, T, T2).

	% Nomen, Pronomen
setze_kasus(Cas, [[POS, [Num, _], Typ, Gen, _, _, Gf, _]|T], [[POS, [Num, Cas], Typ, Gen, _, _, Gf, _]|T2]) :-
	!, setze_kasus(Cas, T, T2).
	% Restliche Token/Terminalknoten
setze_kasus(Cas, [[POS, [Num, _], Typ, Gen, _, Gf, _]|T], [[POS, [Num, Cas], Typ, Gen, _, Gf, _]|T2]) :-
	!, setze_kasus(Cas, T, T2).

setze_kasus(Cas, [X|T], [X|T2]) :- setze_kasus(Cas, T, T2).


	% entferne_semantik(+Eingabe, -Ausgabe)
	% Entfernt die Semantik der Token-Eintraege. Sicherheitsmassnahme fuer das Generieren aufgrund der freien Variabeln.

entferne_semantik([], []) :- !.
	% Nomen, Pronomen
entferne_semantik([[POS, Form, Typ, Gen, _, _, Gf, Atom]|T], [[POS, Form, Typ, Gen, _, _, Gf, Atom]|T2]) :- !, entferne_semantik(T, T2).
	% Rest
entferne_semantik([[POS, Form, Typ, Art, _, Gf, Atom]|T], [[POS, Form, Typ, Art, _, Gf, Atom]|T2]) :- !, entferne_semantik(T, T2).
entferne_semantik(X, X).
