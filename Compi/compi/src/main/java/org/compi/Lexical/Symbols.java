package org.compi.Lexical;

/**
 * Enum que representa los símbolos en el análisis léxico.
 * @author Enzo Palau
 */
public enum Symbols {
    openParen, closeParen, openBrace, closeBrace, openBracket, closeBracket,
    semicolon, comma, dot, colon, quote, doubleQuote;

    // constantes para los caracteres para comparar con los símbolos
    public static final char OPEN_PAREN    = '(';
    public static final char CLOSE_PAREN   = ')';
    public static final char OPEN_BRACE    = '{';
    public static final char CLOSE_BRACE   = '}';
    public static final char OPEN_BRACKET  = '[';
    public static final char CLOSE_BRACKET = ']';
    public static final char SEMICOLON     = ';';
    public static final char COMMA         = ',';
    public static final char DOT           = '.';
    public static final char COLON         = ':';
    public static final char QUOTE         = '\'';
    public static final char DOUBLE_QUOTE  = '"';
}


