
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


:- op(100, fx, '?').
:- op(600, yfx, und).
:- op(650, yfx, oder).

	% Lexikonzugriff
	% lex-Aufruf am Ende, da performanter und lex keine Variabeln ergänzt.

	% Verben

v(Zeichenlisten, Formmerkmale, Val, Semantik, Infinitiv, Atome)
	% Hier wird kein Token gelesen!
	--> {lex(v>v, Zeichenlisten, Formmerkmale, Val, Semantik, Infinitiv, Atome)}.

v_aux(Infinitiv, Formmerkmale, Atom)
	--> [Liste], {lex(v>aux, [Liste, []], Formmerkmale, _-_, '', Infinitiv, [Atom, ''])}.


	% Nomen

n([n>app, A, hum, mask, C, D, 'Anrede', '(Anrede)'], [n>app, A, hum, mask, C, D, 'Anrede', '(Anrede)'])
	--> generierung(suggest), !.

n(Info, Info)
	--> {Info = [n>N, Formmerkmale, Typ, Genus, Val, Semantik, Grundform, Atom]},
		[VF], {lex(n>N, VF, Formmerkmale, Typ, Genus, Val, Semantik, Grundform, Atom)}.


	% Pronomen

pn(Info, Info)
	--> {Info = [pro>Sub, Formmerkmale, Typ, '-', Val, Semantik, Grundform, Atom]},
		[VF], {lex(pro>Sub, VF, Formmerkmale, Typ, '-', Val, Semantik, Grundform, Atom)}.


	% Andere Wortarten (Artikel, Praepositionen)

t(Info, Info)
	--> {Info = [POS, Formmerkmale, Typ, Artmerkmal, Semantik, Grundform, Atom]},
		[VF], {lex(POS, VF, Formmerkmale, Typ, Artmerkmal, Semantik, Grundform, Atom)}.


	% Blackbox

b(Info, Info) --> {Info = [blackbox, Typ, Token]}, blackbox(Typ, Token).

blackbox(_, Token) --> \+generierung(suggest), [X],
	{nonvar(X), !, nicht_lexikalisch(X), atom_chars(TokA, X),
	atom_concat('"', TokA, Tok_), atom_concat(Tok_, '"', Token)}.

blackbox(_, Token) --> \+generierung(suggest),
	{nonvar(Token), !, atom_concat(Tok_, '"', Token), atom_concat('"', TokA, Tok_),
	atom_chars(TokA, X), nicht_lexikalisch(X)}, [X].

blackbox(_, Token) --> \+generierung(suggest), [X], {!, var(Token), var(X)}. % Bei voellig freier Generierung (s. transformation).

blackbox(hum, '(Nachname)') --> [[]].
blackbox(event, '(Titel)') --> [[]].
blackbox(thema, '(Thema)') --> [[]].
blackbox(loc, '(Raum)') --> [[]].


	% Angabe

a(Info, Info) --> {Info = [angabe, Typ, Token]}, angabe(Typ, Token).

angabe(semester>sose, '2011') --> generierung(suggest), {!}.
angabe(semester>sose, Atom) --> {nonvar(Atom), atom_chars(Atom, Zs)}, jahr(Zs-[], _), {!}.
angabe(semester>sose, Atom) --> jahr(Zs-[], _), {atom_chars(Atom, Zs)}.

angabe(semester>wise, '2011/2012') --> generierung(suggest), {!}.
angabe(semester>wise, Atom) --> {nonvar(Atom), atom_chars(Atom, A)}, jahr(A-['/'|B], NumA), [['/']], jahr(B-[], NumB), {NumB is NumA + 1, !}.
angabe(semester>wise, Atom) --> jahr(A-['/'|B], NumA), [['/']], jahr(B-[], NumB), {atom_chars(Atom, A), NumB is NumA + 1}.

angabe(semester>sem, Atom) --> angabe(semester>sose, Atom) ; angabe(semester>wise, Atom).


jahr(['2', '0', Z1, Z2|D]-D, Num) --> [J],
	{praefix_von(J, ['2', '0', Z1, Z2]), !, ziffer(Z1), ziffer(Z2), number_chars(Num, ['2','0',Z1,Z2])}.

