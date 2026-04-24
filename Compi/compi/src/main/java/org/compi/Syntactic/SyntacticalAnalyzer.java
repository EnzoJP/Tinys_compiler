package org.compi.Syntactic;

import org.compi.Lexical.LexicalAnalyzer;
import org.compi.Lexical.token;
import java.io.File;
import java.util.List;
import org.compi.Syntactic.SyntacticExceptions;

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
    public void siguienteTerminal (token received , List<String> expected) throws SyntacticExceptions {

        String receivedType = received.getType();

        for (String expectedToken : expected) {
            if (receivedType.equals(expectedToken)) {
                // avanzamos al siguiente token
                tokenAct = lexicalAnalyzer.nextToken();
                return;
            }
        }
        // no se encontró el token esperado
        throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba uno de los siguientes tokens: " + expected + ". Se encontró '" + received.getLexeme() + "' en la línea " + received.getLine() + ", columna " + received.getColumn());

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
            //llamar semantico
        }

    }

    /**
     * ⟨Start⟩ ::= pstart ⟨Bloque-Método⟩
     */

    public void start() {
        siguienteTerminal(tokenAct, List.of("pstart"));
        bloqueMetodo();
    }

    /**
     * ⟨Lista-Definiciones⟩ ::= <Class> ⟨Lista-Definiciones⟩ | <Impl> ⟨Lista-Definiciones⟩ |  λ
     */

    public void listaDefiniciones() throws SyntacticExceptions  {
        if (tokenAct.getType().equals("pclass")) {
            Class();
            listaDefiniciones();
        } else if (tokenAct.getType().equals("pimpl")) {
            impl();
            listaDefiniciones();
        } else if (tokenAct.getType().equals("pstart")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'class', 'impl' o 'start'. Se encontro " + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * ⟨Class⟩ ::=  pclass classID <ClassF>
     */

    public void Class() {
        siguienteTerminal(tokenAct,List.of("pclass"));
        siguienteTerminal(tokenAct,List.of("classID");
        classF();
    }

    /**
     * <ClassF> :: = ⟨Herencia⟩ openBrace ⟨AtributoE⟩ closeBrace | openBrace ⟨AtributoE⟩ closeBrace
     */

    public void classF() {
        //caso herencia
        if (tokenAct.getType().equals("colon")){
            herencia();
            siguienteTerminal(tokenAct,List.of("openBrace"));
            atributoE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));
        } else if (tokenAct.getType().equals("openBrace")) {
            //caso {
            atributoE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));

        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ':' o '{'. Se encontro " + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <AtributoE> :: = <Atributo> <AtributoE> | λ
     */

    public void atributoE() {
        List<String> primeros = List.of("ppub", "Str", "Bool", "Int", "ClassID", "parray");
        if(primeros.contains(tokenAct.getType())){
            atributo();
            atributoE();
        } else if (tokenAct.getType().equals("closeBrace")) {
            //caso lambda
            return;
            
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'pub', 'Str', 'Bool', 'Int' , 'ClassID', 'Array' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * ⟨Impl⟩ ::= pimpl ClassID openBrace ⟨MiembroE⟩ closeBrace
     */

    public void impl() {
        siguienteTerminal(tokenAct,List.of("pimpl"));
        siguienteTerminal(tokenAct,List.of("ClassID"));
        siguienteTerminal(tokenAct,List.of("openBrace"));
        miembroE();
        siguienteTerminal(tokenAct,List.of("closeBrace"));
    }

    /**
     * <MiembroE> ::= <Miembro> <MiembroE> | λ
     */

    public void miembroE() {
        List<String> primeros = List.of("pst","pfn","dot");
        if(primeros.contains(tokenAct.getType())){
            miembro();
            miembroE();
        }else-if(tokenAct.getType().equals("closeBrace")){
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'st', 'fn', '.' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * ⟨Herencia⟩ ::= colon <Tipo>
     */

    public void herencia() {
        siguienteTerminal(tokenAct,List.of("colon"));
        tipo();
    }

    /**
     * ⟨Miembro⟩ ::= <Metodo> | <Constructor>
     */

    public void miembro() {
        if(tokenAct.getType().equals("pst") || tokenAct.getType().equals("pfn")){
            metodo();
        } else if (tokenAct.getType().equals("dot")) {
            constructor();
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'st', 'fn', '.' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * ⟨Constructor ⟩ ::= dot ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩
     */

    public void constructor() {
        siguienteTerminal(tokenAct,List.of("dot");
        argumentosFormales();
        bloqueMetodo();
    }

    /** ⟨Atributo⟩ ::= ⟨Visibilidad⟩ ⟨Tipo⟩ ⟨Lista-Declaración-Variables⟩ semicolon  | ⟨Tipo⟩ ⟨Lista-Declaración-Variables⟩ semicolon
     *
     */

    public void atributo() {
        List<String> primerosTipo=List.of("Str", "Bool", "Int", "ClassID", "parray");
        if(tokenAct.getType().equals("ppub")){
            visibilidad();
            tipo();
            listaDeclaracionVariables();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else-if(primerosTipo.contains(tokenAct.getType())){
            tipo();
            listaDeclaracionVariables();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'pub', 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * ⟨Método⟩ ::= ⟨Forma-Método⟩ pfn <MetodoF> | pfn<MetodoF>
     */

    public void metodo() {
        List<String> primerosFormaMetodo= List.of("Str","Bool" ,"Int","ClassID" ,"parray", "pvoid");
       if (primerosFormaMetodo.contains(tokenAct.getType())) {
           formaMetodo();
           siguienteTerminal(tokenAct,List.of("pfn"));
           metodoF();
        }else-if(tokenAct.getType().equals("pfn")){
           siguienteTerminal(tokenAct,List.of("pfn"));
           metodoF();}
        else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' ,'void', 'fn' o 'Array'. Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <MetodoF> ::= ⟨Tipo-Método⟩ objectID ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩ | objectID  ⟨Argumentos-Formales⟩ ⟨Bloque-Método⟩
     */

    public void metodoF() {
        List<String> primerosTipoMetodo =List.of("Str","Bool" ,"Int","ClassID" ,"parray", "pvoid");
        if(primerosTipoMetodo.contains(tokenAct.getType())){
            tipoMetodo();
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            argumentosFormales();
            bloqueMetodo();
        }else-if(tokenAct.getType.equals("ObjectID")) {
                siguienteTerminal(tokenAct, List.of("ObjectID"));
                argumentosFormales();
                bloqueMetodo();
            }

    }

    /**
     * ⟨Visibilidad⟩ ::= ppub
     *
     */

    public void visibilidad() {
        siguienteTerminal(tokenAct,List.of("ppub"));
    }

    /**
     * ⟨Forma-Método⟩::= pst
     */

    public void formaMetodo() {
            siguienteTerminal(tokenAct,List.of("pst"));
    }

    /**
     * ⟨Bloque-Método⟩::= openBrace <Decl-Var-LocalesE> <SentenciaE> closeBrace
     */

    public void bloqueMetodo() {
            siguienteTerminal(tokenAct,List.of("openBrace"));
            declVarLocalesE();
            sentenciaE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));
    }

    /**
     * <Decl-Var-LocalesE> ::=  <Decl-Var-Locales> <Decl-Var-LocalesE> | λ
     */

    public void declVarLocalesE() {
        List<String> primerosDeclVarLocales = List.of("Str","Bool","Int","ClassID","parray");
        List<String> siguientes = List.of("closeBrace", "semicolon", "id" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace"," pret" );
        if(primerosDeclVarLocales.contains(tokenAct.getType())){
            declvarLocales();
            declVarLocalesE();
        }else-if(siguientes.contains(tokenAct.getType())){
            return;
        }else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID', 'Array' " +
                        "'{', '}', ';' , 'id', 'self', '(' , 'if', 'for', 'while' o  'ret' ." +
                        " Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine+ ", columna " + tokenAct.getColumn());
        }
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
     * ⟨ExpUn⟩ ::= ⟨OpUnario⟩ ⟨ExpUn⟩ | openParen ⟨ExpUnF⟩ | ⟨Literal⟩  | ⟨Primario⟩
     */

    public void expUn() {

    }

    /**
     * ⟨ExpUnF⟩::= Int closeParen ⟨ExpUn⟩| ⟨Expresion⟩ closeParen ⟨ExpresionParentizadaF⟩
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
     * ⟨Acceso-Variable-EncadenadoF⟩::= openBracket ⟨Expresion⟩ closeBracket ⟨Acceso-Variable-EncadenadoF2⟩ | ⟨Encadenado⟩  | λ
      */

    public void accesoVariableEncadenadoF() {

    }

    /**
     * ⟨Acceso-Variable-EncadenadoF2⟩ ::= ⟨Encadenado⟩  | λ
     */
    public void accesoVariableEncadenadoF2(){

    }










}
