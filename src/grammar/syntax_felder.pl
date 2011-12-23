
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


	% vorfeld(-Phn, ?P1-P2, ?A1-A2, ?Rest)
	% Phn = Im Satz erkennbare Phrasen
	% P1-P2 = Differenzliste mit Abfolge der im Vorfeld erkannten Phrasen
	% A1-A2 = Differenzliste mit Abfolge der im Vorfeld erkannten Atome

vorfeld([NP, PP|Rest], [NP, PP|D]-D, A1-A3, Rest)
	--> {NP = [np, _, event|_], PP = [pp, _, thema|_]},
		np(NP, A1-A2),
		pp(PP, A2-A3).

vorfeld([Ph|Rest], [Ph|P2]-P2, A1-A2, Rest)
	--> obj([Ph|P2]-P2, A1-A2).

	% kein member zur Phrasenauswahl wie bei mittelfeld: festgelegte Reihenfolge bei gegebenen Phrasen (Thema-Rhema)


	% mittelfeld(?Phn, ?P1-P3, ?A1-A3, ?Rest)
	% Phn = Im Mittelfeld und restlichen Satz erkennbare Phrasen
	% P1-P3 = Differenzliste mit Abfolge der im Mittelfeld erkannten Phrasen
	% A1-A3 = Differenzliste mit Abfolge der im Mittelfeld erkannten Atome
	% Rest = Phn abzueglich der im Mittelfeld erkannten Phrasen

mittelfeld(Phn, []-[], A1-A1, Phn) --> {true}. % Leeres bzw kurzes Mittelfeld bevorzugen

mittelfeld(Phn, [P1N|P2]-P3, A1-A3, Rest)
	--> {var(P1), member(P1, Phn), normobjekt(P1, P1N)},
		obj([P1N|P2]-P2, A1-A2),
		{entferne_liste([P1N], Phn, Rest_)},
		mittelfeld(Rest_, P2-P3, A2-A3, Rest).

	% mittelfeld(+Temp, +GenusVerbi, ?Phrasen, ?PhrasenErkannt, ?Atome, ?Rest)
	% Wie mittelfeld/4, jedoch um Temp und GenusVerbi erweitert. Bei einfachen Zeiten (Praesens, Praeteritum) im aktiv kein Unterschied zu mittelfeld/4. Bei zusammengesetzten Zeiten wird jedoch zwingend eine Konstituente und ein folgendes Token verlangt.
	% Temp = Tempus des Verbs

mittelfeld(Temp, aktiv, Phrasen, PhnErkannt, Atome, Rest)
	--> {einfache_zeiten(Temp)}, !, mittelfeld(Phrasen, PhnErkannt, Atome, Rest).

mittelfeld(_, _, Phn, [P1N|P2]-P3, A1-A3, Rest)
	--> {var(P1), member(P1, Phn), normobjekt(P1, P1N)},
		obj([P1N|P2]-P2, A1-A2),
		{entferne_liste([P1N], Phn, Rest_)},
		\+generierung(suggest),
		mittelfeld(Rest_, P2-P3, A2-A3, Rest).


	% nachfeld
	% Ist noch nicht implementiert.

nachfeld --> {true}.


	% lsk(?Formmerkmale, ?Valenz, ?Semantik, ?Infinitiv, ?Zeichenliste, ?Atom, ?Atome)
	% Linke Satzklammer. Waehlt immer ein Verb aus.
	% Formmerkmale = [Genus Verbi, Verbstellung, Person, Numerus, Tempus]
	% Valenz = Valenz des Verbs
	% Semantik = Semantik des Verbs
	% Infinitiv = Infinitiv der Semantik-tragenden Verbform
	% Zeichenliste = Vollform von Verb ODER Partikel als Zeichenliste fuer lsk
	% Atom = Vollformen von Verb ODER Partikel als Atom fuer lsk
	% Atome = Differenzliste mit der in der Satzklammer erkannten Atome

	% Aktiv, Vollverb

lsk([aktiv, Vst, Pers, Num, Temp], Val, Sem, Inf, LP, AP, [AV|Diff]-Diff)
	--> {einfache_zeiten(Temp)},
		[LV], v([LV, LP], [aktiv, Vst, Pers, Num, Temp], Val, Sem, Inf, [AV, AP]).

	% Aktiv, Hilfsverb haben
	% praes: hat
	% praet: hatte

