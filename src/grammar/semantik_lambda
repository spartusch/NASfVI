
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


	% normalisiere_semantik(+Satzsemantik, -Normalform)
	% Normalisiert die Satzsemantik.
	% Satzsemantik = Liste mit Semantik des Verbs und der Angaben

normalisiere_semantik([SemV_, SemA_], [SemV, SemA]) :-
	normalisiere_term(SemV_, SemV),
	normalisiere_term(SemA_, SemA).

normalisiere_term(Term, Normalform) :-
	betanormalform(Term, Nf_),
	normalisiere(Nf_, Normalform).

%normalisiere_term(Term, Normalform) :-
%	betanormalform(Term, Normalform).


	% normalisiere(+Term, -Normalform)
	% Fuehrt normalisiere/3 aus bis sich keine Aenderung des Terms mehr ergibt.

normalisiere(Term, Normalform) :-
	normalisiere(Term, Nf, Aenderung),
	(
		Aenderung = 1
		-> normalisiere(Nf, Normalform)
		; Normalform = Nf
	).

	% normalisiere(+Term, -Normalform, -Aenderung)
	% Entfernt Variabeln, die direkt als Argumente von und/2, oder/2, */2 verwendet werden, und die entsprechenden Funktoren.
	% Term = Semantik-Term, der normalisiert werden soll
	% Normalform = Normalisierter Term
	% Aenderung = 1 falls Term und Normalform sich unterscheiden, sonst 0

normalisiere(X, X, 0) :- var(X), !.

normalisiere(ex(X, R0), ex(X, R), Aenderung) :- nonvar(R0), !, normalisiere(R0, R, Aenderung).
normalisiere(qu(X, R0), qu(X, R), Aenderung) :- nonvar(R0), !, normalisiere(R0, R, Aenderung).
normalisiere(lam(_, R0), R, 1) :- !, normalisiere(R0, R, _).

normalisiere(Pred, Erg, Aenderung) :-
	member(Pred, [und(A,B), oder(A,B), A*B]), !,
	Pred =.. Liste,
	normalisiere(Liste, Erg, Aenderung).

normalisiere([_,A0,B0], B, 1) :- var(A0), nonvar(B0), !, normalisiere(B0, B, _).
normalisiere([_,A0,B0], A, 1) :- nonvar(A0), var(B0), !, normalisiere(A0, A, _).
normalisiere([_,A0,B0], _, 1) :- var(A0), var(B0), !.
normalisiere([Func,A0,B0], Pred, 1) :-
	normalisiere(A0, A, AenderungA), normalisiere(B0, B, AenderungB),
	(AenderungA = 1 ; AenderungB = 1), !, Pred =.. [Func, A, B].
normalisiere([Func, A, B], Pred, 0) :- !, Pred =.. [Func, A, B].

normalisiere(X, X, 0).


	% betanormalform(+LambdaTerm, -Normalform)
	% Normalisiert den LambdaTerm durch Anwendung von Alpha- und Beta-Konversionen.
	% LambdaTerm = Lambdaterm der Eingabe
	% Normalform = Beta-Normalform von LambdaTerm

betanormalform(Term, Normalform) :-
	beta(Term, ReduzierterTerm, Aenderung),
	(
		Aenderung = 1
		-> betanormalform(ReduzierterTerm, Normalform)
		; Normalform = ReduzierterTerm
	).

	% betanormalform_liste(+Liste, -NormalisierteListe)
	% Normalisiert jedes Element von Liste.

betanormalform_liste([T|Ts], [Nf|Nfs]) :-
	betanormalform(T, Nf),
	betanormalform_liste(Ts, Nfs).
betanormalform_liste([], []).

	% beta(+LambdaTerm, -ReduzierterTerm, -Aenderung)
	% Fuehrt eine Beta-Reduktion mit LambdaTerm durch.
	% LambdaTerm = Der zu verarbeitende Lambdaterm
	% ReduzierterTerm = Der Beta-reduzierte LambdaTerm
	% Aenderung = 1 falls eine Beta-Reduktion durchgefuehrt wurde, sonst 0

beta(X, X, 0) :- var(X), !.
beta(T*S, T*SR, Aenderung) :- var(T), !, beta(S, SR, Aenderung).

beta(lam(X_, R_)*S, R, 1) :-
	!, alpha(lam(X_, R_), lam(X, R)),
	X = S.	% simuliert R[X/S]

beta(T*S, TR, Aenderung) :-
	!, beta(T, TR_, Aenderung_),
	(
		Aenderung_ = 1
		-> TR = TR_*S, Aenderung = 1
		; beta(S, SR, Aenderung), TR = T*SR
	).

beta(lam(X, R_), lam(X, R), Aenderung) :- !, beta(R_, R, Aenderung).

beta(T, TR, 0) :- % fuer f(Term,..,Term)
	compound(T), !,
	T =.. [F|Args],
	betanormalform_liste(Args, Nfs),
	TR =.. [F|Nfs].    

beta(T, T, 0) :- !. % Konstante

	% alpha(+Term, -UmbenannterTerm)
	% Fuehrt eine Alpha-Konversion durch, d. h. benennt gebundene Variabeln in Term um.

alpha(T, T_) :- alpha(T, [], T_), !.

	% alpha(+LambdaTerm, +Variabeln, -NeuerTerm)
	% Benennt die in LambdaTerm gebundenen Variabeln um.
	% LambdaTerm = Lambdaterm dessen mit Lambda gebundene Variabeln umbenannt werden sollen
	% Variabeln = Liste von (alteVariabel, neueVariabel)-Paaren fuer jede gebundene Variabel (sollte [] sein)
	% NeuerTerm = LambdaTerm in dem jedes Vorkommen von alteVariabel durch neueVariabel ersetzt ist

alpha(V_, L, V) :- var(V_), !, umbenennen(V_, L, V).

alpha(lam(V_,R_), L, lam(V,R)) :- !, alpha(R_, [(V_,V)|L], R).
alpha(T_*S_, L, T*S) :- !, alpha(T_, L, T), alpha(S_, L, S).
alpha(C, _, C) :- atomic(C), !.

alpha(T_, L, T) :- % fuer: f(Term,...,Term)
	compound(T_), !,
	T_ =.. [F|Ts_],
	alpha_liste(Ts_, L, Ts),
	T =.. [F|Ts].

	% alpha_liste(+Liste, +Variabeln, -UmbenannteListe)
	% Fuehrt eine Alpha-Konversion fuer jedes Element von Liste durch.
	
alpha_liste([T_|Ts_], L, [T|Ts]) :-
	alpha(T_, L, T),
	alpha_liste(Ts_, L, Ts).
alpha_liste([], _, []).

	% umbenennen(+V, +Variabeln, -UmbenanntesV)
	% Liefert die umbenannte Variabel der Variabel V gemaess Variabeln.

umbenennen(V, [], V).
umbenennen(V, [(Var,UV)|_], UV) :- V == Var, !.
umbenennen(V, [_|T], UV) :- !, umbenennen(V, T, UV).
