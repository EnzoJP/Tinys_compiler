package org.compi.Lexical;
import org.compi.etapas.Etapa1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class LexicalTest {
    private static final String TEST_DIR = "src/tc/LexicalTests/";

    @Test

    // Test de archivo vacío, debe dar análisis correcto
    public void testEmpty() {
        Etapa1.main(new String[]{ TEST_DIR + "testEmpty.s" });
    }
    @Test
    public void testPosition() {
        PrintStream originalOut = System.out;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Etapa1.main(new String[]{ TEST_DIR + "testPosicion.s" });

        System.setOut(originalOut);

        String salida = baos.toString();
        originalOut.println(salida);

        assertTrue(salida.contains("LINEA 1 (COLUMNA 1)"),  "aaa  → L1 C1");
        assertTrue(salida.contains("LINEA 3 (COLUMNA 1)"),  "b    → L3 C1");
        assertTrue(salida.contains("LINEA 3 (COLUMNA 3)"),  "=    → L3 C3");
        assertTrue(salida.contains("LINEA 6 (COLUMNA 1)"),  ".    → L6 C1");
        assertTrue(salida.contains("LINEA 6 (COLUMNA 4)"),  "hola → L6 C4");
    }
    // TEST DE CARACTERES INVALIDOS
    @Test
    //Test para chequear caracteres inválidos
    public void testInvalid() {
        File file = new File(TEST_DIR + "testInvalid.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);
        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null); // recorre hasta encontrar el error
                }
        );

        System.out.println(ex.getMessage());
    }
    //Test para chequear declaración inválida en id
    @Test
    public void testInvalidID() {
        File file = new File(TEST_DIR + "testId.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null); // recorre hasta encontrar el error
                }
        );

        System.out.println(ex.getMessage());
    }



}