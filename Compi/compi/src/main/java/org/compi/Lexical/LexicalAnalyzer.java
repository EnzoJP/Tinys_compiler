package org.compi.Lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Clase que representa el analizador léxico, encargado de convertir el código fuente en tokens.
 * Lee el archivo fuente carácter a carácter usando un BufferedReader.
 * Usa una LinkedList como buffer del lexema actual y un lookAhead para el siguiente carácter.
 * @author Enzo Palau
 * @author Luciana Puentes
 */
public class LexicalAnalyzer {

    private final File sourceFile;
    private static final String[] KEYWORDS = {
            "class", "impl", "else", "if", "false", "true", "while", "ret",
            "nil", "new", "fn", "st", "pub", "self", "div", "for", "in","start", "Str", "void", "Int", "Bool", "Array"
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
            throw new LexicalExceptions("ERROR: No se puede abrir el archivo fuente: " + sourceFile.getName());
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

    /** Lee un entero o lanza excepción si es un identificador inválido que empieza con dígito. */
    private token readInteger() {
        //consume dígitos mientras los haya
        while (lookAhead != -1 && Character.isDigit((char) lookAhead)) {
            consume();
        }
        // si inmediatamente sigue una letra es un identificador inválido porque no puede empezar con un dígito un identificador
        if (lookAhead != -1 && (Character.isLetter((char) lookAhead) || lookAhead == '_')) {
            consume();
            throw new LexicalExceptions(
                    "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: se dio num + chars, se esperaba o un entero o un identificador |\n"
                            + "| No es identificador valido |");
        }
        return makeToken(String.valueOf(Literals.IntegerLiteral), flushBuffer());
    }

    /** Lee un literal de cadena entre comillas dobles. */
    private token readString() {
        advance(); // consume "
        int len =0;
        while (lookAhead != -1 && lookAhead != '\n' && lookAhead != '\0') {
            len=len+1;
            if (len>1024) {
                throw new LexicalExceptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | STRING DE LONGITUD MAYOR A 1024|"
                );

            }
            if (lookAhead == '\\') {
                advance();

                if (lookAhead == '\\') {        // \\ -> \
                    buffer.add('\\');
                    advance();
                    continue;
                }

                if (lookAhead == '"') {         // \" -> "
                    buffer.add('"');
                    advance();
                    continue;
                }

                if (lookAhead == 'n') {         // \n -> newline
                    buffer.add('\\');
                    buffer.add('n');
                    advance();
                    continue;
                }

                if (lookAhead == 't') {         // \t -> tab
                    buffer.add('\\');
                    buffer.add('t');
                    advance();
                    continue;
                }

                throw new LexicalExceptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | ESCAPE INVALIDO EN STRING |"
                );
            }
            if (lookAhead == '"') {
                break; // cierre real
            }

            consume();
        }
        if (lookAhead != Symbols.DOUBLE_QUOTE) {
            throw new LexicalExceptions(
                    "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | CADENA NO CERRADA |"
            );
        }
        advance(); // consume la " final
        return makeToken(String.valueOf(Literals.StringLiteral), flushBuffer());
    }

    /** Lee un identificador o palabra reservada. */
    private token readIdentifierOrKeyword() {
        //me aseguro al menos de que el primer carácter sea letra o _ para ser mas seguro
        char charCurrent = (char) lookAhead;

        if (lookAhead == -1 || lookAhead == '\n' || lookAhead == '\0'
                || (!Character.isLetter((char) lookAhead) && lookAhead != '_')) {
            consume();
            charCurrent = (char) lookAhead;
            throw new LexicalExceptions(
                    "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: se esperaba un identificador o palabra reservada que empiece con letra o _ |\n"
                            + " no es un identificador valido |");
        }

        while (lookAhead != -1 && lookAhead != '\n' && lookAhead != '\0'
                && (Character.isLetterOrDigit((char) lookAhead) || lookAhead == '_')) {
            charCurrent = (char) lookAhead;
            consume();
        }

        // caso de que el identificador tenga un dígito raro que no sea letra o dígito o _
        if (!Character.isLetterOrDigit((charCurrent)) && charCurrent != '_') {
            consume();
            System.out.println("excepcion char");
            throw new LexicalExceptions(
                    "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: se esperaba un identificador o palabra reservada que contenga solo letras, dígitos o _ |\n"
                            + " no es un identificador valido |" + "se encontró un carácter no reconocido: '" + (char) lookAhead + "' |");
        }

        String lexeme = flushBuffer();
        if (isKeyword(lexeme)) {

            switch (lexeme){
                case "class": return makeToken(String.valueOf(Keywords.pclass), lexeme);
                case "impl": return makeToken(String.valueOf(Keywords.pimpl), lexeme);
                case "else": return makeToken(String.valueOf(Keywords.pelse), lexeme);
                case "if": return makeToken(String.valueOf(Keywords.pif), lexeme);
                case "false": return makeToken(String.valueOf(Keywords.pfalse), lexeme);
                case "true": return makeToken(String.valueOf(Keywords.ptrue), lexeme);
                case "while": return makeToken(String.valueOf(Keywords.pwhile), lexeme);
                case "ret": return makeToken(String.valueOf(Keywords.pret), lexeme);
                case "nil": return makeToken(String.valueOf(Keywords.pnil), lexeme);
                case "new": return makeToken(String.valueOf(Keywords.pnew), lexeme);
                case "fn": return makeToken(String.valueOf(Keywords.pfn), lexeme);
                case "st": return makeToken(String.valueOf(Keywords.pst), lexeme);
                case "pub": return makeToken(String.valueOf(Keywords.ppub), lexeme);
                case "self": return makeToken(String.valueOf(Keywords.pself), lexeme);
                case "div": return makeToken(String.valueOf(Keywords.pdiv), lexeme);
                case "for": return makeToken(String.valueOf(Keywords.pfor), lexeme);
                case "in": return makeToken(String.valueOf(Keywords.pin), lexeme);
                case "start": return makeToken(String.valueOf(Keywords.pstart), lexeme);
                case "Str": return makeToken(String.valueOf(Keywords.pstr), lexeme);
                case "void": return makeToken(String.valueOf(Keywords.pvoid), lexeme);
                case "Int": return makeToken(String.valueOf(Keywords.pint), lexeme);
                case "Bool": return makeToken(String.valueOf(Keywords.pbool), lexeme);
                case "Array": return makeToken(String.valueOf(Keywords.parray), lexeme);
            }

        }

        // identificador de clase que empieza con mayúscula
        if (Character.isUpperCase(lexeme.charAt(0))) {

            return makeToken(String.valueOf(Identificators.ClassID), lexeme);
        }
        return makeToken(String.valueOf(Identificators.ObjectID), lexeme);
    }


    /**
     * Retorna el siguiente token del archivo fuente.
     * retorna null si se llega al final del archivo.
     * Lanza LexicalExeptions ante un error léxico.
     */
    public token nextToken() throws LexicalExceptions {
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
                        while (lookAhead != -1 && lookAhead != '\n') {
                            column++;
                            if (lookAhead > 127) {
                                throw new LexicalExceptions(
                                        "| LINEA " + tokenStartLine + " (COLUMNA " + column +
                                                ") | DESCRIPCION: Se encontró un carácter no reconocido: '" + (char) lookAhead + "' |\n"
                                                + "no es un token valido ");
                            }

                            advance();
                        }

                        flushBuffer();
                        return nextToken();
                }
                // comentario de varias líneas
                if (lookAhead == '*') {
                    advance(); // consume el '*'
                    while (lookAhead != -1 ) {
                        column++;
                        if (lookAhead=='\n'){
                            line++;
                            column=0;
                        }
                        // validar caracteres permitidos
                        if (lookAhead>127) {
                            throw new LexicalExceptions(
                                    "| LINEA " + line + " (COLUMNA " + column + ") | DESCRIPCION: Se encontró un carácter no reconocido: '" + current + "' |\n"
                                            + "no es un token valido ");
                        }
                        if (lookAhead == '*') {
                            advance();
                            if (lookAhead == '/') {
                                advance(); // consume el '/'
                                flushBuffer();
                                return nextToken(); // el resultado de este nextoken es el siguiente token después del comentario
                            }

                        } else {
                            advance();
                        }
                    }
                    throw new LexicalExceptions(
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
                throw new LexicalExceptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Se esperaba &&, se dio un & |\n"
                                + "no es un operador valido ");

            case Operators.PIPE:
                consume();
                if (lookAhead == Operators.PIPE) {          // ||
                    consume();
                    return makeToken(String.valueOf(Operators.orOperator), flushBuffer());
                }
                throw new LexicalExceptions(
                        "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Se esperaba ||, se dio |  |\n"
                                + "no es un operador valido ");

            // literales enteros
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return readInteger();

            // strings o char
            case Symbols.DOUBLE_QUOTE:
                return readString();


            case '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y','Z':

                return readIdentifierOrKeyword();
        }

        // si no se reconoce el carácter, lanza excepción, en teoria no debería pasar porque el switch cubre todos los casos posibles de caracteres válidos
        consume();
        throw new LexicalExceptions(
                "| LINEA " + tokenStartLine + " (COLUMNA " + tokenStartColumn + ") | DESCRIPCION: Se encontró un carácter no reconocido: '" + current + "' |\n"
                        + "no es un token valido ");

    }
}




