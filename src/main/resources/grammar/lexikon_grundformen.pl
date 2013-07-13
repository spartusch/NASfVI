
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


:- discontiguous(form_/5).

	% Verben

	% grundform(?Verbart, ?Infinitiv, ?Partikel, ?Flexionsklasse, ?Valenz, ?Semantik)
	% Verbart = Vollverb (v>v) oder Hilfsverb (v>aux)
	% Infinitiv = Grundform des Verbs
	% Partikel = Partikel des Verbs oder leer
	% Flexionsklasse = rg oder urg(Ablautreihe:Position des Stammvokals, Flexionseigenschaft)
	% Valenz = syntaktisch-semantische Valenz des Verbs in Form von semantischen Rollen und obligatorischen und fakultativen Phrasen
	% Semantik = Semantik des Verbs

	% Flexionseigenschaften:
	% - = keine Besonderheiten
	% uml = Umlautung des Stammvokals im Praesens, Singular, 2. und 3. Person
	% eiw = Wechsel von e zu i als Stammvokal im Praesens, Singular, 2. und 3. Person

grundform(v>aux, haben, '', rg, [], '').
grundform(v>aux, werden, '', urg([e,u,o]:2, eiw), [], '').
grundform(v>aux, sein, '', -, [], '').

grundform(v>v, finden, statt, urg([i,a,u]:2, -),
	% ohne Agens kein Passiv
	[patiens(event, SemPa), ?loc(SemLoc), ?temp_d(SemDies)],
	SemPa * lam(Y, SemDies * lam(D, SemLoc * lam(L, halten('_', Y, L, D))))
).

grundform(v>v, halten, '', urg([a,ie,a]:2, uml),
	[agens(hum, SemAg), patiens(event, SemPa), ?loc(SemLoc), ?temp_d(SemDies)],
	SemAg * lam(X, SemDies * lam(D, SemLoc * lam(L, SemPa * lam(Y, halten(X, Y, L, D)))))
).

grundform(v>v, geben, '', urg([e,a,e]:2, eiw),
	[agens(hum, SemAg), patiens(event, SemPa), ?loc(SemLoc), ?temp_d(SemDies)],
	SemAg * lam(X, SemDies * lam(D, SemLoc * lam(L, SemPa * lam(Y, halten(X, Y, L, D)))))
).

grundform(v>v, aehneln, '', rg,
	[nom(event, SemNom), dat(event, SemDat), ?temp_d(SemDies)],
	SemNom * lam(X, SemDies * lam(D, SemDat * lam(Y, aehneln(X, Y, D))))
).

grundform(v>v, handeln, '', rg,
	[patiens(event, SemPa), thema(dat, SemThema)],
	SemPa * SemThema
).

grundform(v>v, geben, '', urg([e,a,e]:2, eiw),
	[expletiv, patiens(_, Sem)],
	Sem * lam(X, X)
).


	% Nomen

	% grundform(?Nomenart, ?Grundform, ?Typ, ?Genus, ?Flexionsklasse, ?Valenz, ?Semantik)
	% Nomenart = n>n oder n>app (Apposition)
	% Grundform = Grundform des Nomens
	% Typ = (semantischer) Typ des Nomens
	% Genus = Genus des Nomens
	% Flexionsklasse = [Flexionsklasse im Singular, Flexionsklasse im Plural]
	% Valenz = Valenz des Nomens in Form von obligatorischen und fakultativen Phrasen
	% Semantik = Semantik des Nomens

grundform(n>n, 'Sommersemester', semester>sose, neut, [s, -], [], lam(Y, lam(X, semester(X,Y)))).
grundform(n>n, 'Wintersemester', semester>wise, neut, [s, -], [], lam(Y, lam(X, semester(X,Y)))).
grundform(n>n, 'Semester', semester>sem, neut, [s, -], [], lam(Y, lam(X, semester(X,Y)))).

