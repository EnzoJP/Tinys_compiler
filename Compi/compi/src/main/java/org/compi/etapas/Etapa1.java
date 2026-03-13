package org.compi.etapas;

import org.compi.Lexical.LexicalAnalyzer;
import org.compi.Lexical.LexicalExceptions;
import org.compi.Lexical.token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal para la etapa 1 del proyecto de compiladores.
 * Esta clase se encarga de:
 * 1. Validar los argumentos de entrada (archivo fuente y opcionalmente archivo de salida).
 * 2. imprimir el resultado de analizar el archivo fuente léxicamente, mostrando los tokens encontrados o los errores léxicos.
 * @author Enzo Palau
 */
public class Etapa1 {

    public static void main(String[] args) {

        // Validar argumentos
        if (args.length < 1) {
            System.err.println("Use: java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]");
            System.exit(1);
        }

        if (args.length > 2) {
            System.err.println("ERROR: Demasiados argumentos. Use: java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]");
            System.exit(1);
        }

        File sourceFile = new File(args[0]);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.err.println("ERROR: No se puede abrir el archivo fuente: " + args[0]);
            System.exit(1);
        }

        //  salida
        PrintWriter out;
        boolean toFile = args.length == 2;
        try {
            if (toFile) {
                out = new PrintWriter(new FileWriter(args[1]));
            } else {
                out = new PrintWriter(System.out, true);
            }
        } catch (IOException e) {
            System.err.println("ERROR: No se puede abrir el archivo de salida: " + args[1]);
            System.exit(1);
            return;
        }

        //  Análisis léxico para la etapa 1
        LexicalAnalyzer lexer = new LexicalAnalyzer(sourceFile);

        try {
            List<token> tokens = new ArrayList<>();

            token t;
            while ((t = lexer.nextToken()) != null) {
                tokens.add(t);
            }

            // éxito
            out.println("CORRECTO: ANALISIS LEXICO");
            out.println("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |");


            for (token tk : tokens) {
                out.printf("| %s | %s | LINEA %d (COLUMNA %d) |%n",
                        tk.getType(),
                        tk.getLexeme(),
                        tk.getLine(),
                        tk.getColumn());
            }

        } catch (LexicalExceptions e) {
            out.println(e.getMessage());

        } finally {
            if (toFile) {
                out.close();
            } else {
                out.flush();
            }
        }
    }
}


