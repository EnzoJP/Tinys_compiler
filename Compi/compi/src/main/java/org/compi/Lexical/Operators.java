package org.compi.Lexical;

/**
 * Enum que representa los operadores en el análisis léxico.
 * @author Enzo Palau
 */
public enum Operators {
    // Arithmetic Operators
    sumOperator,
    divOperator,
    substractionOperator,
    multiplicationOperator,
    incrementOperator,
    decrementOperator,

    //comparison operators
    equalOperator,
    notEqualOperator,
    greaterThanOperator,
    lessThanOperator,
    greaterThanOrEqualOperator,
    lessThanOrEqualOperator,

    // Logical Operators
    andOperator,
    orOperator,
    notOperator,

    // Assignment Operators
    assignmentOperator;

    //Constantes de caracteres para comparación rápida
    public static final char PLUS      = '+';
    public static final char MINUS     = '-';
    public static final char STAR      = '*';
    public static final char SLASH     = '/';
    public static final char EQUAL     = '=';
    public static final char BANG      = '!';
    public static final char GREATER   = '>';
    public static final char LESS      = '<';
    public static final char AMPERSAND = '&';
}

