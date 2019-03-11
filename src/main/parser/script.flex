package com.gs.parser;

import beaver.Symbol;
import beaver.Scanner;

import com.gs.parser.GameScriptParser.Terminals;

%%

%class GameScriptScanner
%public
%extends Scanner
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return mkTok(Terminals.EOF, "eof");
%eofval}
%unicode
%line
%column
%{
	StringBuffer string = new StringBuffer();
	Character character = null;
	Character translateEscapeSeq(String s) {
		if (s.equals("\\n")) return '\n';
		if (s.equals("\\r")) return '\r';
		if (s.equals("\\'")) return '\'';
		if (s.equals("\\\"")) return '"';
		if (s.equals("\\t")) return '\t';
		if (s.equals("\\\\")) return '\\';
		return s.charAt(1);
	}
	private Symbol mkTok(short id)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength());
	}
	private Symbol mkTok(short id, Object value)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
	}
%}
EOL = \r|\n|\r\n
WS  = {EOL} | [ \t\f]

Float = [:digit:] [:digit:]* "." [:digit:]+
Int = [:digit:] [:digit:]*
Ident = [a-zA-Z] [a-zA-Z0-9_]*

%state STRING, MLCOMMENT

%%

<YYINITIAL> {
	"//"[^\r\n]* {EOL} { /* ignore comment */ }
	"#"[^\r\n]* {EOL} { /* ignore shell-comment */ }
	"/*" { yybegin(MLCOMMENT); }
	{WS}+ { /* ignore */ }
	"." { return mkTok(Terminals.DOT); }
	"+=" { return mkTok(Terminals.ASSIGN_ADD); }
	"-=" { return mkTok(Terminals.ASSIGN_SUB); }
	"*=" { return mkTok(Terminals.ASSIGN_MUL); }
	"/=" { return mkTok(Terminals.ASSIGN_DIV); }
	"%=" { return mkTok(Terminals.ASSIGN_MOD); }
	"==" { return mkTok(Terminals.EQUAL); }
	"||" { return mkTok(Terminals.LOGICALOR); }
	"&&" { return mkTok(Terminals.LOGICALAND); }
	"!=" { return mkTok(Terminals.NOTEQUAL); }
	"<=" { return mkTok(Terminals.LESSEQ); }
	">=" { return mkTok(Terminals.GREATEREQ); }
	"<" { return mkTok(Terminals.LESS); }
	">" { return mkTok(Terminals.GREATER); }
	"if" { return mkTok(Terminals.IF); }
	"else" { return mkTok(Terminals.ELSE); }
	"default" { return mkTok(Terminals.DEFAULT); }
	"def" { return mkTok(Terminals.DEF); }
	"while" { return mkTok(Terminals.WHILE); }
	"for" { return mkTok(Terminals.FOR); }
	"return" { return mkTok(Terminals.RETURN); }
	"true" { return mkTok(Terminals.TRUE, new Boolean(true)); }
	"false" { return mkTok(Terminals.FALSE, new Boolean(false)); }
	"null" { return mkTok(Terminals.NULL); }
	"case" { return mkTok(Terminals.CASE); }
	"switch" { return mkTok(Terminals.SWITCH); }
	"break" { return mkTok(Terminals.BREAK); }
	"List" { return mkTok(Terminals.LIST_KW); }
	"Array" { return mkTok(Terminals.ARRAY_KW); }
	"Map" { return mkTok(Terminals.MAP_KW); }
	"[" { return mkTok(Terminals.LSQBRACKET); }
	"]" { return mkTok(Terminals.RSQBRACKET); }
	"{" { return mkTok(Terminals.LBRACE); }
	"}" { return mkTok(Terminals.RBRACE); }
	{Float} { return mkTok(Terminals.FLOAT, Double.parseDouble(yytext())); }
	{Int} { return mkTok(Terminals.INT, Integer.parseInt(yytext())); }
	{Ident} { return mkTok(Terminals.IDENT, yytext()); }
	"(" { return mkTok(Terminals.LPAREN); }
	")" { return mkTok(Terminals.RPAREN); }
	";" { return mkTok(Terminals.SEMICOLON); }
	":" { return mkTok(Terminals.COLON); }
	"," { return mkTok(Terminals.COMMA); }
	"=" { return mkTok(Terminals.ASSIGN); }
	"+" { return mkTok(Terminals.PLUS); }
	"-" { return mkTok(Terminals.MINUS); }
	"*" { return mkTok(Terminals.MUL); }
	"/" { return mkTok(Terminals.DIV); }
	"|" { return mkTok(Terminals.PIPE); }
	"%" { return mkTok(Terminals.MODULUS); }
	\"  { string.setLength(0); yybegin(STRING); }
	\\  { return mkTok(Terminals.BACKSLASH); }
	"'"."'" { return mkTok(Terminals.CHARACTER, yycharat(yylength() - 2)); }
	"'"\\."'" { return mkTok(Terminals.CHARACTER, translateEscapeSeq(yytext().substring(1, 3))); }
//	[\"] ([^\"\\]|\\.)* [\"] { return mkTok(Terminals.STRING, yytext()); }
}
<MLCOMMENT> {
	"*/"                         { yybegin(YYINITIAL); }
	[^*]+                        { }
	"*"                          { }
	\n                           { }
}
<STRING> {
  \"                             { yybegin(YYINITIAL); return mkTok(Terminals.STRING, string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

. { throw new Scanner.Exception("unexpected character '" + yytext() + '"'); }

