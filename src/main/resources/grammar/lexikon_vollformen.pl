
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


	% Verben
	% vollform/7

	% Hilfsverb, infinite Formen
vollform(v>aux, [LVerb, []], Form, [], '', Infinitiv, [AVerb, '']) :-
	indefinite(Form),
	hilfsverb(Infinitiv, Form, AVerb), atom_chars(AVerb, LVerb).

	% Hilfsverb, finite Formen
vollform(v>aux, [LVerb, []], [_Vst|[Pers, Num, Temp]], [], '', Infinitiv, [AVerb, '']) :-
	hilfsverb(Infinitiv, [Pers, Num, Temp], AVerb), atom_chars(AVerb, LVerb).

	% Vollverb, infinite Formen
vollform(v>v, [LVerb, []], Form, Val, Sem, Infinitiv, [AVerb, '']) :-
	indefinite(Form),
	flexion(v>v, Infinitiv, '', Val, Sem, Form, AVerb),
	atom_chars(AVerb, LVerb).

	% Vollverb, finite Formen
vollform(v>v, [LVerb, LPartikel], [Vst, Pers, Num, Temp], Val, Sem, Infinitiv, [AVerb, APartikel]) :-
	verbstellung(_, Vst),
	flexion(v>v, Infinitiv, APartikel, Val, Sem, [Vst, Pers, Num, Temp], AVerb),
	atom_chars(AVerb, LVerb), atom_chars(APartikel, LPartikel).


	% Nomen und Pronomen
	% vollform/9

vollform(n>N, LForm, Formmerkmale, Typ, Genus, Val, Sem, Grundform, AForm) :-
	flexion(n>N, Grundform, Typ, Genus, Val_, Sem, Formmerkmale, AForm),
	atom_chars(AForm, LVF),
	nur_kleinbuchstaben(LVF, LForm),
	expandiere_valenz(aktiv, Val_, Val, _).

vollform(pro>Sub, LForm, Formmerkmale, Typ, '-', Val, Sem, Grundform, AForm) :-
	form(pro>Sub, Grundform, Typ, Formmerkmale, AForm, Val_, Sem),
	atom_chars(AForm, LForm),
	expandiere_valenz(aktiv, Val_, Val, _).


	% Sonstige Wortarten (Artikel, Praepositionen, Junktoren, Adverbien)
	% vollform/8

vollform(adv>Sub, LForm, [], Typ, Def, Sem, AForm, AForm) :-
	form(adv>Sub, AForm, Typ, Sem),
	(Sub = qu -> Def = qu ; Def = ''),
	atom_chars(AForm, LForm).

vollform(p>Sub, LForm, [], Typ, Art, Sem, AForm, AForm) :-
	form(p>Sub, AForm, Typ, Art, Sem),
	atom_chars(AForm, LForm).

vollform(junkt>Sub, LForm, [], Sub, '', Sem, AForm, AForm) :-
	form(junkt>Sub, AForm, Sem), atom_chars(AForm, LForm).

vollform(art>Def, LForm, Formmerkmale, Def, Genus, Sem, Grundform, AForm) :-
	form(art>Def, Grundform, Genus, Formmerkmale, AForm, Sem),
	atom_chars(AForm, LForm).


	% Sonstige Praedikate

	% zeige_formen(?Wortart, ?Grundform)
	% Zeigt alle Wortformen der Grundform und/oder Wortart.
	% Wortart = n>n, v>v oder v>aux
	% Grundform = Grundform der Wortformen

zeige_formen(v>V, Infinitiv) :-
	vollform(v>V, _, Formmerk, _, _, Infinitiv, [AVerb, APartikel]),
	write(Infinitiv), write(' '), write(Formmerk), write(': '), write(AVerb),
	(APartikel \= '' -> write(' '), write(APartikel); true),
	nl, fail.

zeige_formen(n>N, Grundform) :-
	vollform(n>N, _, Formmerk, _, _, _, _, Grundform, Vollform),
	write(Grundform), write(' '), write(Formmerk), write(': '), write(Vollform),
	nl, fail.

zeige_formen(POS, Grundform) :-
	vollform(POS, _, Formmerk, _, _, _, Grundform, Vollform),
	write(Grundform), write(' '), write(Formmerk), write(': '), write(Vollform),
	nl, fail.

zeige_formen(_, _).


	% schreibe_vollformen(+Wortart)
	% Schreibt alle Vollformen der Wortart nach STDOUT.
	% Parameter "numbervars(true)" bei write_term/2 ist nicht ISO-Standard, sondern SWI-Prolog!

schreibe_vollformen(Art) :-
	(VF = vollform(Art, _, F, _, _, _, _) % vollform/7: Verb
		; VF = vollform(Art, _, F, _, _, _, _, _, _) % vollform/9: Nomen/Pronomen
		; VF = vollform(Art, _, F, _, _, _, _, _) % vollform/8: Rest
	),
	call(VF),
		%maskiere_variabeln(VF),
		numbervars(VF, 0, _, [singletons(true)]), % SWI-Prolog
	((F = [_, Cas], Cas \= gen)
		; (F = [_, 3, _, _], F \= [vl, _, _, _])
		; (F \= [_, _], F \= [_, _, _, _])
	),
	write_term(VF, [ignore_ops(false), quoted(true), numbervars(true)]),
	writeln('.'), fail.

schreibe_vollformen(_).

	% schreibe_vollformenlexikon(+Datei)
	% Schreibt alle Vollformen nach Datei.

schreibe_vollformenlexikon(Datei) :-
	write('Schreibe '), write(Datei), writeln(' ...'),
	open(Datei, write, DStream, []),
	set_output(DStream),
	writeln(':- op(100, fx, \'?\').'),
	writeln(':- op(100, xfx, \'>\').'),
	writeln(':- op(600, yfx, und).'),
	writeln(':- op(650, yfx, oder).'),
	schreibe_vollformen(_),
	close(DStream),
	set_output(user_output).

	% schreibe_vollformenlexikon
	% Schreibt alle Vollformen in die Datei "vollformen_lexikon".

schreibe_vollformenlexikon :- schreibe_vollformenlexikon('vollformen.liste').


	% maskiere_variabeln(+Term)
	% Maskiert freie Variabeln in Term.

maskiere_variabeln(T) :- T =.. S, maskiere_variabeln(S, -1, _).

	% maskiere_variabeln(+Liste, +Wert, ?Endwert)
	% Belegt freie Variabeln in Liste mit '$VAR'(C), damit write_term/2 sie formatiert ausgibt.
	% Wert = Wert+1 ist der Startwert fuer C. '$VAR'(0) ergibt A, '$VAR'(1) ergibt B, ...
	% Endwert = Wert von C bei der letzten Variabel in Liste

maskiere_variabeln([], C, C).
maskiere_variabeln([H|T], C, Cm) :-
	var(H), Cn is C + 1, H = '$VAR'(Cn), maskiere_variabeln(T, Cn, Cm).
maskiere_variabeln([H|T], C, Cm) :- atomic(H), maskiere_variabeln(T, C, Cm), !.
maskiere_variabeln([H|T], C, Cm) :-
	compound(H), \+(H = [_|_]), H =.. Hn, maskiere_variabeln(Hn, C, C1), maskiere_variabeln(T, C1, Cm).
maskiere_variabeln([H|T], C, Cm) :-	maskiere_variabeln(H, C, C1), maskiere_variabeln(T, C1, Cm).
