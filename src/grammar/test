
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


%:- set_prolog_flag(grammatik_test, on).	% Aktiviert die Exceptions von existiert_nicht/3.

test_antwort(Eingabe, Werte, Antwort) :-
	beantworte(Eingabe, Werte, _, _, Antwort).

test_parse(Eingabe) :-
	parse(Eingabe, _), !.

test_suggest(Eingabe) :-
	suggest(Eingabe, Markiertheit, Vorschlaege),
	write(Markiertheit), write(':'), nl, test_write(Vorschlaege).
	

	% test_write(+Liste)
	% Schreibt jedes Element auf eine Zeile.

test_write([]).
test_write([H|T]) :- write('\t'), write(H), nl, test_write(T).


	% test
	% Generiert alle moeglichen Saetze und gibt sie aus.

test :-
	deaktiviere_suggest,
	Sterm = s(_, _, A_S),
	findall(A_S, phrase(Sterm, _, []), S),
	test_write(S).


	% Prolog Unit Tests
	% run_tests/0 und run_tests/1
	% http://www.swi-prolog.org/packages/plunit.html

	% Qualitaet der Test-Suite ermitteln: show_coverage(run_tests).

:- begin_tests(syntax).
test(1) :- test_parse('eine vorlesung findet statt').
test(2) :- test_parse('eine vorlesung findet ueber semantik statt').
test(3) :- test_parse('ueber syntax findet eine vorlesung statt').
test(4) :- test_parse('findet eine vorlesung statt').
test(5) :- test_parse('findet eine vorlesung ueber semantik statt').
test(6) :- test_parse('eine vorlesung fand statt').
test(7) :- test_parse('eine vorlesung fand ueber syntax statt').
test(8) :- test_parse('ueber syntax fand eine vorlesung statt').
test(9) :- test_parse('fand eine vorlesung statt').
test(10) :- test_parse('fand eine vorlesung ueber syntax statt').
test(11) :- test_parse('hat eine vorlesung ueber syntax stattgefunden').
test(12) :- test_parse('hatte ueber semantik eine vorlesung stattgefunden').
test(13) :- test_parse('ueber semantik hatte eine vorlesung stattgefunden').
test(14) :- test_parse('der dozent mueller haelt eine vorlesung ueber semantik').
test(15) :- test_parse('ueber semantik haelt ein dozent eine vorlesung').
test(16) :- test_parse('im sommersemester 2007 hat herr mueller eine vorlesung ueber syntax gehalten').
test(17) :- test_parse('hielt ein dozent ueber semantik eine vorlesung in dem sommersemester 2007').
test(18) :- test_parse('in dem sommersemester 2007 hielt eine vorlesung der professor mueller').
test(19) :- test_parse('es gab einen dozenten').
test(20) :- test_parse('den dozenten mueller hat es gegeben').
test(21) :- test_parse('gab es die vorlesung "grammatikimplementierung"').
test(22) :- test_parse('gab es eine vorlesung ueber prolog').
test(23) :- test_parse('es gibt eine vorlesung ueber syntax').
test(24) :- test_parse('hat es eine vorlesung ueber semantik gegeben').
test(25) :- test_parse('handelte eine vorlesung von syntax').
test(26) :- test_parse('eine vorlesung handelt von semantik').
test(27) :- test_parse('von semantik hat eine vorlesung gehandelt').
test(28) :- test_parse('herr mueller haelt eine vorlesung ueber "syntax und semantik"').
test(29) :- test_parse('wer haelt was').
test(30) :- test_parse('syntax handelt von syntax').
test(31) :- test_parse('wo findet syntax statt').
test(32) :- test_parse('syntax haelt herr mueller wo').
test(33) :- test_parse('welche vorlesung aehnelt welchem proseminar').
test(34) :- test_parse('dem hauptseminar syntax aehnelt ein seminar ueber grammatik').
	% Koordination
test(35) :- test_parse('herr mueller und professor mustermann halten eine vorlesung').
test(36) :- test_parse('haelt herr mueller oder professor mustermann syntax').
test(37) :- test_parse('wird von herrn mueller oder frau mueller das xml-seminar gehalten').
test(38) :- test_parse('welcher kurs aehnelt dem seminar syntax und dem kurs grammatik').
	% Passiv