lsk([aktiv, Vst, Pers, Num, Temp], Val, Sem, Inf, LV, AV, [AAux|Diff]-Diff)
	--> {Vst \= vl},
		v_aux(haben, [aktiv, Vst, Pers, Num, Taux], AAux),
		{zeiten_aux-partizip(Taux, Temp)},
		v([LV, []], [aktiv, part2, [Pers, Num]], Val, Sem, Inf, [AV, '']).

	% Aktiv und Passiv, Hilfsverb werden, Futur 1
	% aktiv: wird (stattfinden)
	% passiv: wird (gehalten werden)

lsk([GV, Vst, Pers, Num, fut1], Val, Sem, Inf, LV, AV, [AAux|Diff]-Diff)
	--> {genusverbi(GV), Vst \= vl},
		v_aux(werden, [GV, Vst, Pers, Num, praes], AAux),
		(
			{(GV = aktiv)}
				-> v([LV, []], [aktiv,  inf,   [Pers, Num]], Val, Sem, Inf, [AV, ''])
				;  v([LV, []], [passiv, part2, [Pers, Num]], Val, Sem, Inf, [AV, ''])
		).

	% Passiv, Hilfsverb werden
	% praes: wird
	% praet: wurde

lsk([passiv, Vst, Pers, Num, Temp], Val, Sem, Inf, LV, AV, [AAux|Diff]-Diff)
	--> {Vst \= vl, einfache_zeiten(Temp)},
		v_aux(werden, [passiv, Vst, Pers, Num, Temp], AAux),
		v([LV, []], [passiv, part2, [Pers, Num]], Val, Sem, Inf, [AV, '']).

	% Passiv, Hilfsverb sein
	% perf: ist
	% pqperf: war

lsk([passiv, Vst, Pers, Num, Temp], Val, Sem, Inf, LV, AV, [AAux|Diff]-Diff)
	--> {Vst \= vl, (Temp = perf ; Temp = pqperf)},
		v_aux(sein, [passiv, Vst, Pers, Num, Taux], AAux),
		{zeiten_aux-partizip(Taux, Temp)},
		v([LV, []], [passiv, part2, [Pers, Num]], Val, Sem, Inf, [AV, '']).


	% rsk(+Formmerkmale, +Zeichenliste, +Atom, ?Atome)
	% Rechte Satzklammer. Erkennt Partikel oder Partizip II des in lsk gewaehlten Verbs.
	% Atome = Differenzliste mit der in der Satzklammer erkannten Atome

	% Aktiv, kein Partikel, leere rechte Satzklammer

rsk([aktiv, _, _, _, Temp], [], '', A-A) --> {einfache_zeiten(Temp), !}.

	% Aktiv, Partikel

rsk([aktiv, Vst, _, _, Temp], LP, AP, [AP|Diff]-Diff)
	--> {Vst \= vl, einfache_zeiten(Temp)}, ergaenze_vollform(LP).

	% Aktiv, Futur 1

rsk([aktiv, _, _, _, fut1], LV, AV, [AV|Diff]-Diff) --> ergaenze_vollform(LV).

	% Passiv, Futur 1

rsk([passiv, _, _, _, fut1], LV, AV, [AV,AAux|Diff]-Diff)
	-->	ergaenze_vollform(LV), v_aux(werden, [passiv, inf, _], AAux).

	% Aktiv und Passiv, Partizip II
	% aktiv bei perf, pqperf
	% passiv bei praes, praet und bei perf + pqperf zusaetzlich mit worden

rsk([aktiv, _, _, _, Temp], LV, AV, [AV|Diff]-Diff)
	--> {(Temp = perf ; Temp = pqperf), !}, ergaenze_vollform(LV).

rsk([passiv, _, _, _, Temp], LV, AV, [AV|Diff]-Diff)
	--> {(Temp = praes ; Temp = praet)}, ergaenze_vollform(LV).

rsk([passiv, _, _, _, Temp], LV, AV, [AV,AAux|Diff]-Diff)
	--> {(Temp = perf ; Temp = pqperf)},
		ergaenze_vollform(LV), v_aux(werden, [passiv, part2, _], AAux).


	% ergaenze_vollform(+Vollform)
	% Zur Verwendung in rsk. Um Backtracking ueber das Mittelfeld zu vermeiden, fuehrt ergaenze_vollform/1 eine Praefix-Erkennung durch. (Die Praefix-Erkennung erfolgt sonst eigentlich beim Lexikonaufruf in lsk.)

ergaenze_vollform(LV) -->
	{nonvar(LV), LV \= []},
	[LV_], {LV_ = LV ; (LV_ = ['#'|LV__], LV__ \= [], praefix_von(LV__, LV))}.
