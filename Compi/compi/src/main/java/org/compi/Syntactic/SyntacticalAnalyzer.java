package org.compi.Syntactic;

import org.compi.Lexical.LexicalAnalyzer;
import org.compi.Lexical.token;
import java.io.File;
import java.util.List;
import org.compi.Syntactic.SyntacticExceptions;
import org.compi.Semantic.SymbolTable.*;
import org.compi.Semantic.SymbolTable.Class;
import org.compi.Semantic.SymbolTable.SemanticExceptions;
/**
 * Clase que representa el analizador Sintactico
 * @author Enzo Palau
 * @author Luciana Puentes
 */
public class SyntacticalAnalyzer {

    private token tokenAct;
    private LexicalAnalyzer lexicalAnalyzer;

    public SyntacticalAnalyzer(File sourceFile) {
        this.lexicalAnalyzer = new LexicalAnalyzer(sourceFile);
    }

    /**
    * Metodo que se encarga de ver el no terminal que se esta analizando y comparar el token recibido con los tokens esperados
     */
    public void siguienteTerminal (token received , List<String> expected) throws SyntacticExceptions {

        String receivedType = received.getType();
        System.out.println("Token recibido: " + received.getLexeme() + " (Tipo: " + receivedType + ", Linea: " + received.getLine() + ", Columna: " + received.getColumn() + ")");

        for (String expectedToken : expected) {
            if (receivedType.equals(expectedToken)) {
                // avanzamos al siguiente token
                tokenAct = lexicalAnalyzer.nextToken();
                return;
            }
        }
        // no se encontro el token esperado
        throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba uno de los siguientes tokens: " + expected + ". Se encontro '" + received.getLexeme() + "' en la linea " + received.getLine() + ", columna " + received.getColumn());

    }

    /**
     * <program> ::=  <Lista-Definiciones> <Start>
     */