jahr(['2', '0', Z1, Z2|D]-D, Num) --> [J],
	{praefix_von(J, [Z1, Z2]), ziffer(Z1), ziffer(Z2), number_chars(Num, ['2','0',Z1,Z2])}.


	% Verallgemeinerte Phrase

	% obj(?Beschreibung, -Atome)
	% Erkennt alle regulaeren Phrasen und bettet die Objektbeschreibung in eine Differenzliste ein.
	% Beschreibung = Differenzliste der Beschreibung des Objekts
	% Atome = Differenzliste mit der linearen Abfolge der Token als Atome

obj([Beschreibung|D]-D, Atome-D2) -->
	%{var(D)},
	(np(Beschreibung, Atome-D2)
	; cnp(Beschreibung, Atome-D2)
	; advp(Beschreibung, Atome-D2)
	; pp(Beschreibung, Atome-D2)
	; cpp(Beschreibung, Atome-D2)
	).

obj_folge(D-D, A-A) --> {true}.
obj_folge([Obj|T]-D, Atome-A) -->
	{nonvar(Obj)}, obj([Obj|D0]-D0, Atome-A0), {T = D0}, obj_folge(T-D, A0-A).


	% Phrasen
	% Erstes Argument = Liste mit Beschreibung der Phrase
	% Zweites Argument = Differenzliste mit linearer Atomabfolge der Phrase


	% cb(?Analyse, ?Atome)
	% Koordinierte Blackbox

cb([blackbox, Typ, Sem, [InfoBbox]], [Bbox|D]-D) -->
	b([blackbox, Typ, Bbox], InfoBbox),
	{Sem = lam(Art, lam(N, Art * (N * Bbox)))}.

	% X und X
cb([blackbox, Typ, Sem, [InfoA, InfoCon, InfoB]], [BoxA, Con|BoxB]-D) -->
	\+generierung(suggest),
	b([blackbox, Typ, BoxA], InfoA),
	t([junkt>con, [], con, '', SemCon, _, Con], InfoCon),
	{InfoB = [blackbox, Typ, SemB, _]},
	cb(InfoB, BoxB-D),
	{Sem = lam(Art, lam(N, SemCon * (Art*(N*BoxA)) * (SemB*Art*N) ))}.


	% NPs

	% cnp(?Analyse, ?Atome)
	% Koordinierte Nominalphrasen

	% Die Professoren Müller und Schmidt halten die Vorlesung
cnp([np, [3, pl, Cas], Typ, def, _, Sem, [InfoArt, InfoN, InfoBbox]], [Art, N|Bbox]-D) -->
	\+generierung(suggest),
	t([art>def, [pl, Cas], def, Genus,       SemArt, _, Art], InfoArt),
	n([n>app,   [pl, Cas], Typ, Genus, _Val, SemN, _, N],     InfoN),
	{InfoBbox = [blackbox, Typ, SemBbox, [_,_,_|_]]},	% Minimum "A und B"
	cb(InfoBbox, Bbox-D),
	{Sem = (SemBbox * SemArt) * SemN}.

	% Herr Mueller und Professor Mustermann und welcher Professor
cnp([np, [3, pl, Cas], Typ, Def, _, Sem, [InfoA, InfoCon, InfoB]], NP_A-D) -->
	\+generierung(suggest),
	{InfoA  =  [np, [3, sg, Cas], Typ, DefA, _, SemA, _]},
	{InfoB  =  [np, [3, _N, Cas], Typ, DefB, _, SemB, _]},
	{InfoCon = [junkt>con, [], con, '', SemCon, _, Con]},
	np(InfoA, NP_A-[Con|NP_B]),
	t(InfoCon, InfoCon),
	(np(InfoB, NP_B-D) ; cnp(InfoB, NP_B-D)),
	{(DefA = DefB
		-> Def = DefA
		; ((DefA = qu ; DefB = qu) -> Def = qu ; true)
	)},
	{Sem = (SemCon * SemA) * SemB}.

	% Herr Mueller oder Professor Mustermann