grundform(n>n, 'Montag', temp_d, mask, [es, e], [], lam(X, tag(X,'mo'))).
grundform(n>n, 'Dienstag', temp_d, mask, [es, e], [], lam(X, tag(X,'di'))).
grundform(n>n, 'Mittwoch', temp_d, mask, [es, e], [], lam(X, tag(X,'mi'))).
grundform(n>n, 'Donnerstag', temp_d, mask, [es, e], [], lam(X, tag(X,'do'))).
grundform(n>n, 'Freitag', temp_d, mask, [es, e], [], lam(X, tag(X,'fr'))).
grundform(n>n, 'Samstag', temp_d, mask, [es, e], [], lam(X, tag(X,'sa'))).
grundform(n>n, 'Sonntag', temp_d, mask, [es, e], [], lam(X, tag(X,'so'))).

grundform(n>app, 'Veranstaltung', event, fem, [-, en],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und SemThema*X))
).

grundform(n>app, 'Kurs', event, mask, [es, e],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und SemThema*X))
).

grundform(n>app, 'Vorlesung', event, fem, [-, en],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und typ(X, vorlesung) und SemThema*X))
).

grundform(n>app, 'Seminar', event, neut, [s, e],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und typ(X, seminar) und SemThema*X))
).

grundform(n>app, 'Proseminar', event, neut, [s, e],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und typ(X, proseminar) und SemThema*X))
).

grundform(n>app, 'Hauptseminar', event, neut, [s, e],
	[?thema(akk, SemThema)],
	lam(Y, lam(X, veranstaltung(X, Y) und typ(X, hauptseminar) und SemThema*X))
).

grundform(n>app, 'Raum', loc, mask, [es, e, umlaut:2], [], lam(Y, lam(X,raum(X,Y)))).
grundform(n>app, 'Dozent',   hum, mask, [en, en], [], lam(Y, lam(X, dozent(X, Y)))).
grundform(n>app, 'Dozentin', hum, fem,  [-, en],  [], lam(Y, lam(X, dozent(X, Y)))).
grundform(n>app, 'Herr', hum, mask, [en, en, unb], [], lam(Y, lam(X, dozent(X, Y)))).
grundform(n>app, 'Frau', hum, fem,  [-, en], [], lam(Y,lam(X, dozent(X, Y)))).
grundform(n>app, 'Professor',   hum, mask, [s, en], [], lam(Y, lam(X, dozent(X, Y) und dozent_titel(X, prof)))).
grundform(n>app, 'Professorin', hum, fem,  [-, en], [], lam(Y, lam(X, dozent(X, Y) und dozent_titel(X, prof)))).


	% Pronomen

	% form(?Wortart, ?Grundform, ?Typ, ?Formmerkmale, ?Atom, ?Valenz, ?Semantik)
	% Die Formen werden in NASfVI nicht aktiv flektiert, haben jedoch Formmerkmale.
	% Formmerkmale = [Numerus, Casus]

	% Expletiv

form(pro>expl, 'es', expl, [sg, nom], es, [], '').

	% Interrogativpronomen

form(pro>qu, Grundform, hum, Form, Atom, [], Sem) :-
	Sem = lam(P, qu(X, dozent(X, '') und P*X)),
	form_(pro>qu, Grundform, hum, Form, Atom).

	% wer haelt was, wer haelt was ueber semantik
	% haelt jemand etwas ueber semantik
form(pro>qu, Grundform, event, Form, Atom, Val, Sem) :-
	Val = [?thema(akk, SemThema)],
	Sem = lam(P, qu(X, veranstaltung(X, '') und SemThema*X und P*X)),
	form_(pro>qu, Grundform, event, Form, Atom).

/*	% von was handelt die vorlesung "syntax"
form(pro>qu, 'was', thema, Form, Atom, [], Sem) :-
	Sem = qu(X, thema(X, '')),
	form_(pro>qu, 'was', thema, Form, Atom).
*/

