
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


	% Flexion
	% Nomen

flexion(n>N, Grundform, Typ, Genus, Val, Sem, [sg, Cas], Vollform) :-
	casus(Cas),
	grundform(n>N, Grundform, Typ, Genus, [Flex, _|Zusatz], Val, Sem),
	(Zusatz = [] ; Zusatz = [Besonderheit]),
	endung(n, Flex, [sg, Cas], Endung_),
	besonderheit(n, Grundform, Besonderheit, [sg, Cas], Endung_, Endung),
	atom_concat(Grundform, Endung, Vollform).

flexion(n>N, Grundform, Typ, Genus, Val, Sem, [pl, Cas], Vollform) :-
	casus(Cas),
	grundform(n>N, Grundform, Typ, Genus, [_, Flex|Zusatz], Val, Sem),
	(Zusatz = [] ; Zusatz = [Besonderheit]),
	(
		(nonvar(Besonderheit), Besonderheit = umlaut:Pos)
			-> ersetze_durch_umlaut(Grundform, Pos, GF_)
			; GF_ = Grundform
	),
	endung(n, Flex, [pl, Cas], Endung_),
	besonderheit(n, Grundform, Besonderheit, [pl, Cas], Endung_, Endung),
	atom_concat(GF_, Endung, Vollform).


	% Flexion
	% Verben

	% Finite Formen
flexion(v>v, Infinitiv, Partikel, Val, Sem, [Vst, Pers, Num, Temp], Vollform) :-
	einfache_zeiten(Temp), numerus(Num), person(Pers),
	grundform(v>v, InfStamm, Partikel_, FlexKlasse, Val, Sem),
	atom_concat(Partikel_, InfStamm, Infinitiv),
	flektiere_stamm(InfStamm, [Pers, Num, Temp], FlexKlasse, FlexStamm),
	konjugiere(FlexStamm, Partikel_, FlexKlasse, [Vst, Pers, Num, Temp], Vollform, Partikel).

	% Partizip II
flexion(v>v, Infinitiv, '', Val, Sem, part2, Vollform) :-
	grundform(v>v, InfStamm, Partikel_, FlexKlasse, Val, Sem),
	atom_concat(Partikel_, InfStamm, Infinitiv),
	flektiere_stamm(InfStamm, [_, _, part2], FlexKlasse, FlexStamm),
	endung(v, FlexKlasse, part2, Endung),
	atom_concat('ge', FlexStamm, S1), % ge.fund
	atom_concat(Partikel_, S1, S2), % statt.gefund
	atom_concat(S2, Endung, Vollform). % stattgefund.en

	% Infinitiv
flexion(v>v, Infinitiv, '', Val, Sem, inf, Infinitiv) :-
	grundform(v>v, InfStamm, Partikel, _, Val, Sem),
	atom_concat(Partikel, InfStamm, Infinitiv).


	% flektiere_stamm(+InfinitivStamm, +Formmerkmale, +Flexionsklasse, -FlektierterStamm)
	% Flektiert den Infinitivstamm.
	% Flexionsklasse = Flexionsklasse, die in flexion_endungstabellen definiert sein muss
	% Formmerkmale = [Person, Numerus, Tempus]

flektiere_stamm(InfStamm, [Pers, Num, Temp], urg(Ablaute:Pos, FlexEig), FlexStamm) :-
	stamm(InfStamm, Stamm, _),
	ablaut(Temp, [Pers, Num], FlexEig, Ablaute, Ablaut),
	ersetze(Stamm, Pos, Ablaut, FlexStamm), !.

flektiere_stamm(InfStamm, [_, _, _], rg, FlexStamm) :- stamm(InfStamm, FlexStamm, _), !.

	% konjugiere(+Stamm, +Partikel_, +Flexionsklasse, +Formmerkmale, ?Vollform, ?Partikel)
	% Erzeugt flektierte Vollformen. Partikel kann leer sein, wenn Partikel_ an den Stamm gefuegt werden muss (z. B. im Perfekt).
	% Flexionsklasse = Flexionsklasse, die in flexion_endungstabellen definiert sein muss
	% Formmerkmale = [Verbstellung, Person, Numerus, Tempus]

konjugiere(Stamm, Partikel_, FlexKlasse, [Vst, Pers, Num, Temp], Vollform, Partikel) :-
	endung(v, FlexKlasse, [Pers, Num, Temp], Endung_),
	besonderheit(v, Stamm, FlexKlasse, [Pers, Num, Temp], Endung_, Endung),
	atom_concat(Stamm, Endung, Vollform_),
	(
		(Partikel_ \= '', Vst = vl)
		->	(atom_concat(Partikel_, Vollform_, Vollform), Partikel = '')
		;	(Vollform = Vollform_, Partikel = Partikel_)
	).

	% ablaut(+Tempus, +Formmerkmale, +Flexionseigenschaft, +Ablautreihe, ?Ablaut)
	% Gibt den passenden Ablaut zurueck.
	% Formmerkmale = [Person, Numerus]
	% Flexionseigenschaft = In lexikon_grundformen definiert

	% Tempus an Anfang gestellt, wegen "First argument indexing"
