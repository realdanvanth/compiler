grammar Expr;
INT: 'int';
IF: 'if';
ELSE: 'else';
EXIT: 'exit';
BOOLEAN: 'boolean';
WHILE:'while';
EQUAL: '=';
NUMBER: [0-9]+;
TRUE:'true';
FALSE:'false';
OPEN_BRACES: '(';
CLOSE_BRACES: ')';
OPEN_CURLY:'{';
CLOSE_CURLY:'}';
NUMOP:'+'|'*'|'/'|'%'|'-';
BOOLOP:'&'|'|';
BOOLNUMOP:'>'|'<'|':';
SEMI_COLON:';';
IDENT: [A-Za-z]+;
expr : OPEN_BRACES expr CLOSE_BRACES
      | expr NUMOP expr
      | IDENT
      | NUMBER;
boolexpr : OPEN_BRACES boolexpr CLOSE_BRACES
      | boolexpr BOOLOP boolexpr
      | expr BOOLNUMOP expr
      | TRUE | FALSE;
block: OPEN_CURLY statements+ CLOSE_CURLY;
statements: assignstmt
          | ifstmt
          | whilestmt
          | exitstmt;
assignstmt : INT IDENT EQUAL expr SEMI_COLON
            | BOOLEAN IDENT EQUAL boolexpr SEMI_COLON
            | IDENT EQUAL expr SEMI_COLON;
ifstmt : IF OPEN_BRACES boolexpr CLOSE_BRACES  block ELSE ifstmt
        | IF OPEN_BRACES boolexpr CLOSE_BRACES block ELSE block
        | IF OPEN_BRACES boolexpr CLOSE_BRACES block SEMI_COLON;
whilestmt: WHILE OPEN_BRACES boolexpr CLOSE_BRACES block; 
exitstmt: EXIT OPEN_BRACES expr CLOSE_BRACES SEMI_COLON;
prog : statements+;
WS: [ \n\r\t]+ -> skip;
