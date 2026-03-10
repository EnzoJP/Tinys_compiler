package org.compi.Lexical;

/**
 * Excepción personalizada para errores léxicos en el análisis de código.
 * @author Enzo Palau
 */
public class LexicalExeptions extends RuntimeException {
    public LexicalExeptions(String message) {
        super(message);
    }
}