form_(pro>qu, 'wer', hum, [sg, nom], wer).
form_(pro>qu, 'wer', hum, [sg, gen], wessen).
form_(pro>qu, 'wer', hum, [sg, dat], wem).
form_(pro>qu, 'wer', hum, [sg, akk], wen).
form_(pro>qu, 'jemand', hum, [sg, nom], jemand).
form_(pro>qu, 'jemand', hum, [sg, gen], jemands).
form_(pro>qu, 'jemand', hum, [sg, gen], jemandes).
form_(pro>qu, 'jemand', hum, [sg, dat], jemand).
form_(pro>qu, 'jemand', hum, [sg, dat], jemandem).
form_(pro>qu, 'jemand', hum, [sg, akk], jemand).
form_(pro>qu, 'jemand', hum, [sg, akk], jemanden).
/*
form_(pro>qu, 'was', Typ, [sg, nom], was) :- Typ = thema ; Typ = event.
form_(pro>qu, 'was', Typ, [sg, gen], wessen) :- Typ = thema ; Typ = event.
form_(pro>qu, 'was', Typ, [sg, dat], was) :- Typ = thema ; Typ = event.
form_(pro>qu, 'was', Typ, [sg, akk], was) :- Typ = thema ; Typ = event.
*/
form_(pro>qu, 'was', event, [sg, nom], was).
form_(pro>qu, 'was', event, [sg, gen], wessen).
form_(pro>qu, 'was', event, [sg, dat], was).
form_(pro>qu, 'was', event, [sg, akk], was).
form_(pro>qu, 'etwas', event, [sg, nom], etwas).
form_(pro>qu, 'etwas', event, [sg, gen], etwas).
form_(pro>qu, 'etwas', event, [sg, dat], etwas).
form_(pro>qu, 'etwas', event, [sg, akk], etwas).


	% Adverbien

form(adv>qu, 'wo', loc, lam(P, qu(X, ort(X) und P*X))).
	% nicht mehrfach wegen Laenge constraint
form(adv>qu, 'wann', Typ, lam(P, qu(X, zeit(X) und P*X))) :- Typ = temp_d ; Typ = semester.

form(adv>adv, 'montags', temp_d, lam(P, ex(X, tag(X, 'mo') und P*X))).
form(adv>adv, 'dienstags', temp_d, lam(P, ex(X, tag(X, 'di') und P*X))).
form(adv>adv, 'mittwochs', temp_d, lam(P, ex(X, tag(X, 'mi') und P*X))).
form(adv>adv, 'donnerstags', temp_d, lam(P, ex(X, tag(X, 'do') und P*X))).
form(adv>adv, 'freitags', temp_d, lam(P, ex(X, tag(X, 'fr') und P*X))).
form(adv>adv, 'samstags', temp_d, lam(P, ex(X, tag(X, 'sa') und P*X))).
form(adv>adv, 'sonntags', temp_d, lam(P, ex(X, tag(X, 'so') und P*X))).


	% Praepositionen

	% form(p>Subart, ?Grundform, ?Typ, ?Artmerkmal, ?Semantik)

form(p>bbox, 'von', thema, dat, lam(T, lam(X, thema(X, T)))).
form(p>bbox, 'ueber', thema, akk, lam(T, lam(X, thema(X, T)))).

form(p>p, 'von', hum, dat, lam(Q, lam(P, Q*P))).
form(p>p, 'in', Typ, dat, lam(Q, lam(P, Q*P))) :- Typ = semester ; Typ = loc.

form(p>def, 'im', Typ, dat, lam(Q, lam(P, ex(X, Q*X und P*X)))) :- Typ = semester ; Typ = loc.
form(p>def, 'am', temp_d, dat, lam(Q, lam(P, ex(X, Q*X und P*X)))).


	% Junktoren

	% form(junkt>Sub, ?Grundform, ?Semantik)
	% Sub = con (konjunktiv) oder dis (disjunktiv)

