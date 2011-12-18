
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


	% Sonderbehandlung der Hilfsverben wegen Konsonantenveraenderungen im Stamm.

	% hilfsverb(?Infinitiv, +Formmerkmale, ?Vollform)
	% Formmerkmale = indefinite Form (inf, part2) oder finite Merkmale ([Person, Numerus, Tempus])

	% Haben, Duden-Regel 650

hilfsverb(haben, inf, haben).
hilfsverb(haben, part2, gehabt).

hilfsverb(haben, [Pers, Num, Temp], Vollform) :-
	einfache_zeiten(Temp), numerus(Num), person(Pers),
	grundform(v>aux, haben, '', rg, [], ''),
	((Temp = praet)
		-> Stamm = hat
		; ( (Temp = praes, Num = sg, (Pers = 2 ; Pers = 3)) -> Stamm = ha ; Stamm = hab )
	),
	konjugiere(Stamm, '', rg, [_, Pers, Num, Temp], Vollform, '').


	% Werden, Duden-Regel 649

hilfsverb(werden, inf, werden).
hilfsverb(werden, part2, worden).

hilfsverb(werden, [2, sg, praes], wirst).
hilfsverb(werden, [3, sg, praes], wird).
hilfsverb(werden, [Pers, sg, praet], wurde) :- (Pers = 1 ; Pers = 3).
hilfsverb(werden, [Pers, Num, Temp], Vollform) :-
	einfache_zeiten(Temp), numerus(Num), person(Pers),
	\+member([Pers, Num, Temp], [[2,sg,praes], [3,sg,praes], [1,sg,praet], [3,sg,praet]]),
	grundform(v>aux, werden, '', FlexKlasse, [], ''),
	flektiere_stamm(werden, [Pers, Num, Temp], FlexKlasse, FlexStamm),
	konjugiere(FlexStamm, '', FlexKlasse, [_, Pers, Num, Temp], Vollform, '').


	% Sein, Duden-Regel 700

hilfsverb(sein, inf, sein).
hilfsverb(sein, part2, gewesen).

hilfsverb(sein, [1, sg, praes], bin).
hilfsverb(sein, [2, sg, praes], bist).
hilfsverb(sein, [3, sg, praes], ist).
hilfsverb(sein, [Pers, pl, praes], sind) :- (Pers = 1 ; Pers = 3).
hilfsverb(sein, [2, pl, praes], seid).

hilfsverb(sein, [Pers, sg, praet], war) :- (Pers = 1 ; Pers = 3).
hilfsverb(sein, [2, sg, praet], warst).
hilfsverb(sein, [Pers, pl, praet], waren) :- (Pers = 1 ; Pers = 3).
hilfsverb(sein, [2, pl, praet], wart).
