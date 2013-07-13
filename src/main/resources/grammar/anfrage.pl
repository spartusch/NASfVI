
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


	% parse

	% parse/2 erlaubt mehrere Lesformen, d. h. ist backtrack-bar

parse(Eingabe, [Markheit, [Inf, GV, Vst, Pers, Num, Temp], Felderstruktur, Semantik]) :-
	deaktiviere_suggest,
	tokenisiere(Eingabe, 0, Token), !,
	markiertheit(Markheit),
	Sterm = s([Markheit, [Inf, GV, Vst, Pers, Num, Temp], Felderstruktur, Sem_], _Atome),
	phrase(Sterm, Token, []),
	normalisiere_semantik(Sem_, Semantik).

	% parse/5 erlaubt nur eine Lesform

parse(Eingabe, Tempus, Query, SimilQuery, Gesucht) :-
	parse(Eingabe, [_Markheit, [_, _, _, _, _, Tempus], _Felderstruktur, Semantik]), !,
	uebersetze_semantik(Semantik, Query, SimilQuery, Gesucht).


	% suggest

suggest(Eingabe, Toleranz, Markiertheit, Vorschlaege) :-
	aktiviere_suggest,
	tokenisiere(Eingabe, 1, Token), !,
	markiertheit(Markiertheit),
	All = (
		intervall(Markiertheit, Toleranz, M),
		phrase(s([M|_], Atome), Token, [])
	),
	findall(Atome, All, Vorschlaege),
	\+(Vorschlaege = []), !.


	% beantworte(+Anfrage, +Werte, -AnalyseAnfrage, -AnalyseAntwort, -Antwort)
	% Parset Anfrage und erzeugt eine Antwort unter Einsetzung von Werte.
	% Werte = Paare aus Lucene-Feldernamen, welche eingesetzt werden sollen
	% Anfrage = Anfrage als ein Atom
	% Antwort = Atomfolge der generierten Antwort

beantworte(Anfrage, Werte, AnalyseAnfrage, [Markheit, [Inf, aktiv, v2, Pers, Num, Temp], Felder, Semantik], Antwort) :-
	deaktiviere_suggest,
	parse(Anfrage, AnalyseAnfrage),
	transform_analyse(AnalyseAnfrage, TransPhrasen),
	werte_einsetzen(TransPhrasen, Werte, Phrasen-[]),
	AnalyseAnfrage = [_, [Inf, _, _, Pers, _, Temp], _, _],
	markiertheit(Markheit),
	Sterm = s_gen(Phrasen, [Markheit, [Inf, aktiv, v2, Pers, Num, Temp], Felder, Sem_], Antwort),
	phrase(Sterm, _, []),
	normalisiere_semantik(Sem_, Semantik), !.


	% antwort(+AnalyseAnfrage, +Werte, -AnalyseAntwort, -Antwort)
	% Erzeugt eine Antwort aufgrund einer vorhandenen Analyse und Werte.
	% AnalyseAnfrage = Analysierte Eingabe, welche als Grundlage zur Generierung dient
	% Werte = Paare aus Lucene-Feldernamen, welche eingesetzt werden sollen
	% AnalyseAntwort = Generierte Antwort-Analyse mit eingesetzten Werten
	% Antwort = Atomfolge der generierten Antwort

antwort(AnalyseAnfrage, Werte, [Markheit, [Inf, aktiv, v2, Pers, Num, Temp], Felder, Semantik], Antwort) :-
	deaktiviere_suggest,
	transform_analyse(AnalyseAnfrage, TransPhrasen),
	werte_einsetzen(TransPhrasen, Werte, Phrasen-[]),
	AnalyseAnfrage = [_, [Inf, _, _, Pers, _, Temp], _, _],
	markiertheit(Markheit),
	Sterm = s_gen(Phrasen, [Markheit, [Inf, aktiv, v2, Pers, Num, Temp], Felder, Sem_], Antwort),
	phrase(Sterm, _, []),
	normalisiere_semantik(Sem_, Semantik), !.


	% Hilfspraedikate


	% zeige_analyse(+Analyse)
	% Einfaches Praediakt, das eine grobe Uebersicht ueber Analyse verschafft.

zeige_analyse([Markheit, Verbinfo, [VF, MF, NF], Semantik]) :-
	writeln('Markiertheit:'), writeln(Markheit), nl,
	writeln('Verbinformationen:'), writeln(Verbinfo), nl,
	writeln('Vorfeld:'), writeq(VF), nl, nl,
	writeln('Mittelfeld:'), writeq(MF), nl, nl,
	writeln('Nachfeld:'), writeq(NF), nl, nl,
	writeln('Semantik:'), writeq(Semantik), nl, nl, nl.
