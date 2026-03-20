package org.compi.Lexical;
import org.compi.etapas.Etapa1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class LexicalTest {
    private static final String TEST_DIR = "src/tc/LexicalTests/";
    private PrintStream originalOut;
    private ByteArrayOutputStream baos;
    private void startCapture() {
        originalOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
    }

    private String stopCapture() {
        System.setOut(originalOut);
        String salida = baos.toString();
        originalOut.println(salida);
        return salida;
    }
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
                    while (lexer.nextToken() != null);
                }
        );

        System.out.println(ex.getMessage());
    }
    //Test para verficar comentarios multilínea sin cerrar
    @Test
    public void testComment(){
        File file = new File(TEST_DIR + "testComment.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null);
                }
        );
        System.out.println(ex.getMessage());
    }
    //Test para verificar longitud de strings
    @Test
    public void testLenString(){
        File file = new File(TEST_DIR + "testLenString.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null);
                }
        );
        System.out.println(ex.getMessage());
    }
    //Test para verificar strings sin cerrar
    @Test
    public void testString(){
        File file = new File(TEST_DIR + "testString.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null);
                }
        );
        System.out.println(ex.getMessage());
    }
    //Test para verificar que a & solo le siga un & sino es inválido
    @Test
    public void testAnd(){
        File file = new File(TEST_DIR + "testAnd.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null);
                }
        );
        System.out.println(ex.getMessage());
    }
    //Test para verificar que las palabras clave se lean correctamente
    @Test
    public void testKeywords() {
        startCapture();
        Etapa1.main(new String[]{ TEST_DIR + "testKeywords.s" });
        String salida = stopCapture();

        assertTrue(salida.contains("CORRECTO: ANALISIS LEXICO"));

        // Verifica cada keyword con su tipo y lexema
        String[][] expected = {
                { String.valueOf(Keywords.pclass),  "class" },
                { String.valueOf(Keywords.pimpl),   "impl"  },
                { String.valueOf(Keywords.pelse),   "else"  },
                { String.valueOf(Keywords.pif),     "if"    },
                { String.valueOf(Keywords.pfalse),  "false" },
                { String.valueOf(Keywords.ptrue),   "true"  },
                { String.valueOf(Keywords.pwhile),  "while" },
                { String.valueOf(Keywords.pret),    "ret"   },
                { String.valueOf(Keywords.pnil),    "nil"   },
                { String.valueOf(Keywords.pnew),    "new"   },
                { String.valueOf(Keywords.pfn),     "fn"    },
                { String.valueOf(Keywords.pst),     "st"    },
                { String.valueOf(Keywords.ppub),    "pub"   },
                { String.valueOf(Keywords.pself),   "self"  },
                { String.valueOf(Keywords.pdiv),    "div"   },
                { String.valueOf(Keywords.pfor),    "for"   },
                { String.valueOf(Keywords.pin),     "in"    },
                { String.valueOf(Keywords.pstart),  "start" },
        };

        //  | TIPO | LEXEMA | LINEA X (COLUMNA Y) |
        for (String[] pair : expected) {
            String lineaEsperada = "| " + pair[0] + " | " + pair[1] + " |";
            assertTrue(salida.contains(lineaEsperada),
                    "No se encontró: " + lineaEsperada);
        }
    }
    //Test para verificar que no haya un escape en un string
    @Test
    public void testEscape(){
        File file = new File(TEST_DIR + "testEscape.s");
        LexicalAnalyzer lexer = new LexicalAnalyzer(file);

        LexicalExceptions ex = assertThrows(
                LexicalExceptions.class,
                () -> {
                    while (lexer.nextToken() != null);
                }
        );
        System.out.println(ex.getMessage());
    }


}