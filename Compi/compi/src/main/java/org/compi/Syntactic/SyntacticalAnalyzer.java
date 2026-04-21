package org.compi.Syntactic;

import org.compi.Lexical.LexicalAnalyzer;
import org.compi.Lexical.token;
import java.io.File;
import java.util.List;

/**
 * Clase que representa el analizador Sintáctico
 * @author Enzo Palau
 * @author Luciana Puentes
 */
public class SyntacticalAnalyzer {

    private token tokenAct;
    private LexicalAnalyzer lexicalAnalyzer;

    public SyntacticalAnalyzer(File sourceFile) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(sourceFile);
    }

    /**
    * Metodo que se encarga de ver el no terminal que se esta analizando y comparar el token recibido con el token esperado
     * (que puede ser una lista, ya que puede tener varios SIGUIENTES)
     */
    public void noTerminal (token received , List<String> expected) {

        String receivedType = received.getType();

        for (String expectedToken : expected) {
            if (receivedType.equals(expectedToken)) {
                // avanzamos al siguiente token
                tokenAct = lexicalAnalyzer.nextToken();
                return;
            }
        }
    }

    /**
     * ⟨program⟩ ::=  ⟨Lista-Definiciones⟩ ⟨Start⟩
     */

    public void program() {
        tokenAct = lexicalAnalyzer.nextToken();
        listaDefiniciones();
        start();
        // debemos haber llegado al final del archivo
        if (tokenAct != null) {
            System.err.println("ERROR SINTACTICO Se esperaba el final del archivo.");
        }else {
            System.out.println("Análisis sintáctico completado sin errores.");
        }

    }

    /**
     * ⟨Start⟩ ::= pstart ⟨Bloque-Método⟩
     */

    public void start() {
        tokenAct = lexicalAnalyzer.nextToken();
        noTerminal(tokenAct, List.of("pstart"));
        bloqueMetodo();

    }

    /**
     * ⟨Lista-Definiciones⟩ ::= <Class> ⟨Lista-Definiciones⟩ | <Impl> ⟨Lista-Definiciones⟩ |  λ
     */

    public void listaDefiniciones() {

    }

    /**
     * ⟨Class⟩ ::=  pclass classID <ClassF>
     */

    public void Class() {

    }

    /**
     * <ClassF> :: = ⟨Herencia⟩ openBrace ⟨AtributoE⟩ closeBrace | openBrace ⟨AtributoE⟩ closeBrace
     */

    public void classF() {

    }

    /**
     * <AtributoE> :: = <Atributo> <AtributoE> | λ
     */

    public void atributoE() {

    }

    /**
     * ⟨Impl⟩ ::= pimpl classID openBrace ⟨MiembroE⟩ closeBrace
     */

    public void impl() {

    }

    /**
     * <MiembroE> ::= <Miembro> <MiembroE> | λ
     */

    public void miembroE() {

    }

    /**
     * ⟨Herencia⟩ ::= colon <Tipo>
     */

    public void herencia() {

    }

    /**
     * ⟨Miembro⟩ ::= <Metodo> | <Constructor>
     */

    public void miembro() {

    }

    /**
     * ⟨Constructor ⟩ ::= dot ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩
     */

    public void constructor() {

    }

    /** ⟨Atributo⟩ ::= ⟨Visibilidad⟩ ⟨Tipo⟩ ⟨Lista-Declaración-Variables⟩ semicolon  | ⟨Tipo⟩ ⟨Lista-Declaración-Variables⟩ semicolon
     *
     */

    public void atributo() {

    }

    /**
     * ⟨Método⟩ ::= ⟨Forma-Método⟩ pfn <MetodoF> | pfn<MetodoF>
     */

    public void metodo() {

    }

    /**
     * <MetodoF> ::= ⟨Tipo-Método⟩ objectID ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩ | objectID  ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩
     */

    public void metodoF() {

    }

    /**
     * ⟨Visibilidad⟩ ::= ppub
     *
     */

    public void visibilidad() {

    }

    /**
     * ⟨Forma-Método⟩::= pst
     */

    public void formaMetodo() {

    }

    /**
     * ⟨Bloque-Método⟩::= openBrace <Decl-Var-LocalesE> <SentenciaE> closeBrace
     */

    public void bloqueMetodo() {

    }

    /**
     * <Decl-Var-LocalesE> ::=  <Decl-Var-Locales> <Decl-Var-LocalesE> | λ
     */

    public void declVarLocalesE() {

    }

    /**
     * <SentenciaE> ::=  <Sentencia> <SentenciaE>  | λ
     */

    public void sentenciaE() {

    }

    /**
     * ⟨Decl-Var-Locales⟩::= ⟨Tipo⟩ ⟨Lista-Declaración-Variables⟩ semicolon
     */

    public void declVarLocales() {

    }

    /**
     * ⟨Lista-Declaracion-Variables⟩::= objectID ⟨Lista-Declaracion-VariablesF⟩
     *
     */

    public void listaDeclaracionVariables() {

    }

    /**
     * ⟨Lista-Declaracion-VariablesF⟩::= comma ⟨Lista-Declaracion-Variables⟩ | λ
     */

    public void listaDeclaracionVariablesF() {

    }

    /**
     * ⟨Argumentos-Formales⟩::= openParen ⟨Argumentos-FormalesF⟩
     */

    public void argumentosFormales() {

    }

    /**
     * ⟨Argumentos-FormalesF⟩::= ⟨Lista-Argumentos-Formales⟩ closeParen | closeParen
     */

    public void argumentosFormalesF() {

    }

    /**
     * ⟨Lista-Argumentos-Formales⟩ ::= ⟨Argumento-Formal⟩⟨Lista-Argumentos-FormalesF⟩
     */

    public void listaArgumentosFormales() {

    }

    /**
     * ⟨Lista-Argumentos-FormalesF⟩ ::= comma ⟨Lista-Argumentos-Formales⟩ | λ
     */

    public void listaArgumentosFormalesF() {

    }

    /**
     * ⟨Argumento-Formal⟩ ::= ⟨Tipo⟩ objectID
     */

    public void argumentoFormal() {

    }

    /**
     * ⟨Tipo-Método⟩ ::=  ⟨Tipo⟩ | pvoid
     */

    public void tipoMetodo() {

    }

    /**
     * ⟨Tipo⟩ ::= ⟨Tipo-Primitivo⟩ | ⟨Tipo-Referencia⟩ | ⟨Tipo-Arreglo⟩
     */

    public  void tipo() {

    }

    /** ⟨Tipo-Primitivo⟩ ::= Str | Int | Bool
     *
     */
    public void tipoPrimitivo() {

    }

    /** ⟨Tipo-Referencia⟩ ::= idclass
     *
     */

    public void tipoReferencia() {

    }

    /**
     * ⟨Tipo-Arreglo⟩ ::= parray ⟨Tipo-Primitivo⟩
     */

    public void tipoArreglo() {

    }
    /**
     * ⟨Sentencia⟩ ::=  semicolon | ⟨Asignacion⟩ semicolon | ⟨Sentencia-Simple⟩ semicolon | pif openParen ⟨Expresion⟩ closeParen ⟨Sentencia⟩ <SentenciaFE> |
     * pwhile openParen ⟨Expresion⟩ closeParen ⟨Sentencia⟩ | pfor openParen ⟨Tipo-Primitivo⟩ objectID pin objectID closeParen ⟨Sentencia⟩ | <Bloque> | pret <SentenciaFR>
     */

    public void sentencia() {

    }

     /**
     * <SentenciaFE> ::= pelse ⟨Sentencia⟩ | λ
     */

    public void sentenciaFE() {

    }

    /**
     * <SentenciaFR> ::=  ⟨Expresion⟩ semicolon | semicolon
     */

    public void sentenciaFR(){

    }

    /**
     * ⟨Bloque⟩ ::=  openBrace <SentenciaE> closeBrace
     */

    public void bloque() {

    }

    /**
     * ⟨Asignacion⟩ ::= ⟨AccesoVar-Simple⟩ equalOperator ⟨Expresion⟩ | ⟨AccesoSelf-Simple⟩ equalOperator ⟨Expresion⟩
     */

    public void asignacion() {

    }

    /**
     * ⟨AccesoVar-Simple⟩ ::= id ⟨AccesoVar-SimpleF⟩
     */

    public void accesoVarSimple() {

    }

    /**
     * ⟨AccesoVar-SimpleF⟩ ::= ⟨Encadenado-SimpleE⟩  | openBracket ⟨Expresion⟩ closeBracket
     */

    public void accesoVarSimpleF() {

    }

    /**
     * ⟨Encadenado-SimpleE⟩ ::= ⟨Encadenado-Simple⟩ ⟨Encadenado-SimpleE⟩ | λ
     */

    public void encadenadoSimpleE() {

    }

    /**
     * ⟨AccesoSelf-Simple⟩ ::=  pself ⟨Encadenado-SimpleE⟩
     */

    public void accesoSelfSimple() {

    }

    /**
     * ⟨Encadenado-Simple⟩::= dot id
     */

    public void encadenadoSimple() {

    }

    /**
     * ⟨Sentencia-Simple⟩ ::= openParen ⟨Expresion⟩ closeParen
     */

    public void sentenciaSimple() {

    }

    /**
     *  ⟨Expresion⟩ ::= ⟨ExpOr ⟩
     */

    public void expresion() {

    }

    /**
     * ⟨ExpOr⟩  ::= ⟨ExpAnd⟩ ⟨EO⟩
     */

    public void expOr() {
    }

    /**
     * ⟨EO⟩  ::= orOperator ⟨ExpAnd⟩ ⟨EO⟩ | λ
     */

    public void EO() {

    }

    /**
     * ⟨ExpAnd⟩  ::= ⟨ExpIgual⟩ ⟨EA⟩
     */

    public void expAnd() {

    }

    /**
     * ⟨EA⟩  ::= andOperator ⟨ExpIgual⟩ ⟨EA⟩ | λ
     */

    public void EA() {

    }

    /**
     * ⟨ExpIgual⟩  ::= ⟨ExpCompuesta⟩ ⟨EI⟩
     */

    public void expIgual() {

    }

    /**
     * ⟨EI⟩  ::= OpIgual ⟨ExpCompuesta⟩ | λ
     */

    public void EI() {

    }

    /**
     * ⟨ExpCompuesta⟩ ::= ⟨ExpAd⟩ ⟨ExpCompuestaF⟩
     */

    public void expCompuesta() {

    }

    /**
     * ⟨ExpCompuestaF⟩ ::= ⟨OpCompuesto⟩ ⟨ExpAd⟩  | λ
     */

    public void expCompuestaF() {

    }

    /**
     * ⟨ExpAd⟩  ::= ⟨ExpMul⟩ ⟨EAD⟩
     */

    public void expAd() {

    }

    /**
     * ⟨EAD⟩  ::= OpAd ⟨ExpMul⟩ ⟨EAD⟩ | λ
     */

    public void EAD() {

    }

    /**
     * ⟨ExpMul⟩  ::= ⟨ExpUn⟩ ⟨EM⟩
     */

    public void ExpMul() {

    }

    /**
     * ⟨EM⟩  ::= OpMul ⟨ExpUn⟩ ⟨EM⟩ | λ
     */

    public void EM() {

    }

    /**
     * ⟨ExpUn⟩ ::= ⟨OpUnario⟩ ⟨ExpUn⟩ | openParen ⟨ExpUnF⟩ | ⟨Literal⟩  | ⟨Primario⟩ ⟨OperandoF⟩
     */

    public void expUn() {

    }

    /**
     * ⟨ExpUnF⟩::= Int closeParen |  ⟨OperandoF⟩
     */

    public void ExpUnF() {

    }

    /**
     * ⟨OpIgual⟩ ::= equalOperator | notEqualOperator
     */

    public void opIgual() {

    }

    /**
     * ⟨OpCompuesto⟩ ::= greaterThanOperator | lessThanOperator | greaterThanOrEqualOperator | lessThanOrEqualOperator
     */

    public void opCompuesto() {
    }

    /**
     * ⟨OpAd⟩ ::=  sumOperator | substractionOperator
     */

    public void opAd() {

    }

    /**
     * ⟨OpUnario⟩ ::=  sumOperator | substractionOperator | notOperator | incrementOperator | decrementOperator
     */

    public void opUnario() {

    }

    /**
     * <OpMul> ::=  multiplicationOperator | divOperator
      */

    public void opMul() {

    }

    /**
     * ⟨OperandoF⟩: := ⟨Encadenado⟩  | λ
     */

    public void operandoF() {

    }

    /**
     * ⟨Literal⟩ := nil | ptrue | pfalse | const_int | const_str
      */

    public void literal() {

    }

    /**
     * ⟨Primario⟩:=  ⟨AccesoSelf ⟩ | id PrimarioF | ⟨Llamada-Método-Estático⟩ | ⟨Llamada-Conclassor ⟩
     */

    public void primario() {

    }

    /**
     * ⟨PrimarioF⟩:= ⟨AccesoVarF2 ⟩ | ⟨Argumentos-Actuales⟩ ⟨Llamada-MétodoF⟩
     */

    public void primarioF() {

    }

    /**
     * ⟨ExpresionParentizada⟩::= openParen  ⟨Expresion⟩ closeParen ⟨ExpresionParentizadaF⟩
     */

    public void expresionParentizada() {

    }

    /**
     * ⟨ExpresionParentizadaF⟩::= ⟨Encadenado⟩  | λ
     */

    public void expresionParentizadaF() {

    }

     /**
     * ⟨AccesoSelf ⟩::= self ⟨AccesoSelfF ⟩
      */

    public void accesoSelf() {

    }

    /**
     * ⟨AccesoSelfF ⟩::= ⟨Encadenado⟩  | λ
     */

    public void accesoSelfF() {

    }

    /**
     * ⟨AccesoVarF2 ⟩::=  openBracket ⟨Expresión⟩ closeBracket ⟨AccesoVarF3 ⟩ | ⟨Encadenado⟩  | λ
     */

    public void accesoVarF2() {
    }

    /**
     * ⟨AccesoVarF3 ⟩ ::= ⟨Encadenado⟩  | λ
     */

    public void accesoVarF3() {

    }

    /**
     * ⟨Llamada-MétodoF⟩::= ⟨Encadenado⟩  | λ
     */

    public void llamadaMetodoF() {

    }

    /**
     * ⟨Llamada-Método-Estático⟩::= idclass dot ⟨Llamada-Método⟩ ⟨Llamada-Método-EstáticoF⟩
     */

    public void llamadaMetodoEstatico() {

    }

    /**
     * ⟨Llamada-Método-EstáticoF⟩::= ⟨Encadenado⟩ | λ
     */

    public void llamadaMetodoEstaticoF() {

    }

    /**
     * ⟨Llamada-Conclassor ⟩::= new⟨Llamada-ConclassorF ⟩
     */

    public void llamadaConclassor() {

    }

    /**
     * ⟨Llamada-ConclassorF ⟩::=  idclass ⟨Argumentos-Actuales⟩⟨Llamada-ConclassorF2 ⟩ | ⟨Tipo-Primitivo⟩ openBracket ⟨Expresion⟩ closeBracket
     */

    public void llamadaConclassorF() {
    }

    /**
     * ⟨Llamada-ConclassorF2 ⟩::= ⟨Encadenado⟩  | λ
      */

    public void llamadaConclassorF2() {

    }

    /**
     * ⟨Argumentos-Actuales⟩ ::= openParen ⟨Argumentos-ActualesF⟩
     */

    public void argumentosActuales() {

    }

    /**
     * ⟨Argumentos-ActualesF⟩ ::= ⟨Lista-Expresiones⟩  closeParen | closeParen
     */

    public void argumentosActualesF() {

    }

    /**
     * ⟨Lista-Expresiones⟩::= ⟨Expresión⟩  ⟨Lista-ExpresionesF⟩
     */

    public void listaExpresiones() {

    }

    /**
     * ⟨Lista-ExpresionesF⟩::= comma ⟨Lista-Expresiones⟩ | λ
     */

    public void listaExpresionesF() {

    }

    /**
     * ⟨Encadenado⟩::= dot⟨EncadenadoF⟩
     */

    public void encadenado() {

    }

    /**
     * ⟨EncadenadoF⟩::= id ⟨EncadenadoF2⟩
     */

    public void encadenadoF() {

    }

    /**
     * ⟨EncadenadoF2⟩::= ⟨Argumentos-Actuales⟩⟨Llamada-Método-EncadenadoF⟩ | ⟨Acceso-Variable-EncadenadoF⟩
     */

    public void encadenadoF2() {

    }

    /**
     * ⟨Llamada-Método-EncadenadoF⟩::= ⟨Encadenado⟩  | λ
     */

    public void llamadaMetodoEncadenadoF() {

    }

    /**
     * ⟨Acceso-Variable-EncadenadoF⟩::= openBracket ⟨Expresion⟩ closeBracket ⟨Encadenado⟩  | ⟨Encadenado⟩  | λ
      */

    public void accesoVariableEncadenadoF() {

    }











}