ablaut(praes, [Pers, sg], eiw, [ e, _, _], i) :- (Pers = 2; Pers = 3), !.
ablaut(praes, [Pers, sg], uml, [V1, _, _], V) :- (Pers = 2; Pers = 3), umlaut(V1, V), !.
ablaut(praes, _, _, [V1, _, _], V1).
ablaut(praet, _, _, [_, V2, _], V2).
ablaut(part2, _, _, [_, _, V3], V3).

	% stamm(+Infinitiv, -Stamm, -Endung)
	% Spaltet Verb-Infinitivendungen vom Infinitiv ab.

stamm(Infinitiv, Stamm, 'en') :- atom_concat(Stamm, 'en', Infinitiv), !.
	% e-Tilgung im Infinitiv (Duden-Grammatik, 620):
stamm(Infinitiv, Stamm, 'n') :- atom_concat(Stamm, 'n', Infinitiv).


	% besonderheit(+Wortart, +Stamm, +Flexionsklasse, +Formmerkmale, +Endung_, ?Endung)
	% Faengt Besonderheiten bei der Flexion der Endungen ab.
	% Wortart = n oder v
	% Formmerkmale = [Numerus, Casus] bei n oder [Person, Numerus, Tempus] bei v
	% Endung_ = regelmaessige Endung
	% Endung = Endung, die die Besonderheiten beruecksichtigt

	% Nomen

	% n-Verdoppelung: -nen bei fem, -in und Pluralendung -en (Dozentin -> Dozentinnen)
besonderheit(n, Gf, _, [pl, _], en, nen) :- atom_concat(_, in, Gf), !.

	% e-Tilgung: -n/-en bei unbetontem -err und Endung -en (Herr -> Herrn/Herren)
besonderheit(n, Gf, unb, [sg, _], en, Endung) :- atom_concat(_, err, Gf), !, (Endung = n ; Endung = en).

	% e-Einfuegung bei sg, gen und Genitiv-Endung s, wenn Stamm auf -s endet (Kurs -> Kurses)
besonderheit(n, Gf, _, [sg, gen], s, es) :- atom_concat(_, s, Gf), !.

	% Verben

	% e-Einschub (Duden-Grammatik, 617 und 641)
besonderheit(v, haelt, urg(_,uml), [3, sg, praes], 't', '') :- !.
besonderheit(v, Stamm, FlexKlasse, [Pers, Num, Temp], Endung_, Endung) :-
	(atom_concat(_, 't', Stamm) ; atom_concat(_, 'd', Stamm)), % Dentalstamm
	member(Endung_, ['t', 'st']),
	\+((
		member([Pers, Num, Temp], [[2, sg, praes], [3, sg, praes]]),
		member(FlexKlasse, [urg(_,uml), urg(_,eiw)])
	)),
	atom_concat('e', Endung_, Endung), !.

	% e-Tilgung (Duden-Grammatik, 620)
besonderheit(v, Stamm, rg, [_, _, praes], 'en', 'n') :-
	(atom_concat(_, 'el', Stamm) ; atom_concat(_, 'er', Stamm)), !.

	% keine Besonderheit
besonderheit(_, _, _, _, Endung, Endung).


	% Allgemeine Hilfspraedikate

	% umlaut(?Vokal, ?Umlautung)

umlaut(a, ae).
umlaut(o, oe).
umlaut(u, ue).

	% spalte(+Liste, +Position, ?Liste1, ?Liste2)
	% Spaltet Liste an Position in Liste1 und Liste2. Das Zeichen an Position in Liste ist das erste Zeichen von Liste2.
	% Position = Position, an der gespalten werden soll. Muss 1 oder groesser sein!

spalte(L, 1, [], L) :- !.
spalte([H|T], Position, [H|A], B) :- Pos_ is Position - 1, spalte(T, Pos_, A, B).

	% spalte_an_zeichen(+Atom, +Position, ?Teil1, ?Zeichen, ?Teil2)
	% Spaltet Atom an Position in Teil1, Zeichen und Teil2.
	% Atom = Eingabe-Atom
	% Position = Position, an der gespalten werden soll. Muss 1 oder groesser sein!
	% Teil1 = Zeichen vor Position als Atom
	% Zeichen = Das Zeichen an Position
	% Teil2 = Zeichen nach Position als Atom

spalte_an_zeichen(Atom, Position, Teil1, Zeichen, Teil2) :-
	atom_chars(Atom, Liste),
	spalte(Liste, Position, Liste1, [Zeichen|Liste2]),
	atom_chars(Teil1, Liste1),
	atom_chars(Teil2, Liste2).

	% ersetze(+Atom, +Position, +NeuerVokal, ?Ergebnis)
	% Ersetzt das Zeichen an Position in Atom durch NeuerVokal.
	% Ergebnis = Atom mit ersetztem Zeichen

ersetze(Atom, Position, NeuerVokal, Ergebnis) :-
	spalte_an_zeichen(Atom, Position, Teil1, _AlterVokal, Teil2),
	atom_concat(Teil1, NeuerVokal, Praefix),
	atom_concat(Praefix, Teil2, Ergebnis).

	% ersetze_durch_umlaut(+Atom, +Position, ?Ergebnis)
	% Ersetzt in Atom einen Vokal an Position durch dessen Umlautung.

ersetze_durch_umlaut(Atom, Position, Ergebnis) :-
	spalte_an_zeichen(Atom, Position, Teil1, Vokal, Teil2),
	umlaut(Vokal, Umlaut),
	atom_concat(Teil1, Umlaut, Praefix),
	atom_concat(Praefix, Teil2, Ergebnis).
