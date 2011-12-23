
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


	% constraint(+Verbinfo, +Felder, +Atome, -Markiertheit)
	% Beschreibt Constraints und deren "Markiertheitswert". Ist "true", wenn die Bedingung verletzt ist!
	% Verbinfo = [Infinitiv, Genus Verbi, Verbstellung, Person, Numerus, Tempus]
	% Felder = [Phrasen Vorfeld, Phrasen Mittelfeld, Phrasen Nachfeld]

	% Prinzip: Passiv
constraint([_, passiv|_], _, _, 5).

	% Prinzip: Erstes Objekt sollte Nom-NP sein
constraint([_, _, v2|_], [[N|_], _, _], _, 3) :- N \= [np, [_, _, nom]|_].
constraint([_, _, v1|_], [_, [N|_], _], _, 5) :- N \= [np, [_, _, nom]|_].

	% Prinzip: Erstes Objekt sollte qu-AdvP sein
constraint([_, _, v2|_], [[A|_], MF, _], _, 6) :- A \= [advp, _, _, qu|_], member([advp, _, _, qu|_], MF).

	% Prinzip: qu-AdvP sollte nur mit v2 verwendet werden
constraint([_, _, v1|_], [_, MF, _], _, 6) :- member([advp, _, _, qu|_], MF).

	% Prinzip: Wenn im Mittelfeld NP-hum vorkommt, soll es an erster Position stehen
constraint(_, [_, [N|T], _], _, 5) :- N \= [np, _, hum|_], member([np, _, hum|_], T).

	% Prinzip: Wenn im Mittelfeld Expletiv vorkommt, muss es an erster Position stehen
constraint(_, [_, [N|T], _], _, 50) :- N \= [np, _, expl|_], member([np, _, expl|_], T).

	% Prinzip: Expletiv steht im Mittelfeld zwar an erster Stelle, kommt aber nochmals vor
%constraint(_, [_, [[np, _, expl|_]|T], _], _, 50) :- member([np, _, expl|_], T).

	% Prinzip: indef/event-NP ohne thema-PP -> 3
	% Prinzip: def/event-NP mit thema-PP -> 5
constraint(_, Felder, _, M) :-
	feldobjekt([np, _, event, Def|_], Felder),
	(
		(Def == indef, \+feldobjekt([pp, _, thema|_], Felder), M = 3)
		; (Def == def, feldobjekt([pp, _, thema|_], Felder), M = 5)
	).

	% Prinzip: "Gibt es"-Fragen nur mit hum- und event-NPs
constraint([geben|_], Felder, _, 15) :-
	feldobjekt([np, _, expl|_], Felder),
	feldobjekt([_, _, Typ|_], Felder), \+member(Typ, [expl, hum, event, thema]).

	% Prinzip: Eine thema-PP sollte direkt auf die event-NP folgen, auf die sie sich bezieht.
	% Bezieht sich auf alle event-NPs in einem Satz!
constraint(_, [VF, MF, NF], _, 5) :-
	\+suggest_modus(an),
		% event-NP und Vorkommen von thema-PP ermitteln
	NP = [np, _, event, _, Val-[]|_],
	PP = [pp, Form, thema, _, Sem|_],
	feldobjekt(NP, [VF, MF, NF]),
	\+(\+feldobjekt(PP, [VF, MF, NF])),
		% thema-PP, die sich auf event-NP bezieht
	nonvar(Val),
	member_normobjekt(PP, Val),
		% Stellung pruefen
	\+((
		(finde_paar(VF, NP, PP2) ; finde_paar(MF, NP, PP2) ; finde_paar(NF, NP, PP2)),
		PP2 = [pp, Form, thema, _, Sem2|_],
		Sem == Sem2
	)).


	% eval(+Analyse, +Atome, ?Markiertheit)
	% Berechnet die Markiertheit der Analyse/Atome.
	% Analyse = Die Analyse des Satzes
	% Atome = Lineare Abfolge der Atome des Satzes
	% Markiertheit = Die ermittelte Markiertheit des Satzes

	% Beinhaltet "Prinzip: Laenge des Satzes"

eval([_, Verbinfo, Felder, _], Atome, Markheit) :-
	findall(M, constraint(Verbinfo, Felder, Atome, M), Ms),
	sumlist(Ms, M_Summe),
	length(Atome, Len),
	Markheit is M_Summe + Len.

	% max_markiertheit(?MaxMarkiertheit)
	% Die maximal erlaubte Markiertheit.

max_markiertheit(30).

	% markiertheit(-M)
	% M = Eine Zahl zwischen 0 und max_markiertheit.

markiertheit(M) :- max_markiertheit(Max), zahl(M), (M =< Max -> true ; !, fail).
