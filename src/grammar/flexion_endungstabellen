
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


	% endung(?Wortart, ?Flexionsklasse, ?Formmerkmale, ?Endung)
	% Fakten der Flexionsendungen in Abhaengigkeit von Formmerkmalen.
	% Formmerkmale = (bei Verben) [Person, Numerus, Tempus]
	% Formmerkmale = (bei Nomen) [Numerus, Casus]

	% Verben

	% Regelmaessige Konjugation: schwache Verben

endung(v, rg, [1,sg,praes], 'e').
endung(v, rg, [2,sg,praes], 'st').
endung(v, rg, [3,sg,praes], 't').
endung(v, rg, [1,pl,praes], 'en').
endung(v, rg, [2,pl,praes], 't').
endung(v, rg, [3,pl,praes], 'en').

endung(v, rg, [1,sg,praet], 'te').
endung(v, rg, [2,sg,praet], 'test').
endung(v, rg, [3,sg,praet], 'te').
endung(v, rg, [1,pl,praet], 'ten').
endung(v, rg, [2,pl,praet], 'tet').
endung(v, rg, [3,pl,praet], 'ten').

endung(v, rg, part2, 't'). % Partizip II

	% Unregelmaessige Konjugation: starke Verben

endung(v, urg(_,_), [1,sg,praes], 'e').
endung(v, urg(_,_), [2,sg,praes], 'st').
endung(v, urg(_,_), [3,sg,praes], 't').
endung(v, urg(_,_), [1,pl,praes], 'en').
endung(v, urg(_,_), [2,pl,praes], 't').
endung(v, urg(_,_), [3,pl,praes], 'en').

endung(v, urg(_,_), [1,sg,praet], '').
endung(v, urg(_,_), [2,sg,praet], 'st').
endung(v, urg(_,_), [3,sg,praet], '').
endung(v, urg(_,_), [1,pl,praet], 'en').
endung(v, urg(_,_), [2,pl,praet], 't').
endung(v, urg(_,_), [3,pl,praet], 'en').

endung(v, urg(_,_), part2, 'en').

	% Nomen

	% Singular

endung(n, -, [sg,nom], '').
endung(n, -, [sg,gen], '').
endung(n, -, [sg,dat], '').
endung(n, -, [sg,akk], '').

endung(n, es, [sg,nom], '').
endung(n, es, [sg,gen], 's').
% endung(n, es, [sg,gen], 'es').
endung(n, es, [sg,dat], '').
% endung(n, es, [sg,dat], 'e').
endung(n, es, [sg,akk], '').

endung(n, en, [sg,nom], '').
endung(n, en, [sg,gen], 'en').
endung(n, en, [sg,dat], 'en').
endung(n, en, [sg,akk], 'en').

endung(n, s, [sg,nom], '').
endung(n, s, [sg,gen], 's').
endung(n, s, [sg,dat], '').
endung(n, s, [sg,akk], '').

	% Plural

endung(n, en, [pl,nom], 'en').
endung(n, en, [pl,gen], 'en').
endung(n, en, [pl,dat], 'en').
endung(n, en, [pl,akk], 'en').

endung(n, e, [pl,nom], 'e').
endung(n, e, [pl,gen], 'e').
endung(n, e, [pl,dat], 'en').
endung(n, e, [pl,akk], 'e').

endung(n, -, [pl,nom], '').
endung(n, -, [pl,gen], '').
endung(n, -, [pl,dat], '').
endung(n, -, [pl,dat], 'n').
endung(n, -, [pl,akk], '').