form(junkt>con, 'und',  lam(A, lam(B, lam(P, A*P und B*P)))).
form(junkt>dis, 'oder', lam(X, lam(A, lam(B, lam(P, ex(X, (A oder B) und P*X)))))).


	% Artikel

	% form(?Wortart, ?Grundform, ?Genus, ?Formmerkmale, ?Atom, ?Semantik)
	% Die Formen sind nicht flektiert im eigentlichen Sinn, haben jedoch Formmerkmale.
	% Formmerkmale = [Numerus, Casus]

form(art>Def, Grundform, Genus, Form, Atom, Sem) :-
	Sem = lam(Q, lam(P, ex(X, Q*X und P*X))),
	(Def = def ; Def = indef),
	form_(art>Def, Grundform, Genus, Form, Atom).

form(art>qu, Grundform, Genus, Form, Atom, Sem) :-
	Sem = lam(Q, lam(P, qu(X, Q*X und P*X))),
	form_(art>qu, Grundform, Genus, Form, Atom).

form_(art>def, 'der', mask, [sg, nom], der).
form_(art>def, 'der', mask, [sg, gen], des).
form_(art>def, 'der', mask, [sg, dat], dem).
form_(art>def, 'der', mask, [sg, akk], den).
form_(art>def, 'die', fem, [sg, nom], die).
form_(art>def, 'die', fem, [sg, gen], der).
form_(art>def, 'die', fem, [sg, dat], der).
form_(art>def, 'die', fem, [sg, akk], die).
form_(art>def, 'das', neut, [sg, nom], das).
form_(art>def, 'das', neut, [sg, gen], des).
form_(art>def, 'das', neut, [sg, dat], dem).
form_(art>def, 'das', neut, [sg, akk], das).

form_(art>def, 'der', _, [pl, nom], die).
form_(art>def, 'der', _, [pl, gen], der).
form_(art>def, 'der', _, [pl, dat], den).
form_(art>def, 'der', _, [pl, akk], die).

form_(art>indef, 'ein', mask, [sg, nom], ein).
form_(art>indef, 'ein', mask, [sg, gen], eines).
form_(art>indef, 'ein', mask, [sg, dat], einem).
form_(art>indef, 'ein', mask, [sg, akk], einen).
form_(art>indef, 'ein', fem, [sg, nom], eine).
form_(art>indef, 'ein', fem, [sg, gen], einer).
form_(art>indef, 'ein', fem, [sg, dat], einer).
form_(art>indef, 'ein', fem, [sg, akk], eine).
form_(art>indef, 'ein', neut, [sg, nom], ein).
form_(art>indef, 'ein', neut, [sg, gen], eines).
form_(art>indef, 'ein', neut, [sg, dat], einem).
form_(art>indef, 'ein', neut, [sg, akk], ein).

	% Interrogativ

form_(art>qu, 'welch', mask, [sg, nom], welcher).
form_(art>qu, 'welch', mask, [sg, gen], welches).
% form_(art>qu, 'welch', mask, [sg, gen], welchen).
form_(art>qu, 'welch', mask, [sg, dat], welchem).
form_(art>qu, 'welch', mask, [sg, akk], welchen).
form_(art>qu, 'welch', fem, [sg, nom], welche).
form_(art>qu, 'welch', fem, [sg, gen], welcher).
form_(art>qu, 'welch', fem, [sg, dat], welcher).
form_(art>qu, 'welch', fem, [sg, akk], welche).
form_(art>qu, 'welch', neut, [sg, nom], welches).
form_(art>qu, 'welch', neut, [sg, gen], welches).
% form_(art>qu, 'welch', neut, [sg, gen], welchen).
form_(art>qu, 'welch', neut, [sg, dat], welchem).
form_(art>qu, 'welch', neut, [sg, akk], welches).

form_(art>qu, 'welch', _, [pl, nom], welche).
form_(art>qu, 'welch', _, [pl, gen], welcher).
form_(art>qu, 'welch', _, [pl, dat], welchen).
form_(art>qu, 'welch', _, [pl, akk], welche).
