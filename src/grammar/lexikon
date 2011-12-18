
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


:- op(100, xfx, '>').
:- dynamic suggest_modus/1.

	% Die lex-Praedikate schlagen Lexikoneintraege im Vollformenlexikon nach und koennen Praefixe zu Vollformen ergaenzen. Sie ermoeglichen so die Suggest-Funktionalitaet.

	% Praefixe werden jedoch nur ergaenzt, wenn:
	% a) sie mit dem Zeichen '#' beginnen (siehe vorverarbeitung)
	% b) nicht vollstaendig generiert wird (ein Praefix muss also gegeben sein)
	% c) das Praefix keinen Eintrag im Vollformenlexikon hat


	% Verben

	% lex(v>v, ?Zeichenlisten, ?Formmerkmale, ?Valenz, ?Semantik, ?Infinitiv, ?Atome)
	% Ruft lex_/7 oder existiert_nicht/3 auf.
	% Zeichenlisten = Vollformen von Verb und Partikel als Listen von Buchstaben
	% Atome = Vollformen von Verb und Partikel als Atome
	% Valenz = Valenz des Verbs

	% lex/7

	% Infinite Verbform

lex(v>Sub, Zeichenlisten, [GenusVerbi, Form, [Pers, Num]], Val, Sem, Infinitiv, Atome) :-
		% [Pers, Num] fuer Kongruenz mit Subjekt des Valenzrahmens
		% Form = inf oder part2
	indefinite(Form),
	lex_(v>Sub, Zeichenlisten, Form, Val_, Sem, Infinitiv, Atome),
	genusverbi(GenusVerbi),
	expandiere_valenz(GenusVerbi, Val_, Val, [Pers, Num]).

	% Finite Verbform

lex(v>Sub, Zeichenlisten, [GenusVerbi, Vst, Pers, Num, Temp], Val, Sem, Infinitiv, Atome) :-
	verbstellung(Vst, Vst_),
	lex_(v>Sub, Zeichenlisten, [Vst_, Pers, Num, Temp], Val_, Sem, Infinitiv, Atome),
	genusverbi(GenusVerbi),
	expandiere_valenz(GenusVerbi, Val_, Val, [Pers, Num]).

	% Fehlerbehandlung

lex(v>Sub, [LVerb, _], _, _, _, _, [AVerb, _]) :- existiert_nicht(v>Sub-lex/7, LVerb, AVerb).


	% lex_/7
	% Macht die eigentliche Arbeit. Vermeidet Linksrekursion in existiert_nicht/3. Cuts werden nicht verwendet, um alternative Eintraege zuzulassen.

	% Ergaenzung: Vollen Match versuchen

lex_(v>Sub, [['#'|LVerb], LPartikel], Formmerkmale, Val, Sem, Infinitiv, Atome) :-
	nonvar(LVerb), LVerb \= [],
	vollform(v>Sub, [LVerb, LPartikel], Formmerkmale, Val, Sem, Infinitiv, Atome).

	% Ergaenzung: Praefix versuchen

lex_(v>Sub, [['#'|LVerb], LPartikel], Formmerkmale, Val, Sem, Infinitiv, Atome) :-
	nonvar(LVerb), LVerb \= [],
	\+vollform(v>Sub, [LVerb, LPartikel], Formmerkmale, Val, Sem, Infinitiv, Atome),
	vollform(v>Sub, [LVerb_, LPartikel], Formmerkmale, Val, Sem, Infinitiv, Atome),
	praefix_von(LVerb, LVerb_).

	% Keine Ergaenzung

lex_(v>Sub, Zeichenlisten, Formmerkmale, Val, Sem, Infinitiv, Atom) :-
	vollform(v>Sub, Zeichenlisten, Formmerkmale, Val, Sem, Infinitiv, Atom).


	% Nomen und Pronomen

	% lex(n>N, ?LVollform, ?Formmerkmale, ?Typ, ?Genus, ?Valenz, ?Semantik, ?Grundform, ?AVollform)
	% Ruft lex_/9 oder existiert_nicht/3 auf.
	% LVollform = Vollform als Zeichenliste
	% AVollform = Vollform als Atom
	% Valenz = Valenz des Nomens

	% lex/9

	% Schlaegt n>n nach, transformiert aber auch n>app zu n>n!