cnp([np, [3, sg, Cas], Typ, Def, _, Sem, [InfoA, InfoCon, InfoB]], NP_A-D) -->
	\+generierung(suggest), !,
	{InfoA  =  [np, [3, sg, Cas], Typ, Def, _, SemA, _]},
	{InfoB  =  [np, [3, sg, Cas], Typ, Def, _, SemB, _]},
	{InfoCon = [junkt>dis, [], dis, '', SemDis, _, Con]},
	np(InfoA, NP_A-[Con|NP_B]),
	t(InfoCon, InfoCon),
	(np(InfoB, NP_B-D) ; cnp(InfoB, NP_B-D)),
	{normalisiere_term(SemA, ex(X, SA)), normalisiere_term(SemB, ex(X, SB)),
	Sem = ((SemDis * X) * SA) * SB}.


	% Einfache Nominalphrasen

	% Vorlesungstitel und Namen direkt: Syntax, "Mathe 1", "Max Mustermann", ...
np([np, [3, sg, Cas], Typ, def, Val, Sem, [InfoBbox]], [Bbox|D]-D) -->
	{(Typ = event, Gf = 'Veranstaltung') ; (Typ = hum, Gf = 'Dozent')},
	b([blackbox, Typ, Bbox], InfoBbox),
	{!, vollform(n>app, _, [sg, Cas], Typ, _, Val, SemN, Gf, _),
	Sem = lam(P, ex(X, (SemN * Bbox)*X und P*X))}.

	% die Syntax-Vorlesung, das XML-Seminar
np([np, [3, sg, Cas], event, def, Val, Sem, [InfoArt, InfoBbox]], [Art, Bbox|D]-D) -->
	t([art>def, [sg, Cas], def, Genus, SemArt, _, Art], InfoArt),
	\+generierung(suggest),
	b([blackbox, event, Bbox], InfoBbox),
	{!, bindestrich_kompositum(Bbox, Titel, Nomen),
	lex(n>app, ['#'|Nomen], [sg, Cas], event, Genus, Val, SemN, _, _),
	Sem = SemArt * (SemN * Titel)}.

	% Herr X, Professor X, ...
np([np, [3, sg, Cas], hum, def, _, Sem, [InfoAnrede, InfoName]], [Anrede, Name|D]-D) -->
	n([n>app, [sg, Cas], hum, _Genus, _, SemAnrede, _, Anrede], InfoAnrede),
	b([blackbox, hum, Name], InfoName),
	{!, Sem = lam(P, ex(X, (SemAnrede*Name)*X und P*X))}.

	% ein Dozent, eine Vorlesung, ... welcher Dozent, welche Vorlesung
np([np, [3, Num, Cas], Typ, Def, Val, Sem, [InfoArt, InfoN]], [Art, N|D]-D) -->
	{(Def = indef ; Def = qu)},
	gen_bedingung(Typ, hum, Gf, 'Dozent'),
	gen_bedingung(Typ, event, Gf, 'Veranstaltung'),
	t([art>Def, [Num, Cas], Def,      Genus,     SemArt, _, Art], InfoArt),
	n([n>n,     [Num, Cas], Typ, Genus, Val,  SemN, Gf,  N],   InfoN),
	{Sem = SemArt * SemN}.

	% die Vorlesung Y, ...
np([np, [3, sg, Cas], Typ, Def, Val, Sem, [InfoArt, InfoN, InfoBbox]], [Art, N, Bbox|D]-D) -->
	(generierung(suggest)
		-> {Def = def}	% nur def bei Suggest
		; {Def = def ; Def = indef}
	),
	t([art>Def, [sg, Cas], Def, Genus,     SemArt, _, Art], InfoArt),
	n([n>app,   [sg, Cas], Typ, Genus, Val,  SemN, _, N],   InfoN),
	b([blackbox, Typ, Bbox], InfoBbox),
	{Sem = SemArt * (SemN * Bbox)}.

	% wer, was
