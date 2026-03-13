package org.compi.Lexical;

/**
 * Excepción personalizada para errores léxicos en el análisis de código.
 * @author Enzo Palau
 */
public class LexicalExceptions extends RuntimeException {
    public LexicalExceptions(String message) {
        super("ERROR LEXICO | \n" +
                "| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: | \n" + message);
    }
}
