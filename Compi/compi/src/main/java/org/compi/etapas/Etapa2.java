package org.compi.etapas;

import org.compi.Syntactic.SyntacticalAnalyzer;

import java.io.File;

public class Etapa2 {

    /**
     * Clase principal para la etapa 2 del proyecto de compiladores.
     * Esta clase se encarga de:
     * 1. Validar los argumentos de entrada (archivo fuente).
     * 2. imprimir los errores sintácticos.
     * @author Enzo Palau
     */

    public static void main(String[] args) {
        // Validar argumentos
        if (args.length != 1) {
            System.err.println("Use: java -jar etapa2.jar <ARCHIVO_FUENTE>");
            System.exit(1);
        }

        File sourceFile = new File(args[0]);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.err.println("ERROR: No se puede abrir el archivo fuente: " + args[0]);
            System.exit(1);
        }


        SyntacticalAnalyzer syntacticAnalyzer = new SyntacticalAnalyzer(sourceFile);
        syntacticAnalyzer.program();


}
}
