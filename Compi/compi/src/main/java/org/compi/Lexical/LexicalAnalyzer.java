package org.compi.Lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Clase que representa el analizador léxico, encargado de convertir el código fuente en tokens.
 * Lee el archivo fuente carácter a carácter usando un BufferedReader.
 * Usa una LinkedList como buffer del lexema actual y un lookAhead para el siguiente carácter.
 * @author Enzo Palau
 */
public class LexicalAnalyzer {

    private final File sourceFile;
    private static final String[] KEYWORDS = {
            "class", "impl", "else", "if", "false", "true", "while", "ret",
            "nil", "new", "fn", "st", "pub", "self", "div", "for", "in","start"
    };
    private final BufferedReader reader;   // lector del archivo fuente
    private final LinkedList<Character> buffer = new LinkedList<>(); // buffer del lexema actual

    private int lookAhead = -2;  // -2 = no inicializado, -1 = EOF
    private int line   = 1;
    private int column = 0;

    // columna donde empezó el token actual
    private int tokenStartColumn;
    private int tokenStartLine;

    public LexicalAnalyzer(File sourceFile) {
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
            this.sourceFile = sourceFile;
            advance(); // carga el primer carácter en lookAhead
        } catch (IOException e) {
            throw new LexicalExeptions("ERROR: No se puede abrir el archivo fuente: " + sourceFile.getName());
        }
    }

    /** Lee el siguiente carácter del archivo y lo pone en lookAhead.
     * Es una referencia al siguiente char */
    private void advance() {
        try {
            lookAhead = reader.read();
            if (lookAhead != -1) {
                column++;
            }
        } catch (IOException e) {
            lookAhead = -1;
        }
    }

    /** Consume lookAhead: lo agrega al buffer y avanza. */
    private void consume() {
        buffer.add((char) lookAhead);
        advance();
    }

    /** Devuelve el lexema acumulado en buffer y lo limpia. */
    private String flushBuffer() {
        StringBuilder sb = new StringBuilder();
        for (char c : buffer) sb.append(c);
        buffer.clear();
        return sb.toString();
    }

    /** Crea un token con la posición donde empezó. */
    private token makeToken(String type, String lexeme) {
        return new token(type, lexeme, tokenStartLine, tokenStartColumn);
    }

    /** Verifica si un lexema es palabra reservada. */
    private boolean isKeyword(String lexeme) {
        for (String kw : KEYWORDS) {
            if (kw.equals(lexeme)) return true;
        }
        return false;
    }


    /**
     * Retorna el siguiente token del archivo fuente.
     * retorna null si se llega al final del archivo.
     * Lanza LexicalExeptions ante un error léxico.
     */
    public token nextToken() {
        // EOF
        if (lookAhead == -1) {
            return null;
        }
        // Ignorar espacios en blanco y actualizar línea/columna
        while (lookAhead != -1 && (lookAhead == ' ' || lookAhead == '\t'
                || lookAhead == '\r' || lookAhead == '\n')) {
            if (lookAhead == '\n') {
                line++;
                column = 0;
            }
            advance();
        }

        //inicializo las posiciones del token actual antes de consumir caracteres
        tokenStartLine   = line;
        tokenStartColumn = column;

        // el (char) hace que lookAhead se interprete como un carácter aunque venga un ascii o un -1 (EOF)
        char current = (char) lookAhead;

        switch (current) {

            //  Símbolos de un solo carácter que se reconocen inmediatamente
            case Symbols.OPEN_PAREN:    consume(); return makeToken(String.valueOf(Symbols.openParen),    flushBuffer());
            case Symbols.CLOSE_PAREN:   consume(); return makeToken(String.valueOf(Symbols.closeParen),   flushBuffer());
            case Symbols.OPEN_BRACE:    consume(); return makeToken(String.valueOf(Symbols.openBrace),    flushBuffer());
            case Symbols.CLOSE_BRACE:   consume(); return makeToken(String.valueOf(Symbols.closeBrace),   flushBuffer());
            case Symbols.OPEN_BRACKET:  consume(); return makeToken(String.valueOf(Symbols.openBracket),  flushBuffer());
            case Symbols.CLOSE_BRACKET: consume(); return makeToken(String.valueOf(Symbols.closeBracket), flushBuffer());
            case Symbols.SEMICOLON:     consume(); return makeToken(String.valueOf(Symbols.semicolon),     flushBuffer());
            case Symbols.COMMA:         consume(); return makeToken(String.valueOf(Symbols.comma),         flushBuffer());
            case Symbols.DOT:           consume(); return makeToken(String.valueOf(Symbols.dot),           flushBuffer());
            case Symbols.COLON:         consume(); return makeToken(String.valueOf(Symbols.colon),         flushBuffer());

            //simbolos que puden ser dobles

            //quote y double quote

            //operadores que pueden ser dobles o simples
            case Operators.PLUS:
                consume();
                if (lookAhead == Operators.PLUS) {          // ++
                    consume();
                    return makeToken(String.valueOf(Operators.incrementOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.sumOperator), flushBuffer());

            case Operators.MINUS:
                consume();
                if (lookAhead == Operators.MINUS) {         // --
                    consume();
                    return makeToken(String.valueOf(Operators.decrementOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.substractionOperator), flushBuffer());

            case Operators.STAR:
                consume();
                return makeToken(String.valueOf(Operators.multiplicationOperator), flushBuffer());

            // Pueden ser comentarios o operadores  de división
            case Operators.SLASH:
                //comentario de una sola línea
                consume();
                if (lookAhead == Operators.SLASH) {
                    // Ignorar el resto de la línea
                    while (lookAhead != -1 && lookAhead != '\n') advance();
                    return nextToken(); // el resultado de este nextoken es el siguiente token después del comentario
                }
                // comentario de varias líneas
                if (lookAhead == '*') {
                    advance(); // consume el '*'
                    while (lookAhead != -1) {
                        if (lookAhead == '*') {
                            advance();
                            if (lookAhead == '/') {
                                advance(); // consume el '/'
                                return nextToken(); // el resultado de este nextoken es el siguiente token después del comentario
                            }
                        } else {
                            advance();
                        }
                    }
                    throw new LexicalExeptions(
                            "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Comentario multilinea no cerrado |\n"
                                    + "se esperaba */ para cerrar el comentario ");
                }
                return makeToken(String.valueOf(Operators.divOperator), flushBuffer());


            case Operators.EQUAL:
                consume();
                if (lookAhead == Operators.EQUAL) {         // ==
                    consume();
                    return makeToken(String.valueOf(Operators.equalOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.assignmentOperator), flushBuffer());

            case Operators.BANG:
                consume();
                if (lookAhead == Operators.EQUAL) {         // !=
                    consume();
                    return makeToken(String.valueOf(Operators.notEqualOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.notOperator), flushBuffer());

            case Operators.GREATER:
                consume();
                if (lookAhead == Operators.EQUAL) {         // >=
                    consume();
                    return makeToken(String.valueOf(Operators.greaterThanOrEqualOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.greaterThanOperator), flushBuffer());

            case Operators.LESS:
                consume();
                if (lookAhead == Operators.EQUAL) {         // <=
                    consume();
                    return makeToken(String.valueOf(Operators.lessThanOrEqualOperator), flushBuffer());
                }
                return makeToken(String.valueOf(Operators.lessThanOperator), flushBuffer());

            case Operators.AMPERSAND:
                consume();
                if (lookAhead == Operators.AMPERSAND) {     // &&
                    consume();
                    return makeToken(String.valueOf(Operators.andOperator), flushBuffer());
                }
                throw new LexicalExeptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Se esperaba &&, se dio un & |\n"
                                + "no es un operador valido ");

            case Operators.PIPE:
                consume();
                if (lookAhead == Operators.PIPE) {          // ||
                    consume();
                    return makeToken(String.valueOf(Operators.orOperator), flushBuffer());
                }
                throw new LexicalExeptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Se esperaba ||, se dio |  |\n"
                                + "no es un operador valido ");


        }

        // esto es temporal hago un token vacio solo para que no explote con algo no reconocido aun
        consume(); // avanza para no quedar atrapado aca
        return makeToken("UNRECOGNIZED", String.valueOf(current));
    }}




