package org.compi.Syntactic;

/**
 * Excepción personalizada para errores sintácticos en el análisis de código.
 * @author Enzo Palau
 */

public class SyntacticExceptions extends RuntimeException {
    public SyntacticExceptions(String message) {
        super(message);
    }
}