test(40) :- test_parse('wird eine vorlesung von einem dozenten gehalten').
test(41) :- test_parse('wurde eine vorlesung von einem dozenten gehalten').
test(42) :- test_parse('eine vorlesung wurde gehalten').
test(43) :- test_parse('eine vorlesung wird gehalten').
test(44) :- test_parse('eine vorlesung wird gegeben').
test(45) :- test_parse('eine vorlesung wird von dem dozenten mueller gegeben').
test(46) :- test_parse('wurde von dem dozenten mueller eine vorlesung gegeben').
test(47) :- test_parse('ist eine vorlesung gehalten worden').
test(48) :- test_parse('war eine vorlesung von dem dozenten mueller gehalten worden').
test(49) :- test_parse('eine vorlesung war von einem dozenten gegeben worden').
test(50) :- test_parse('eine vorlesung wird ueber "den ursprung der arten" von professor darwin gehalten').
test(51) :- test_parse('wird eine vorlesung von den dozentinnen mueller und musterfrau gehalten').
	% Futur 1
test(60) :- test_parse('eine vorlesung wird von einem dozenten gehalten werden').
test(61) :- test_parse('eine vorlesung wird von dem dozenten mueller ueber syntax gehalten werden').
test(62) :- test_parse('eine vorlesung wird stattfinden').
test(63) :- test_parse('wird eine vorlesung stattfinden').
test(64) :- test_parse('wird eine vorlesung gehalten werden').
test(65) :- test_parse('der dozent mueller wird eine vorlesung geben').
test(66) :- test_parse('eine vorlesung wird ueber "den ursprung der arten" von professor darwin gehalten werden').
test(67) :- test_parse('in welchem raum wird eine vorlesung ueber syntax stattfinden').
test(68) :- test_parse('von syntax wird eine vorlesung handeln').
test(69) :- test_parse('von wem wird eine vorlesung gehalten werden').
test(70) :- test_parse('von herrn mueller wird eine vorlesung gehalten werden').
test(71) :- test_parse('von dem herren mueller wird eine vorlesung gehalten werden').
test(72) :- test_parse('wer wird eine vorlesung im raum 1.14 halten').
:- end_tests(syntax).

:- begin_tests(suggest).
test(1) :- suggest('ueber semantik f', 0, _, _).
test(3) :- suggest('hat eine vorlesung s', 0, _, _).
test(4) :- suggest('fand die vorle', 0, _, _).
test(5) :- suggest('gibt es', 0, _, _).
test(6) :- suggest('ueber syntax haelt dozent mueller eine vorlesung', 0, _, _).
test(7) :- \+suggest('eine vorlesung findet eine vorl', 0, _, _).
test(8) :- suggest('syntax handelt von syntax', 0, _, _).
test(9) :- \+suggest('hat i', 0, _, _).
test(10) :- suggest('haelt herr mueller od', 0, _, _).
test(11) :- suggest('haelt herr x eine vorlesung in dem ra', 0, _, _).
test(12) :- suggest('wann wurde montags syntax gehalten', 0, _, _).
test(13) :- suggest('welche vorlesung ae', 0, _, _).
:- end_tests(suggest).

:- begin_tests(semantik).
	% PL
test(1) :- parse('wer haelt eine vorlesung', [_,_,_,S]), !,
	S = [qu(X, dozent(X, '') und ex(Y, veranstaltung(Y, '') und typ(Y, vorlesung) und halten(X, Y, '_', '_'))), _].
test(2) :- parse('ein professor haelt ein seminar ueber syntax in dem wintersemester 2008/09', [_,_,_,S]), !,
	S = [ex(X, dozent(X, '') und dozent_titel(X, prof) und ex(Y, veranstaltung(Y, '') und typ(Y, seminar) und thema(Y, '"syntax"') und halten(X, Y, '_', '_'))), ex(Z, semester(Z, '2008/2009'))].
test(3) :- parse('frau mueller gibt das seminar grammatik ueber syntax in dem wintersemester 2008/09 montags im raum 1.14', [_,_,_,S]), !,
	S = [ex(X, dozent(X, '"mueller"') und ex(T, tag(T, mo) und ex(R, raum(R, '"1.14"') und ex(Y, veranstaltung(Y, '"grammatik"') und typ(Y, seminar) und thema(Y, '"syntax"') und halten(X, Y, R, T))))), ex(Z, semester(Z, '2008/2009'))].

	% Lucene
test(21) :- parse('was haelt wer ueber syntax', _, '(titel:"syntax" OR beschreibung:"syntax" ) ', '', [dozent, titel]).
test(22) :- parse('frau mueller gibt welches seminar ueber syntax im wintersemester 2008/09', _, Q, '', [titel]),
	Q = 'dozent:"mueller" typ:"seminar" (titel:"syntax" OR beschreibung:"syntax" ) semester:"2008/2009" '.
test(24) :- parse('haelt professor mueller eine vorlesung ueber syntax', _, Q, '', []),
	Q = 'dozent:("prof" +"mueller") typ:"vorlesung" (titel:"syntax" OR beschreibung:"syntax" ) '.