np([np, [3, Num, Cas], Typ, Sub, Val, Sem, [InfoPro]], [Pro|D]-D) -->
	\+generierung(suggest),
	pn([pro>Sub, [Num, Cas], Typ, '-', Val, Sem, _, Pro], InfoPro).


	% PPs

	% cpp(?Analyse, ?Atome)
	% Koordinierte Praepositionalphrase

	% Im Sommersemester 2008 und im Sommersemester 2007
cpp([pp, [Cas], Typ, Def, Sem, [InfoA, InfoCon, InfoB]], PP_A-D) -->
	\+generierung(suggest),
	{InfoA  =  [pp, [Cas], Typ, DefA, SemA, _]},
	{InfoB  =  [pp, [Cas], Typ, DefB, SemB, _]},
	{InfoCon = [junkt>con, [], con, '', SemCon, _, Con]},
	pp(InfoA, PP_A-[Con|PP_B]),
	t(InfoCon, InfoCon),
	(pp(InfoB, PP_B-D) ; cpp(InfoB, PP_B-D)),
	{(DefA = DefB
		-> Def = DefA
		; ((DefA = qu ; DefB = qu) -> Def = qu ; true)
	)},
	{Sem = (SemCon * SemA) * SemB}.

	% Im Raum 114 oder im Raum 113
cpp([pp, [Cas], Typ, Def, Sem, [InfoA, InfoCon, InfoB]], PP_A-D) -->
	\+generierung(suggest),
	{InfoA  =  [pp, [Cas], Typ, Def, SemA, _]},
	{InfoB  =  [pp, [Cas], Typ, Def, SemB, _]},
	{InfoCon = [junkt>dis, [], dis, '', SemDis, _, Con]},
	pp(InfoA, PP_A-[Con|PP_B]),
	t(InfoCon, InfoCon),
	(pp(InfoB, PP_B-D) ; cpp(InfoB, PP_B-D)),
	{normalisiere_term(SemA, ex(X, SA)), normalisiere_term(SemB, ex(X, SB)),
	Sem = ((SemDis * X) * SA) * SB}.


	% Einfache Praepositionalphrasen

	% ueber "Syntax"
pp([pp, [Cas], Typ, '', Sem, [InfoPraep, InfoBbox]], [Praep, Bbox|D]-D) -->
	(generierung(suggest) -> {Gen = ja} ; {Gen = nein}),	% Bei Generierung lex-Exception verhindern
	t([p>bbox, [], Typ, Cas, SemPraep, _, Praep], InfoPraep),
	b([blackbox,   Typ, Bbox], InfoBbox),
	%({gen = ja -> true ; !}),
	{!, Sem = SemPraep * Bbox}.

	% im Raum 1.14
pp([pp, [Cas], loc, def, Sem, [InfoPraep, InfoRaum, InfoBbox]], [Praep, Raum, Bbox|D]-D) -->
	t([p>def, [], loc, Cas, SemPraep, _, Praep], InfoPraep),
	n([n>app, [sg, Cas], loc, _, _, SemRaum, _, Raum], InfoRaum),
	b([blackbox, loc, Bbox], InfoBbox),
	{!, Sem = SemPraep * (SemRaum * Bbox)}.

	% am Montag
pp([pp, [Cas], temp_d, def, Sem, [InfoPraep, InfoTag]], [Praep, Tag|D]-D) -->
	\+generierung(suggest),
	t([p>def, [], temp_d, Cas, SemPraep, _, Praep], InfoPraep),
	n([n>n, [sg, Cas], temp_d, _, _, SemTag, _, Tag], InfoTag),
	{Sem = SemPraep * SemTag}.

	% von dem Dozenten, in dem Raum 114, in einem Raum
pp([pp, [Cas], Typ, Def, Sem, [InfoPraep, InfoNP]], [Praep|NP]-D) -->
	{InfoNP = [np, [_, _, Cas], Typ, Def, _, SemNP, _]},	% Valenz wird nicht durchgereicht: *Wo befindet sich der Raum ueber Syntax der Vorlesung
	t([p>p,   [],               Typ, Cas, SemPraep, _, Praep], InfoPraep),
	(np(InfoNP, NP-D) ; cnp(InfoNP, NP-D)),
	{Sem = SemPraep * SemNP}.

	% in welchem Semester
	% Grundsaetzlich auch in "von dem Dozenten"-Regel moeglich, aber so effizienter wegen Typ>_