    public void program() {
        tokenAct = lexicalAnalyzer.nextToken();
        List<String> primerosListaDefiniciones = List.of("pclass", "pimpl", "pstart");
        if (!primerosListaDefiniciones.contains(tokenAct.getType())) {
            throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'class', 'impl' o 'start'. Se encontro " + tokenAct.getLexeme() + " en la linea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
        listaDefiniciones();
        start();
        // debemos haber llegado al final del archivo
        if (tokenAct != null) {
            System.err.println("ERROR SINTACTICO Se esperaba el final del archivo.");
        }else {
            System.out.println("Analisis sintactico completado sin errores.");
            //llamar semantico
        }

    }

    /**
     * <Start> ::= pstart <Bloque-Metodo>
     */

    public void start() {
        siguienteTerminal(tokenAct, List.of("pstart"));
        bloqueMetodo();
    }

    /**
     * <Lista-Definiciones> ::= <Class> <Lista-Definiciones> | <Impl> <Lista-Definiciones> |  lambda
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
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'class', 'impl' o 'start'. Se encontro " + tokenAct.getLexeme() + " en la linea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Class> ::=  pclass classID <ClassF>
     */

    public void Class() {
        siguienteTerminal(tokenAct,List.of("pclass"));
        Class clase = new Class(lexicalAnalyzer.nextToken());
        SymbolTable.addClass(clase);
        SymbolTable.setCurrentClass(clase);
        siguienteTerminal(tokenAct,List.of("ClassID"));
        classF();
    }

    /**
     * <ClassF> :: = <Herencia> openParen <AtributoE> closeBrace | openBrace <AtributoE> closeBrace
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
            siguienteTerminal(tokenAct,List.of("openBrace"));
            atributoE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));

        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ':' o '{'. Se encontro " + tokenAct.getLexeme() + " en la linea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <AtributoE> :: = <Atributo> <AtributoE> | lambda
     */

    public void atributoE() {
        List<String> primeros = List.of("ppub", "pstr", "pbool", "pint", "ClassID", "parray");
        if(primeros.contains(tokenAct.getType())){
            atributo();
            atributoE();
        } else if (tokenAct.getType().equals("closeBrace")) {
            //caso lambda
            return;
            
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'pub', 'Str', 'Bool', 'Int' , 'ClassID', 'Array' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Impl> ::= pimpl ClassID openBrace <MiembroE> closeBrace
     */

    public void impl() {
        siguienteTerminal(tokenAct,List.of("pimpl"));
        siguienteTerminal(tokenAct,List.of("ClassID"));
        siguienteTerminal(tokenAct,List.of("openBrace"));
        miembroE();
        siguienteTerminal(tokenAct,List.of("closeBrace"));
    }

    /**
     * <MiembroE> ::= <Miembro> <MiembroE> | lambda
     */

    public void miembroE() {
        List<String> primeros = List.of("pst","pfn","dot");
        if(primeros.contains(tokenAct.getType())){
            miembro();
            miembroE();
        }else if(tokenAct.getType().equals("closeBrace")){
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'st', 'fn', '.' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Herencia> ::= colon <Tipo>
     */

    public void herencia() throws SemanticExceptions{
        siguienteTerminal(tokenAct,List.of("colon"));
        if(!lexicalAnalyzer.nextToken().getType().equals("ClassID")){
            throw new SemanticExceptions("ERROR SEMANTICO DECLARACIONES: El objeto a heredar debe ser una clase Se encontro " +
                                        tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
        SymbolTable.getCurrentClass().setAncestorName(lexicalAnalyzer.nextToken().getLexeme());
        tipo();
    }

    /**
     * <Miembro> ::= <Metodo> | <Constructor>
     */

    public void miembro() {
        if(tokenAct.getType().equals("pst") || tokenAct.getType().equals("pfn")){
            metodo();
        } else if (tokenAct.getType().equals("dot")) {
            constructor();
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'st', 'fn', '.' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Constructor> ::= dot <Argumentos-Formales> <Bloque-Metodo>
     */

    public void constructor() {
        siguienteTerminal(tokenAct,List.of("dot"));
        argumentosFormales();
        bloqueMetodo();
    }

    /** <Atributo> ::= <Visibilidad> <Tipo> <Lista-Declaracion-Variables> semicolon  | <Tipo> <Lista-Declaracion-Variables> semicolon
     *
     */

    public void atributo() {
        List<String> primerosTipo=List.of("pstr", "pbool", "pint", "ClassID", "parray");
        if(tokenAct.getType().equals("ppub")){
            visibilidad();
            tipo();
            listaDeclaracionVariables();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else if(primerosTipo.contains(tokenAct.getType())){
            tipo();
            listaDeclaracionVariables();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'pub', 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Metodo> ::= <Forma-Metodo> pfn <MetodoF> | pfn<MetodoF>
     */

    public void metodo() {
        List<String> primerosFormaMetodo= List.of("pstr","pbool","pint","ClassID" ,"parray", "pvoid");
       if (primerosFormaMetodo.contains(tokenAct.getType())) {
           formaMetodo();
           siguienteTerminal(tokenAct,List.of("pfn"));
           metodoF();
        }else if(tokenAct.getType().equals("pfn")){
           siguienteTerminal(tokenAct,List.of("pfn"));
           metodoF();
       }
        else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' ,'void', 'fn' o 'Array'. Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <MetodoF> ::= <Tipo-Metodo> objectID <Argumentos-Formales> <Bloque-Metodo> | objectID  <Argumentos-Formales> <Bloque-Metodo>
     */

    public void metodoF() {
        List<String> primerosTipoMetodo =List.of("pstr","pbool" ,"pint","ClassID" ,"parray", "pvoid");
        if(primerosTipoMetodo.contains(tokenAct.getType())){
            tipoMetodo();
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            argumentosFormales();
            bloqueMetodo();
        }else if(tokenAct.getType().equals("ObjectID")) {
                siguienteTerminal(tokenAct, List.of("ObjectID"));
                argumentosFormales();
                bloqueMetodo();
            }

    }

    /**
     * <Visibilidad> ::= ppub
     *
     */

    public void visibilidad() {
        siguienteTerminal(tokenAct,List.of("ppub"));
    }

    /**
     * <Forma-Metodo>::= pst
     */

    public void formaMetodo() {
            siguienteTerminal(tokenAct,List.of("pst"));
    }

    /**
     * <Bloque-Metodo>::= openBrace <Decl-Var-LocalesE> <SentenciaE> closeBrace
     */

    public void bloqueMetodo() {
            siguienteTerminal(tokenAct,List.of("openBrace"));
            declVarLocalesE();
            sentenciaE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));
    }

    /**
     * <Decl-Var-LocalesE> ::=  <Decl-Var-Locales> <Decl-Var-LocalesE> | lambda
     */

    // _____ ARREGLAR ______ COSO DE ID
    public void declVarLocalesE() {
        List<String> primerosDeclVarLocales = List.of("pstr","pbool","pint","ClassID","parray");
        List<String> siguientes = List.of("closeBrace", "semicolon", "ObjectID" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace"," pret" );
        if(primerosDeclVarLocales.contains(tokenAct.getType())){
            declVarLocales();
            declVarLocalesE();
        }else if(siguientes.contains(tokenAct.getType())){
            return;
        }else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID', 'Array' " +
                        "'{', '}', ';' , 'ObjectID', 'self', '(' , 'if', 'for', 'while' o  'ret' ." +
                        " Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <SentenciaE> ::=  <Sentencia> <SentenciaE>  | lambda
     */

    public void sentenciaE() {
        //print token
        System.out.println(tokenAct.getType());
        List<String> primerosSentencia = List.of("semicolon", "ObjectID" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace","pret" );
        if (primerosSentencia.contains(tokenAct.getType())) {
            sentencia();
            sentenciaE();
        } else if (tokenAct.getType().equals("closeBrace")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ';' , 'ObjectID', 'self', '(' , 'if', 'for', 'while' , 'ret' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Decl-Var-Locales>::= <Tipo> <Lista-Declaracion-Variables> semicolon
     */

    public void declVarLocales() {
        List<String> primerosDeclVarLocales = List.of("pstr","pbool","pint","ClassID","parray");
        if(primerosDeclVarLocales.contains(tokenAct.getType())){
            tipo();
            listaDeclaracionVariables();
            siguienteTerminal(tokenAct,List.of("semicolon"));

        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Lista-Declaracion-Variables>::= objectID <Lista-Declaracion-VariablesF>
     *
     */

    public void listaDeclaracionVariables() {

        if(tokenAct.getType().equals("ObjectID")){
                siguienteTerminal(tokenAct,List.of("ObjectID"));
                listaDeclaracionVariablesF();
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ObjectID'. Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
            }
    }


    /**
     * <Lista-Declaracion-VariablesF>::= comma <Lista-Declaracion-Variables> | lambda
     */

    public void listaDeclaracionVariablesF() {
        if (tokenAct.getType().equals("comma")) {
            siguienteTerminal(tokenAct,List.of("comma"));
            listaDeclaracionVariables();
        } else if (tokenAct.getType().equals("semicolon")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ',' o ';'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Argumentos-Formales>::= openParen <Argumentos-FormalesF>
     */

    public void argumentosFormales() {
        if (tokenAct.getType().equals("openParen")) {
            siguienteTerminal(tokenAct,List.of("openParen"));
            argumentosFormalesF();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '('. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Argumentos-FormalesF>::= <Lista-Argumentos-Formales> closeParen | closeParen
     */

    public void argumentosFormalesF() {
        List<String> primerosListaArgumentosFormales = List.of("pstr","pbool","pint","ClassID","parray");
        if (tokenAct.getType().equals("closeParen")) {
            siguienteTerminal(tokenAct,List.of("closeParen"));
        } else if (primerosListaArgumentosFormales.contains(tokenAct.getType())) {
            listaArgumentosFormales();
            siguienteTerminal(tokenAct,List.of("closeParen"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '(', 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());

        }

    }

    /**
     * <Lista-Argumentos-Formales> ::= <Argumento-Formal><Lista-Argumentos-FormalesF>
     */

    public void listaArgumentosFormales() {
        List<String> primerosArgumentoFormal = List.of("pstr","pbool","pint","ClassID","parray");
        if (primerosArgumentoFormal.contains(tokenAct.getType())) {
            argumentoFormal();
            listaArgumentosFormalesF();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Lista-Argumentos-FormalesF> ::= comma <Lista-Argumentos-Formales> | lambda
     */

    public void listaArgumentosFormalesF() {
        if (tokenAct.getType().equals("comma")) {
            siguienteTerminal(tokenAct,List.of("comma"));
            listaArgumentosFormales();
        } else if (tokenAct.getType().equals("closeParen")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ',' o ')'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Argumento-Formal> ::= <Tipo> objectID
     */

    public void argumentoFormal() {

        List<String> primerosTipo = List.of("pstr","pbool","pint","ClassID","parray");
        if (primerosTipo.contains(tokenAct.getType())) {
            tipo();
            siguienteTerminal(tokenAct,List.of("ObjectID"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Tipo-Metodo> ::=  <Tipo> | pvoid
     */

    public void tipoMetodo() {

        List<String> primerosTipoMetodo = List.of("pstr","pbool" ,"pint","ClassID" ,"parray");
        if (primerosTipoMetodo.contains(tokenAct.getType())) {
            tipo();
        } else if (tokenAct.getType().equals("pvoid")) {
            siguienteTerminal(tokenAct,List.of("pvoid"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' ,'void' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Tipo> ::= <Tipo-Primitivo> | <Tipo-Referencia> | <Tipo-Arreglo>
     */

    public  void tipo() {

        List<String> primerosTipo = List.of("pstr","pbool","pint");
        if (primerosTipo.contains(tokenAct.getType())) {
            tipoPrimitivo();
        } else if (tokenAct.getType().equals("ClassID")) {
            tipoReferencia();
        } else if (tokenAct.getType().equals("parray")) {
            tipoArreglo();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID' o 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /** <Tipo-Primitivo> ::= Str | Int | Bool
     */
    public void tipoPrimitivo() {
        if (tokenAct.getType().equals("pstr") || tokenAct.getType().equals("pint") || tokenAct.getType().equals("pbool")) {
            siguienteTerminal(tokenAct,List.of("pstr", "pint", "pbool"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Int' o 'Bool'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /** <Tipo-Referencia> ::= idclass
     *
     */

    public void tipoReferencia() {
        if (tokenAct.getType().equals("ClassID")) {
            siguienteTerminal(tokenAct,List.of("ClassID"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ClassID'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Tipo-Arreglo> ::= parray <Tipo-Primitivo>
     */

    public void tipoArreglo() {
        if (tokenAct.getType().equals("parray")) {
            siguienteTerminal(tokenAct,List.of("parray"));
            tipoPrimitivo();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Array'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }
    /**
     * <Sentencia> ::=  semicolon | <Asignacion> semicolon | <Sentencia-Simple> semicolon | pif openParen <Expresion> closeParen <Sentencia> <SentenciaFE> |
     * pwhile openParen <Expresion> closeParen <Sentencia> | pfor openParen <Tipo-Primitivo> objectID pin objectID closeParen <Sentencia> | <Bloque> | pret <SentenciaFR>
     */

    public void sentencia() {

        List<String> primerosSentencia = List.of("semicolon", "ObjectID" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace"," pret" );
        if (tokenAct.getType().equals("semicolon")) {
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else if (tokenAct.getType().equals("ObjectID") || tokenAct.getType().equals("pself")) {
            asignacion();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else if (tokenAct.getType().equals("openParen")) {
            sentenciaSimple();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else if (tokenAct.getType().equals("pif")) {
            siguienteTerminal(tokenAct,List.of("pif"));
            siguienteTerminal(tokenAct,List.of("openParen"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeParen"));
            sentencia();
            sentenciaFE();
        } else if (tokenAct.getType().equals("pwhile")) {
            siguienteTerminal(tokenAct,List.of("pwhile"));
            siguienteTerminal(tokenAct,List.of("openParen"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeParen"));
            sentencia();
        } else if (tokenAct.getType().equals("pfor")) {
            siguienteTerminal(tokenAct,List.of("pfor"));
            siguienteTerminal(tokenAct,List.of("openParen"));
            tipoPrimitivo();
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            siguienteTerminal(tokenAct,List.of("pin"));
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            siguienteTerminal(tokenAct,List.of("closeParen"));
            sentencia();
        } else if (tokenAct.getType().equals("openBrace")) {
            bloque();
        } else if (tokenAct.getType().equals("pret")) {
            siguienteTerminal(tokenAct,List.of("pret"));
            sentenciaFR();
        } else {
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ';' , 'ObjectID', 'self', '(' , 'if', 'for', 'while' , 'ret' o '{'. Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

     /**
      * <SentenciaFE> ::= pelse <Sentencia> | lambda
      */

    public void sentenciaFE() {
        List<String>  siguientesSentenciaFE = List.of("semicolon", "ObjectID", "pself", "openParen", "pif", "pwhile","pfor","openBrace","pret","closeBrace");
        if (tokenAct.getType().equals("pelse")){
            siguienteTerminal(tokenAct,List.of("pelse"));
            sentencia();
        } else if (siguientesSentenciaFE.contains(tokenAct.getType())){
            return;
        }else{
            throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba ';' else. Se encontro" + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <SentenciaFR> ::=  <Expresion> semicolon | semicolon
     * primeros =  sumOperator substractionOperator notOperator incrementOperator decrementOperator nil ptrue pfalse const_int const_str openParen self  ObjectID idclass new semicolon
     */

    public void sentenciaFR(){

        List<String> primerosSentenciaFR = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosSentenciaFR.contains(tokenAct.getType())) {
            expresion();
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else if (tokenAct.getType().equals("semicolon")) {
            siguienteTerminal(tokenAct,List.of("semicolon"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID', 'new' o ';'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Bloque> ::=  openBrace <SentenciaE> closeBrace
     */

    public void bloque() {

        if (tokenAct.getType().equals("openBrace")) {
            siguienteTerminal(tokenAct,List.of("openBrace"));
            sentenciaE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '{'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Asignacion> ::= <AccesoVar-Simple> equalOperator <Expresion> | <AccesoSelf-Simple> equalOperator <Expresion>
     */

    public void asignacion() {

        List<String> primerosAsignacion = List.of("ObjectID", "pself");
        if (primerosAsignacion.contains(tokenAct.getType())) {
            if (tokenAct.getType().equals("ObjectID")) {
                accesoVarSimple();
                siguienteTerminal(tokenAct,List.of("assignmentOperator"));
                expresion();
            } else if (tokenAct.getType().equals("pself")) {
                accesoSelfSimple();
                siguienteTerminal(tokenAct,List.of("assignmentOperator"));
                expresion();
            }
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ObjectID' o 'self'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <AccesoVar-Simple> ::= ObjectID <AccesoVar-SimpleF>
     */

    public void accesoVarSimple() {

        if (tokenAct.getType().equals("ObjectID")) {
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            accesoVarSimpleF();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ObjectID'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <AccesoVar-SimpleF> ::= <Encadenado-SimpleE>  | openBracket <Expresion> closeBracket
     */

    public void accesoVarSimpleF() {
        //print del token para debug
        System.out.println("Token en accesoVarSimpleF: " + tokenAct.getLexeme() + " (Tipo: " + tokenAct.getType() + ", Linea: " + tokenAct.getLine() + ", Columna: " + tokenAct.getColumn() + ")");
        if (tokenAct.getType().equals("dot") || tokenAct.getType().equals("openParen")) {
            encadenadoSimpleE();
        } else if (tokenAct.getType().equals("openBracket")) {
            siguienteTerminal(tokenAct,List.of("openBracket"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeBracket"));
        } else if (tokenAct.getType().equals("assignmentOperator")) {
            //caso lambda
            System.out.println("Lambda de accesoVarSimpleF");
            encadenadoSimpleE(); } // accesoVarSimpleF no deriva en lamda el lamda de sus primeros viene de aca
        else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '.' , '(', '=' o '['. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Encadenado-SimpleE> ::= <Encadenado-Simple> <Encadenado-SimpleE> | lambda
     */

    public void encadenadoSimpleE() {
        if(tokenAct.getType().equals("dot")){
            encadenadoSimple();
            encadenadoSimpleE();
        } else if (tokenAct.getType().equals("assignmentOperator")) {
            return;
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '.' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }


    }

    /**
     * <AccesoSelf-Simple> ::=  pself <Encadenado-SimpleE>
     */

    public void accesoSelfSimple() {

        if (tokenAct.getType().equals("pself")) {
            siguienteTerminal(tokenAct,List.of("pself"));
            encadenadoSimpleE();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'self'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Encadenado-Simple> ::= dot ObjectID
     */

    public void encadenadoSimple() {


        if (tokenAct.getType().equals("dot")) {
            siguienteTerminal(tokenAct,List.of("dot"));
            siguienteTerminal(tokenAct,List.of("ObjectID"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '.'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Sentencia-Simple> ::= openParen <Expresion> closeParen
     */

    public void sentenciaSimple() {

        if (tokenAct.getType().equals("openParen")) {
            siguienteTerminal(tokenAct,List.of("openParen"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeParen"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '('. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     *  <Expresion> ::= <ExpOr >
     */

    public void expresion() {

        List<String> primerosExpresion = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpresion.contains(tokenAct.getType())) {
            expOr();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <ExpOr>  ::= <ExpAnd> <EO>
     */

    public void expOr() {
        List<String> primerosExpor = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpor.contains(tokenAct.getType())) {
            expAnd();
            EO();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <EO>  ::= orOperator <ExpAnd> <EO> | lambda
     */

    public void EO() {
        List<String> siguientes = List.of("semicolon", "closeParen", "closeBracket" ,"comma");
        if(tokenAct.getType().equals("orOperator")){
            siguienteTerminal(tokenAct,List.of("orOperator"));
            expAnd();
            EO();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '||', ';' , ')', '}' o ','. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <ExpAnd>  ::= <ExpIgual> <EA>
     */

    public void expAnd() {

        List<String> primerosExpAnd = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpAnd.contains(tokenAct.getType())) {
            expIgual();
            EA();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <EA>  ::= andOperator <ExpIgual> <EA> | lambda
     */

    public void EA() {
        List<String> siguientes = List.of("orOperator","closeParen","closeBracket","semicolon","comma");
        if(tokenAct.getType().equals("andOperator")){
            siguienteTerminal(tokenAct,List.of("andOperator"));
            expIgual();
            EA();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '||', ';' , ')', '}' o ','. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <ExpIgual>  ::= <ExpCompuesta> <EI>
     */

    public void expIgual() {

        List<String> primerosExpIgual = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpIgual.contains(tokenAct.getType())) {
            expCompuesta();
            EI();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <EI>  ::= <OpIgual>  <ExpCompuesta> | lambda
     */

    public void EI() {
        List<String> primerosOpIgual =List.of("equalOperator","notEqualOperator");
        List<String> siguientes = List.of("andOperator", "semicolon", "closeParen", "orOperator", "closeBracket", "comma");
        if(primerosOpIgual.contains(tokenAct.getType())){
            opIgual();
            expCompuesta();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '=', '!=', '||', '&&', ';', '}', ')' o ','. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <ExpCompuesta> ::= <ExpAd> <ExpCompuestaF>
     */

    public void expCompuesta() {

        List<String> primerosExpCompuesta = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpCompuesta.contains(tokenAct.getType())) {
            expAd();
            expCompuestaF();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <ExpCompuestaF> ::= <OpCompuesto> <ExpAd>  | lambda
     */

    public void expCompuestaF() {
        List<String> primerosOpCompuesto = List.of("greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        List<String> siguientes = List.of("equalOperator", "notEqualOperator", "andOperator", "semicolon", "closeParen", "orOperator", "closeBracket" , "comma");
        if(primerosOpCompuesto.contains(tokenAct.getType())){
             opCompuesto();
             expAd();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}' o ','. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <ExpAd>  ::= <ExpMul> <EAD>
     */

    public void expAd() {

        List<String> primerosExpAd = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpAd.contains(tokenAct.getType())) {
            ExpMul();
            EAD();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <EAD>  ::= OpAd <ExpMul> <EAD> | lambda
     */

    public void EAD() {

        List<String> primerosOpAd = List.of("sumOperator", "substractionOperator");
        List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator", "equalOperator", "notEqualOperator", "andOperator",  "closeParen",
                "orOperator", "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator", "comma");
        if (primerosOpAd.contains(tokenAct.getType())){
            opAd();
            ExpMul();
            EAD();
        } else if (siguientes.contains(tokenAct.getType())){
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '+' o '-' o una expresion. Se encontro "
                    + tokenAct.getLexeme() + " en la linea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <ExpMul>  ::= <ExpUn> <EM>
     */

    public void ExpMul() {

        List<String> primerosExpMul = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpMul.contains(tokenAct.getType())) {
            expUn();
            EM();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <EM>  ::= <OpMul> <ExpUn> <EM> | lambda
     */

    public void EM() {
        List<String> siguientes = List.of("sumOperator", "substractionOperator", "equalOperator", "notEqualOperator", "andOperator",  "closeParen",
                "orOperator", "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator", "comma");
        if(tokenAct.getType().equals("multiplicationOperator")||tokenAct.getType().equals("divOperator")){
            opMul();
            expUn();
            EM();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' o ','. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <ExpUn> ::= <OpUnario> <ExpUn> | openParen <ExpUnF> | <Literal>  | <Primario>
     */

    public void expUn() {

        List<String> primerosExpUn = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "pnil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID", "ClassID", "pnew");
        if (primerosExpUn.contains(tokenAct.getType())) {
            if (tokenAct.getType().equals("sumOperator") || tokenAct.getType().equals("substractionOperator") || tokenAct.getType().equals("notOperator") || tokenAct.getType().equals("incrementOperator") || tokenAct.getType().equals("decrementOperator")) {
                opUnario();
                expUn();
            } else if (tokenAct.getType().equals("openParen")) {
                siguienteTerminal(tokenAct,List.of("openParen"));
                ExpUnF();
            } else if (tokenAct.getType().equals("pnil") || tokenAct.getType().equals("ptrue") || tokenAct.getType().equals("pfalse") || tokenAct.getType().equals("IntegerLiteral") || tokenAct.getType().equals("StringLiteral")) {
                literal();
            } else if (tokenAct.getType().equals("pself") || tokenAct.getType().equals("ObjectID") || tokenAct.getType().equals("ClassID") || tokenAct.getType().equals("pnew")) {
                primario();
            }
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <ExpUnF> ::= Int closeParen <ExpUn> | <Expresion> closeParen <ExpresionParentizadaF>
     */

    public void ExpUnF() {
        List<String> primerosExpresion = List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator", "nil", "ptrue", "pfalse", "const_int", "const_str",
                "openParen", "self",  "ObjectID", "ClassID", "new");
        if(tokenAct.getType().equals("Int")){
            siguienteTerminal(tokenAct,List.of("Int"));
            siguienteTerminal(tokenAct,List.of("closeParen"));
            expUn();
        } else if (primerosExpresion.contains(tokenAct.getType())) {
            expresion();
            siguienteTerminal(tokenAct,List.of("closeParen"));
            expresionParentizadaF();
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Int', un operador, 'nil', 'true', 'false', un literal, '(', 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <OpIgual> ::= equalOperator | notEqualOperator
     */

    public void opIgual() {

        if (tokenAct.getType().equals("equalOperator") || tokenAct.getType().equals("notEqualOperator")) {
            siguienteTerminal(tokenAct,List.of("equalOperator", "notEqualOperator"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '==' o '!='. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <OpCompuesto> ::= greaterThanOperator | lessThanOperator | greaterThanOrEqualOperator | lessThanOrEqualOperator
     */

    public void opCompuesto() {

        if (tokenAct.getType().equals("greaterThanOperator") || tokenAct.getType().equals("lessThanOperator") || tokenAct.getType().equals("greaterThanOrEqualOperator") || tokenAct.getType().equals("lessThanOrEqualOperator")) {
            siguienteTerminal(tokenAct,List.of("greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '>', '<', '>=' o '<='. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <OpAd> ::=  sumOperator | substractionOperator
     */

    public void opAd() {

        if (tokenAct.getType().equals("sumOperator") || tokenAct.getType().equals("substractionOperator")) {
            siguienteTerminal(tokenAct,List.of("sumOperator", "substractionOperator"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '+' o '-'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <OpUnario> ::=  sumOperator | substractionOperator | notOperator | incrementOperator | decrementOperator
     */

    public void opUnario() {

        if (tokenAct.getType().equals("sumOperator") || tokenAct.getType().equals("substractionOperator") || tokenAct.getType().equals("notOperator") || tokenAct.getType().equals("incrementOperator") || tokenAct.getType().equals("decrementOperator")) {
            siguienteTerminal(tokenAct,List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '+', '-', '!' , '++' o '--'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <OpMul> ::=  multiplicationOperator | divOperator
      */

    public void opMul() {

        if (tokenAct.getType().equals("multiplicationOperator") || tokenAct.getType().equals("divOperator")) {
            siguienteTerminal(tokenAct,List.of("multiplicationOperator", "divOperator"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '*' o '/'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Literal> := nil | ptrue | pfalse | IntegerLiteral| StringLiteral
      */

    public void literal() {

        if (tokenAct.getType().equals("pnil") || tokenAct.getType().equals("ptrue") || tokenAct.getType().equals("pfalse") || tokenAct.getType().equals("StringLiteral") || tokenAct.getType().equals("IntegerLiteral")) {
            siguienteTerminal(tokenAct,List.of("pnil", "ptrue", "pfalse", "StringLiteral", "IntegerLiteral"));
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'nil', 'true', 'false', 'StringLiteral' o 'IntegerLiteral'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <Primario>:=  <AccesoSelf > | ObjectID PrimarioF | <Llamada-Metodo-Estatico> | <Llamada-Conclassor >
     */

    public void primario() {

        if (tokenAct.getType().equals("pself")) {
            accesoSelf();
        } else if (tokenAct.getType().equals("ObjectID")) {
            siguienteTerminal(tokenAct,List.of("ObjectID"));
            primarioF();
        } else if (tokenAct.getType().equals("ClassID")) {
            llamadaMetodoEstatico();
        } else if (tokenAct.getType().equals("pnew")) {
            llamadaConclassor();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'self', 'ObjectID', 'ClassID' o 'new'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <PrimarioF>:= <AccesoVarF2 > | <Argumentos-Actuales> <Llamada-MetodoF>
     */

    public void primarioF() {
        //sig
        List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        if(tokenAct.getType().equals("openBracket")||tokenAct.getType().equals("dot")){
            accesoVarF2();
        } else if (tokenAct.getType().equals("openParen")) {
            argumentosActuales();
            llamadaMetodoF();
        } else if (siguientes.contains(tokenAct.getType())) {
            accesoVarF2();
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '{', '.' o '('. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }



    /**
     * <ExpresionParentizadaF>:= <Encadenado>  | lambda
     */

    public void expresionParentizadaF() {
        List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        if(tokenAct.getType().equals("dot")){
            encadenado();
        }else if(siguientes.contains(tokenAct.getType())){
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                    " '+' , '.' o  '-' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

     /**
      * <AccesoSelf >:= self <AccesoSelfF >
       */

     public void accesoSelf() {

         if (tokenAct.getType().equals("pself")) {
             siguienteTerminal(tokenAct,List.of("pself"));
             accesoSelfF();
         } else {
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'self'. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <AccesoSelfF >:= <Encadenado>  | lambda
      */

     public void accesoSelfF() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        if(tokenAct.getType().equals("dot")){
            encadenado();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                    " '+' , '.' o  '-' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }

     /**
      * <AccesoVarF2 >::=  openBracket <Expresion> closeBracket <AccesoVarF3 > | <Encadenado>  | lambda
      */

     public void accesoVarF2() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("openBracket")){
            siguienteTerminal(tokenAct,List.of("openBracket"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeBracket"));
            accesoVarF3();
         } else if (tokenAct.getType().equals("dot")) {
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }

     /**
      * <AccesoVarF3 > ::= <Encadenado>  | lambda
      */

     public void accesoVarF3() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        if(tokenAct.getType().equals("dot")){
            encadenado();
        } else if (siguientes.contains(tokenAct.getType())) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                    " '+' , '.' o  '-' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }
    /** <Llamada-Metodo>::= ObjectID <Argumentos-Actuales> <Llamada-MétodoF>
     *
     */
    public void llamadaMetodo(){
        siguienteTerminal(tokenAct,List.of("ObjectID"));
        argumentosActuales();
        llamadaMetodoF();
    }

    /**
      * <Llamada-MetodoF>:= <Encadenado>  | lambda
      */

     public void llamadaMetodoF() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("dot")){
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }

     /**
      * <Llamada-Metodo-Estatico>:= idclass dot <Llamada-Metodo> <Llamada-Metodo-EstaticoF>
      */

     public void llamadaMetodoEstatico() {

         if (tokenAct.getType().equals("ClassID")) {
                 siguienteTerminal(tokenAct,List.of("ClassID"));
                 siguienteTerminal(tokenAct,List.of("dot"));
                 llamadaMetodo();
                 llamadaMetodoEstaticoF();
             } else {
                 throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ClassID'. Se encontro "
                         + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
             }

     }

     /**
      * <Llamada-Metodo-EstaticoF>:= <Encadenado> | lambda
      */

     public void llamadaMetodoEstaticoF() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("dot")){
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }

     /**
      * <Llamada-Conclassor> ::= pnew< Llamada-ConclassorF >
      */

     public void llamadaConclassor() {

         if (tokenAct.getType().equals("pnew")) {
             siguienteTerminal(tokenAct,List.of("pnew"));
             llamadaConclassorF();
         } else {
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'new'. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <Llamada-ConclassorF >::=  idclass <Argumentos-Actuales> <Llamada-ConclassorF2 > | <Tipo-Primitivo> openBracket <Expresion> closeBracket
      */

     public void llamadaConclassorF() {
         List<String> primerosTipoPrimitivo= List.of("pstr","pint","pbool");
         if(tokenAct.getType().equals("ClassID")){
             siguienteTerminal(tokenAct,List.of("ClassID"));
             argumentosActuales();
             llamadaConclassorF2();
         } else if (primerosTipoPrimitivo.contains(tokenAct.getType())) {
            tipoPrimitivo();
            siguienteTerminal(tokenAct,List.of("openBracket"));
            expresion();
             siguienteTerminal(tokenAct,List.of("closeBracket"));
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ClassID', 'Str', 'Int' o 'Bool'. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <Llamada-ConclassorF2 >::= <Encadenado>  | lambda
      */

     public void llamadaConclassorF2() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("dot")){
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }

     /**
      * <Argumentos-Actuales> ::= openParen <Argumentos-ActualesF>
      */

     public void argumentosActuales() {

         if (tokenAct.getType().equals("openParen")) {
             siguienteTerminal(tokenAct,List.of("openParen"));
             argumentosActualesF();
         } else {
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '('. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <Argumentos-ActualesF> ::= <Lista-Expresiones>  closeParen | closeParen
      */

     public void argumentosActualesF() {
        List<String> primerosListaExpresiones =List.of("sumOperator", "substractionOperator", "notOperator", "incrementOperator", "decrementOperator",
                "nil", "ptrue", "pfalse", "IntegerLiteral", "StringLiteral", "openParen", "pself", "ObjectID" ,"ClassID" ,"pnew");
        if(primerosListaExpresiones.contains(tokenAct.getType())){
            listaExpresiones();
            siguienteTerminal(tokenAct,List.of("closeParen"));
        } else if (tokenAct.getType().equals("closeParen")) {
            siguienteTerminal(tokenAct,List.of("closeParen"));
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '+', '-', '!', '++', '--', 'nil', 'true', 'false', 'const_str'," +
                    " 'const_int', '(', 'self', 'ObjectID', 'ClassID', o 'new' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }

     /**
      * <Lista-Expresiones>::= <Expresion>  <Lista-ExpresionesF>
      */

     public void listaExpresiones() {
         List<String> primerosExpresion = List.of("sumOperator", "substractionOperator", "notOperator",
                 "incrementOperator", "decrementOperator", "nil", "ptrue", "pfalse","IntegerLiteral", "StringLiteral",
                 "openParen", "self",  "ObjectID", "ClassID", "new");
         if(primerosExpresion.contains(tokenAct.getType())){
             expresion();
             listaExpresionesF();
         }
            else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '+', '-', '!', '++', '--', 'nil', 'true', 'false', 'const_str'," +
                        " 'const_str', '(', 'self', 'nil',  '+' , 'ObjectID', 'ClassID', 'new' o  '-' . Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
            }
     }

     /**
      * <Lista-ExpresionesF> ::= comma <Lista-Expresiones> | lambda
      */

     public void listaExpresionesF() {

        if(tokenAct.getType().equals("comma")){
            siguienteTerminal(tokenAct,List.of("comma"));
            listaExpresiones();
        } else if (tokenAct.getType().equals("closeParen")) {
            return;
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ',' o ')'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }

     /**
      * <Encadenado> ::= dot<EncadenadoF>
      */

     public void encadenado() {

         if (tokenAct.getType().equals("dot")) {
             siguienteTerminal(tokenAct,List.of("dot"));
             encadenadoF();
         } else {
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '.'. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <EncadenadoF> ::= ObjectID <EncadenadoF2>
      */

     public void encadenadoF() {

         if (tokenAct.getType().equals("ObjectID")) {
             siguienteTerminal(tokenAct,List.of("ObjectID"));
             encadenadoF2();
         } else {
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'ObjectID'. Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }

     }

     /**
      * <EncadenadoF2> ::= <Argumentos-Actuales> <Llamada-Metodo-EncadenadoF> | <Acceso-Variable-EncadenadoF>
      */

     public void encadenadoF2() {
        if(tokenAct.getType().equals("openParen")){
            argumentosActuales();
            llamadaMetodoEncadenadoF();
        } else if (tokenAct.getType().equals("openBracket")|| tokenAct.getType().equals("dot")) {
            accesoVariableEncadenadoF();
        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba '(' , '{' o '.'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }

     /**
      * <Llamada-Metodo-EncadenadoF> ::= <Encadenado>  | lambda
      */

     public void llamadaMetodoEncadenadoF() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("dot")){
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }

     /**
      * <Acceso-Variable-EncadenadoF> ::= openBracket <Expresion> closeBracket <Acceso-Variable-EncadenadoF2> | <Encadenado>  | lambda
      */

     public void accesoVariableEncadenadoF() {
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
        if(tokenAct.getType().equals("openBracket")){
            siguienteTerminal(tokenAct,List.of("openBracket"));
            expresion();
            siguienteTerminal(tokenAct,List.of("closeBracket"));
            accesoVariableEncadenadoF2();
        } else if (tokenAct.getType().equals("dot")) {
            encadenado();
        }else{
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                    " '+' , '{', '.' o  '-' . Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
     }

     /**
      * <Acceso-Variable-EncadenadoF2> ::= <Encadenado>  | lambda
      */
     public void accesoVariableEncadenadoF2(){
         List<String> siguientes = List.of("multiplicationOperator", "divOperator", "sumOperator", "substractionOperator"
                 ,"equalOperator", "notEqualOperator", "andOperator",  "closeParen", "orOperator",
                 "closeBracket", "semicolon", "greaterThanOperator", "lessThanOperator", "greaterThanOrEqualOperator", "lessThanOrEqualOperator");
         if(tokenAct.getType().equals("dot")){
             encadenado();
         } else if (siguientes.contains(tokenAct.getType())) {
             return;
         }else{
             throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba un operador, '>', '<', '>=', '<=', '=', '!=', '&&', ';', ')', '||', '}', '*', '/' " +
                     " '+' , '.' o  '-' . Se encontro "
                     + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
         }
     }






 }
