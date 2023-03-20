lexer grammar Lexicon
	;

LITENT
	: [0-9]+
	;

LITREAL
	: [0-9]+ '.' [0-9]+
	;

LITCHAR
	: '\'\\n\''
	| '\''~[\n\r\t]'\''
	;

IDENT
	: [a-zA-Z_][a-zA-Z0-9_]*
	;

BLOCK_COMMENT
	: '/*' .*? '*/' -> skip
	;
	
LINE_COMMENT
	: '//' .*? '\n' -> skip
	;

WS
	: [ \t\r\n]+ -> skip
	;
	
