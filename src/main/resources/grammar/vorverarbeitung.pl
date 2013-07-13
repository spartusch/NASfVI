
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


	% zeichen(?T, ?Z)
	% Ueberprueft, ob Z vom Typ T ist.
	% T = buchst (Buchstabe), ziffer oder whitespace

zeichen(Cat, C) :- zeichen_(Cat, C), !.

	% Falls bekanntes Zeichen, aber u. U. falsche Kategorie, nur fail, keine Exception ausloesen:
zeichen(_, C) :- (zeichen_(buchst, C) ; zeichen_(ziffer, C)
	; zeichen_(whitespace, C) ; zeichen_(separator, C) ; C = '"' ; C = '.' ; C = '-'), !, fail.
zeichen(_, C) :- throw(error(domain_error('gueltiges Zeichen', C), zeichen/2)).

zeichen_(buchst, C) :- alphabet(A), member(C, A).
zeichen_(ziffer, C) :- ziffern(Z), member(C, Z).
zeichen_(whitespace, C) :- whitespace(W), member(C, W).
zeichen_(separator, '/').


	% tokenisiere(+Eingabe, +Ergaenzen, -Tokenliste)
	% Tokenisiert die Eingabe und aktiviert/deaktiviert Praefix-Ergaenzung.
	% Eingabe = Satz, der verarbeitet werden soll, als Atom
	% Ergaenzen = 0 oder 1. Boolescher Wert fuer Aktivierung der Praefix-Ergaenzung
	% Tokenliste = Tokenisierte Eingabe. Jedes Token ist eine Liste seiner Buchstaben

	% Bei aktivierter Praefix-Ergaenzung wird vor jedes Token ein '#' und am Ende des Satzes eine freie Variabel angefuegt. Das Lexikon erlaubt bei vorhandenem '#' eine Praefix-Ergaenzung.

tokenisiere(S, E, T) :- atom_chars(S, C), tokenisiere(E, T, C, []), !.

tokenisiere(0, [T]) --> token(T).

tokenisiere(1, [['#'|T]|_]) --> wort(T).	% nur letztes Wort lexikalisch ergaenzungsfaehig
tokenisiere(1, [T|_]) --> token(T).
tokenisiere(1, [T|_]) --> separator(T).

tokenisiere(E, [T|TL]) --> token(T), leer, {!}, tokenisiere(E, TL).
tokenisiere(E, [T, S|TL]) --> token(T), separator(S), {!}, tokenisiere(E, TL).
tokenisiere(E, [T|TL]) --> token(T), tokenisiere(E, TL).

token(T) --> alphanum(T) ; blackbox_token(T).

wort([C, '-'|T]) --> [C], ['-'], {zeichen(buchst, C)}, wort(T).
wort([C]) --> [C], {zeichen(buchst, C)}.
wort([C|T]) --> [C], {zeichen(buchst, C)}, wort(T).

alphanum(['.', C]) --> ['.'], [C], {zeichen(ziffer, C)}.
alphanum(['.', C|T]) --> ['.'], [C], {zeichen(ziffer, C)}, alphanum(T).
alphanum([C]) --> [C], {zeichen(buchst, C) ; zeichen(ziffer, C)}.
alphanum([C, '-'|T]) --> [C], ['-'], {zeichen(buchst, C) ; zeichen(ziffer, C)}, alphanum(T).
alphanum([C|T]) --> [C], {zeichen(buchst, C) ; zeichen(ziffer, C)}, alphanum(T).

separator([C]) --> [C], {zeichen(separator, C)}.

leer --> [C], {zeichen(whitespace, C)}.
leer --> [C], {zeichen(whitespace, C)}, leer.

	% blackbox_token/3, beliebig/3
	% Erlaubt die Einbettung eines beliebigen Substrings zwischen " und ". Das abschliessende " kann auch entfallen - in diesem Fall wird der gesamte Reststring als Blackbox-Token geparset.

beliebig([]) --> ['"'].
beliebig([C|T]) --> [C], {C \= '"'}, beliebig(T). % Falls kein abschliessendes " so viel wie moeglich, d. h. Reststring ...
beliebig([C]) --> [C], {C \= '"'}.
blackbox_token(BBT) --> ['"'], beliebig(BBT), {!}.