pp([pp, [Cas], semester, qu, Sem, [InfoPraep, InfoArt, InfoN]], [Praep, Art, N|D]-D) -->
	t([p>p,    [],        semester,   Cas,      SemPraep, _, Praep], InfoPraep),
	t([art>qu, [sg, Cas], qu,         Genus,    SemArt,   _, Art],   InfoArt),
	n([n>n,    [sg, Cas], semester>_, Genus, _, SemN,     _, N],     InfoN),
	{Sem = SemPraep * (SemArt * (SemN * ''))}.


	% PPs mit Angaben

	% im (Semesterangabe)
pp([pp, [dat], semester, '', Sem, [InfoPraep, 'Semesterangabe']], [Praep, '(Semesterangabe)'|D]-D) -->
	generierung(suggest), {!},
	t([p>def, [], semester, dat, Sem, _, Praep], InfoPraep).

	% im Sommersemester 2009
pp([pp, [Cas], semester, def, Sem, [InfoPraep, InfoN, InfoAngabe]], [Praep, N, Angabe|D]-D) -->
	t([p>def, [],        semester,        Cas,       SemPraep, _, Praep], InfoPraep),
	n([n>n,   [sg, Cas], semester>Subtyp, _Genus, _, SemN,     _, N],     InfoN),
	a([angabe,           semester>Subtyp, Angabe],                        InfoAngabe),
	{Sem = SemPraep * (SemN * Angabe)}.

	% in dem Sommersemester 2009
pp([pp, [Cas], semester, def, Sem, [InfoPraep, InfoArt, InfoN, InfoAngabe]], [Praep, Art, N, Angabe|D]-D) -->
	t([p>p,     [],        semester,        Cas,      SemPraep, _, Praep], InfoPraep),
	t([art>def, [sg, Cas], def,             Genus,    SemArt,   _, Art],   InfoArt),
	n([n>n,     [sg, Cas], semester>Subtyp, Genus, _, SemN,     _, N],     InfoN),
	a([angabe,             semester>Subtyp, Angabe],                       InfoAngabe),
	{Sem = SemPraep * (SemArt * (SemN * Angabe))}.


	% AdvPs

	% wo, wann
advp([advp, [_], Typ, qu, Sem, [InfoAdv]], [Adv|D]-D) -->
	(generierung(suggest)
		-> {member(Typ, [temp_d, loc])}
		; {true}
	),
	t([adv>qu, [], Typ, qu, Sem, _, Adv], InfoAdv).

	% montags, dienstags
advp([advp, [_], Typ, '', Sem, [InfoAdv]], [Adv|D]-D) -->
	\+generierung(suggest),
	t([adv>adv, [], Typ, '', Sem, _, Adv], InfoAdv).


	% Hilfspraedikate

gen_bedingung(Bedingung, Bedingung, Dann, Dann) --> generierung(suggest), {!}.
gen_bedingung(_, _, _, _) --> {true}.

	% bindestrich_kompositum(+Kompositum, ?Teil1, ?Teil2)
	% Trennt ein Kompositum am Bindestrich in den vorderen und den hinteren Teil.
	% Kompositum = Liste der Zeichen oder Atom - beginnt mit " und endet mit "
	% Teil1 = Liste der Zeichen oder Atom - beginnt mit " und endet mit "
	% Teil2 = Liste der Zeichen

bindestrich_kompositum([], _, _) :- !, fail.
bindestrich_kompositum(['-'|T], ['"'], T2) :- !, entferne_bboxmarkierung(T, T2).
bindestrich_kompositum([H|T], [H|T2], Teil2) :- bindestrich_kompositum(T, T2, Teil2), !.

bindestrich_kompositum(K, A, B) :-
	atomic(K), atom_chars(K, LForm),
	bindestrich_kompositum(LForm, A1, B),
	atom_chars(A, A1).

entferne_bboxmarkierung(['"'], []).
entferne_bboxmarkierung([H|T], [H|T2]) :- entferne_bboxmarkierung(T, T2).