test(23) :- parse('welche vorlesung aehnelt dem proseminar syntax im sommersemester 2010', _, Q, SQ, [titel]),
	Q = 'typ:"vorlesung" semester:"2010" ',
	SQ = 'titel:"syntax" typ:"proseminar" semester:"2010" '.
test(25) :- parse('aehnelt eine vorlesung einem proseminar ueber syntax', _, Q, SQ, []),
	Q = 'typ:"vorlesung" ',
	SQ = 'typ:"proseminar" (titel:"syntax" OR beschreibung:"syntax" ) '.
test(26) :- parse('aehnelt eine vorlesung ueber syntax einem proseminar', _, Q, SQ, []),
	Q = 'typ:"vorlesung" (titel:"syntax" OR beschreibung:"syntax" ) ',
	SQ = 'typ:"proseminar" '.
test(27) :- parse('welche vorlesung aehnelt einem seminar ueber syntax', _, Q, SQ, [titel]),
	Q = 'typ:"vorlesung" ',
	SQ = 'typ:"seminar" (titel:"syntax" OR beschreibung:"syntax" ) '.
:- end_tests(semantik).

:- begin_tests(antworten).
test(1) :- test_antwort('wer haelt eine vorlesung ueber syntax im sommersemester 2007', [[dozent,'"x"']], A),
	A = [eine, 'Vorlesung', ueber, '"syntax"', haelt, '"x"', im, 'Sommersemester', '2007'].
test(2) :- test_antwort('wer haelt die vorlesung x', [[dozent,'"mueller"']], A),
	A = [die, 'Vorlesung', '"x"', haelt, '"mueller"'].
test(3) :- test_antwort('im wintersemester 2007/08 findet was statt', [[titel,'"prolog"']], A),
	A = [im, 'Wintersemester', '2007/2008', findet, '"prolog"', statt].
test(4) :- test_antwort('was haelt wer ueber syntax', [[dozent,'"mueller"'],[titel,'"prolog"']],  A),
	A = [ueber, '"syntax"', haelt, '"mueller"', '"prolog"'].
test(5) :- test_antwort('wer haelt syntax', [[dozent,'"x"','"y"','"z"']], A),
	A = ['"syntax"', halten, die, 'Dozenten', '"x"', und, '"y"', und, '"z"'].
test(6) :- test_antwort('in welchem raum findet syntax statt', [[raum,'"1.14"','"0.3"']], A),
	A = ['"syntax"', findet, im, Raum, '"1.14"', und, im, Raum, '"0.3"', statt].
test(7) :- test_antwort('in welchem raum findet syntax statt', [[raum,'"114"']], A),
	A = ['"syntax"', findet, im, 'Raum', '"114"', statt].
test(8) :- test_antwort('wurde etwas ueber voicexml gehalten', [], A),
	A = [jemand, hielt, etwas, ueber, '"voicexml"'].
test(9) :- test_antwort('wo findet syntax statt', [[raum,'"114"','"Audimax"']], A),
	A = ['"syntax"', findet, im, 'Raum', '"114"', und, im, 'Raum', '"Audimax"', statt].
test(10) :- test_antwort('was ist von herrn "a" oder herrn "b" gehalten worden', [[titel,'"xyz"']], A),
	A = ['Herr', '"a"', oder, 'Herr', '"b"', hat, '"xyz"', gehalten].
test(11) :- test_antwort('was ist von herrn "a" und herrn "b" gehalten worden', [[titel,'"xyz"']], A),
	A = ['Herr', '"a"', und, 'Herr', '"b"', haben, '"xyz"', gehalten].
test(12) :- test_antwort('welche seminare haelt herr "x" wann', [[tag, do], [titel,'"y"']], A),
	A = ['Herr', '"x"', haelt, am, 'Donnerstag', '"y"'].
test(13) :-  test_antwort('welches seminar aehnelt der vorlesung syntax', [[titel, '"grammatikimplementierung"']], A),
	A = [der, 'Vorlesung', '"syntax"', aehnelt, '"grammatikimplementierung"'].
test(14) :- test_antwort('aehnelt eine vorlesung ueber syntax einem proseminar', [], A),
	A = [eine, 'Vorlesung', ueber, '"syntax"', aehnelt, einem, 'Proseminar'].
:- end_tests(antworten).

:- begin_tests(fehler).
test(1, [throws(error(domain_error('gueltiges Zeichen', _), zeichen/2))]) :- parse('eine vorlesung $ fand statt', _).
%test(2, [throws(error(domain_error('Token', _), _))]) :- parse('eine vorlesung fand xyz statt', _).
:- end_tests(fehler).