lex(n>n, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	lex_(n>Sub, LVollform, Formmerkmale, Typ, Genus, Val, Sem_, Grundform, AVollform),
	((Sub = app)
		-> Sem = Sem_ * '' % leeren Namen einsetzen
		; Sem = Sem_
	).

lex(n>app, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	lex_(n>app, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform).

lex(pro>Sub, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	lex_(pro>Sub, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform).

lex(POS, LVollform, _, _, _, _, _, _, AVollform) :- existiert_nicht(POS-lex/9, LVollform, AVollform).


	% lex_/9

lex_(POS, ['#'|LVollform], Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	(POS = n>_ ; POS = pro>_),
	nonvar(LVollform),
	LVollform \= [],
	vollform(POS, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform).

lex_(POS, ['#'|LVollform], Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	(POS = n>_ ; POS = pro>_),
	nonvar(LVollform), LVollform \= [],
	\+vollform(POS, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform),
	vollform(POS, LVollform_, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform),
	praefix_von(LVollform, LVollform_).

lex_(POS, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform) :-
	(POS = n>_ ; POS = pro>_),
	vollform(POS, LVollform, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AVollform).


	% Sonstige Wortarten (Artikel, Pronomen, Praepositionen)

	% lex(?Wortart, ?LVollform, ?Formmerkmale, ?Typ, ?Artmerkmal, ?Semantik, ?Grundform, ?AVollform)
	% lex_(?Wortart, ?LVollform, ?Formmerkmale, ?Typ, ?Artmerkmal, ?Semantik, ?Grundform, ?AVollform)
	% Behandelt alle Wortarten ausser Verben (lex/7) und Nomen (lex/9).
	% LVollform = Vollform als Zeichenliste
	% AVollform = Vollform als Atom

	% lex/8

lex(POS, LVollfrom, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform) :-
	lex_(POS, LVollfrom, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform).

lex(POS, LVollform, _, _, _, _, _, AVollform) :- existiert_nicht(POS-lex/8, LVollform, AVollform).

	% lex_/8

lex_(POS, ['#'|LVollform], Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform) :-
	nonvar(LVollform), LVollform \= [],
	vollform(POS, LVollform, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform).

lex_(POS, ['#'|LVollform], Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform) :-
	nonvar(LVollform), LVollform \= [],
	\+vollform(POS, LVollform, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform),
	vollform(POS, LVollform_, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform),
	praefix_von(LVollform, LVollform_).

lex_(POS, LVollfrom, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform) :-
	vollform(POS, LVollfrom, Formmerk, Typ, Artmerk, Semantik, Grundform, AVollform).


	% Allgemeine Hilfspraedikate

	% praefix_von(+Praefix, +Wort)
	% Praefix ist ein Praefix von Wort. Arbeitet mit Zeichenlisten.

praefix_von([], _) :- !.
praefix_von([H|T], [H|T2]) :- praefix_von(T, T2).

	% existiert_nicht(+Praedikat, ?Zeichenliste, ?Atom)
	% Ueberprueft, ob es unabhaengig von der Wortart fuer die Zeichenliste und/oder das Atom einen Lexikoneintrag gibt. Falls nicht, wird eine Exception geworfen. Wird nur ausgefuhert, wenn das Prolog-Flag "grammatik_test" den Wert "on" hat und sich die Grammatik nicht im Suggest-Modus befindet.
	% Praedikat = Angabe eines aufrufenden Praedikats fuer die Exception

existiert_nicht(Pred, Liste, Atom) :-
	current_prolog_flag(grammatik_test, on),
	suggest_modus(aus),
	\+((
		(
			(nonvar(Liste), ziffern(Zs), Liste = [Z], member(Z, Zs))
			; lex_(v>_, [Liste, _], _, _, _, _, [Atom, _])
			; lex_(v>_, [_, Liste], [v1/v2, _, _, praes], _, _, _, [_, Atom]) % Partikel
			; lex_(n>_, Liste, _, _, _, _, _, _, Atom)
			; lex_(pro>_, Liste, _, _, _, _, _, _, Atom)
			; lex_(_, Liste, _, _, _, _, _, Atom)
		), !
	)),
	atom_chars(Atom, Liste),
	throw(error(domain_error('Token', Atom), Pred)).

	% nicht_lexikalisch(+Liste)
	% Ueberprueft, ob die Zeichenreihe Liste KEIN dem Lexikon bekanntes Token darstellt.

nicht_lexikalisch(Liste) :- var(Liste), !.

nicht_lexikalisch(Liste) :-
	\+((
		(
			lex_(v>_, [Liste, _], _, _, _, _, [Atom, _])
			; lex_(v>_, [_, Liste], [v1/v2, _, _, praes], _, _, _, [_, Atom]) % Partikel
			; lex_(n>_, Liste, _, _, _, _, _, _, Atom)
			; lex_(pro>_, Liste, _, _, _, _, _, _, Atom)
			; lex_(_, Liste, _, _, _, _, _, Atom)
		), !
	)).
