package org.compi.Syntactic;

import org.compi.Lexical.LexicalAnalyzer;
import org.compi.Lexical.token;
import java.io.File;
import java.util.List;
import org.compi.Syntactic.SyntacticExceptions;

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

        for (String expectedToken : expected) {
            if (receivedType.equals(expectedToken)) {
                // avanzamos al siguiente token
                tokenAct = lexicalAnalyzer.nextToken();
                return;
            }
        }
        // no se encontro el token esperado
        throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba uno de los siguientes tokens: " + expected + ". Se encontró '" + received.getLexeme() + "' en la línea " + received.getLine() + ", columna " + received.getColumn());

    }

    /**
     * <program> ::=  <Lista-Definiciones> <Start>
     */

    public void program() {
        tokenAct = lexicalAnalyzer.nextToken();
        List<String> primerosListaDefiniciones = List.of("pclass", "pimpl", "pstart");
        if (!primerosListaDefiniciones.contains(tokenAct.getType())) {
            throw new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'class', 'impl' o 'start'. Se encontro " + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
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
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'class', 'impl' o 'start'. Se encontro " + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <Class> ::=  pclass classID <ClassF>
     */

    public void Class() {
        siguienteTerminal(tokenAct,List.of("pclass"));
        siguienteTerminal(tokenAct,List.of("classID"));
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
            atributoE();
            siguienteTerminal(tokenAct,List.of("closeBrace"));

        }else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ':' o '{'. Se encontro " + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine() + ", columna " + tokenAct.getColumn());
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

    public void herencia() {
        siguienteTerminal(tokenAct,List.of("colon"));
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
        List<String> siguientes = List.of("closeBrace", "semicolon", "id" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace"," pret" );
        if(primerosDeclVarLocales.contains(tokenAct.getType())){
            declVarLocales();
            declVarLocalesE();
        }else if(siguientes.contains(tokenAct.getType())){
            return;
        }else{
                throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'Str', 'Bool', 'Int', 'ClassID', 'Array' " +
                        "'{', '}', ';' , 'id', 'self', '(' , 'if', 'for', 'while' o  'ret' ." +
                        " Se encontro "
                        + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }
    }

    /**
     * <SentenciaE> ::=  <Sentencia> <SentenciaE>  | lambda
     */

    public void sentenciaE() {
        List<String> primerosSentencia = List.of("semicolon", "id" ,"pself" ,"openParen" ,"pif" ,"pwhile", "pfor", "openBrace"," pret" );
        if (primerosSentencia.contains(tokenAct.getType())) {
            sentencia();
            sentenciaE();
        } else if (tokenAct.getType().equals("closeBrace")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba ';' , 'id', 'self', '(' , 'if', 'for', 'while' , 'ret' o '}'. Se encontro "
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

    }

    /**
     * <Lista-Argumentos-Formales> ::= <Argumento-Formal><Lista-Argumentos-FormalesF>
     */

    public void listaArgumentosFormales() {

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

    }

    /**
     * <Tipo-Metodo> ::=  <Tipo> | pvoid
     */

    public void tipoMetodo() {

    }

    /**
     * <Tipo> ::= <Tipo-Primitivo> | <Tipo-Referencia> | <Tipo-Arreglo>
     */

    public  void tipo() {

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

    }

     /**
      * <SentenciaFE> ::= pelse <Sentencia> | lambda
      */
    // ARREGLAR TEMA DE IF ELSE
    public void sentenciaFE() {
        if (tokenAct.getType().equals("pelse")) {
            siguienteTerminal(tokenAct,List.of("pelse"));
            sentencia();
        } else if (tokenAct.getType().equals("closeBrace") || tokenAct.getType().equals("semicolon") || tokenAct.getType().equals("id") || tokenAct.getType().equals("pself") || tokenAct.getType().equals("openParen") || tokenAct.getType().equals("pif") || tokenAct.getType().equals("pwhile") || tokenAct.getType().equals("pfor") || tokenAct.getType().equals("pret") || tokenAct.getType().equals("openBrace")) {
            return;
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'else', ';' , 'id', 'self', '(' , 'if', 'for', 'while' , 'ret' o '}'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <SentenciaFR> ::=  <Expresion> semicolon | semicolon
     */

    public void sentenciaFR(){

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

    }

    /**
     * <AccesoVar-Simple> ::= id <AccesoVar-SimpleF>
     */

    public void accesoVarSimple() {

        if (tokenAct.getType().equals("id")) {
            siguienteTerminal(tokenAct,List.of("id"));
            accesoVarSimpleF();
        } else {
            throw  new SyntacticExceptions("ERROR SINTACTICO Se esperaba 'id'. Se encontro "
                    + tokenAct.getLexeme() + " en la línea " + tokenAct.getLine ()+ ", columna " + tokenAct.getColumn());
        }

    }

    /**
     * <AccesoVar-SimpleF> ::= <Encadenado-SimpleE>  | openBracket <Expresion> closeBracket
     */

    public void accesoVarSimpleF() {

    }

    /**
     * <Encadenado-SimpleE> ::= <Encadenado-Simple> <Encadenado-SimpleE> | lambda
     */

    public void encadenadoSimpleE() {

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
     * <Encadenado-Simple> ::= dot id
     */

    public void encadenadoSimple() {

        if (tokenAct.getType().equals("dot")) {
            siguienteTerminal(tokenAct,List.of("dot"));
            siguienteTerminal(tokenAct,List.of("id"));
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

    }

    /**
     * <ExpOr>  ::= <ExpAnd> <EO>
     */

    public void expOr() {
    }

    /**
     * <EO>  ::= orOperator <ExpAnd> <EO> | lambda
     */

    public void EO() {

    }

    /**
     * <ExpAnd>  ::= <ExpIgual> <EA>
     */

    public void expAnd() {

    }

    /**
     * <EA>  ::= andOperator <ExpIgual> <EA> | lambda
     */

    public void EA() {

    }

    /**
     * <ExpIgual>  ::= <ExpCompuesta> <EI>
     */

    public void expIgual() {

    }

    /**
     * <EI>  ::= OpIgual <ExpCompuesta> | lambda
     */

    public void EI() {

    }

    /**
     * <ExpCompuesta> ::= <ExpAd> <ExpCompuestaF>
     */

    public void expCompuesta() {

    }

    /**
     * <ExpCompuestaF> ::= <OpCompuesto> <ExpAd>  | lambda
     */

    public void expCompuestaF() {

    }

    /**
     * <ExpAd>  ::= <ExpMul> <EAD>
     */

    public void expAd() {

    }

    /**
     * <EAD>  ::= OpAd <ExpMul> <EAD> | lambda
     */

    public void EAD() {

    }

    /**
     * <ExpMul>  ::= <ExpUn> <EM>
     */

    public void ExpMul() {

    }

    /**
     * <EM>  ::= OpMul <ExpUn> <EM> | lambda
     */

    public void EM() {

    }

    /**
     * <ExpUn> ::= <OpUnario> <ExpUn> | openParen <ExpUnF> | <Literal>  | <Primario>
     */

    public void expUn() {

    }

    /**
     * <ExpUnF> ::= Int closeParen <ExpUn> | <Expresion> closeParen <ExpresionParentizadaF>
     */

    public void ExpUnF() {



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
     * <Primario>:=  <AccesoSelf > | id PrimarioF | <Llamada-Metodo-Estatico> | <Llamada-Conclassor >
     */

    public void primario() {

    }

    /**
     * <PrimarioF>:= <AccesoVarF2 > | <Argumentos-Actuales> <Llamada-MetodoF>
     */

    public void primarioF() {

    }



    /**
     * <ExpresionParentizadaF>:= <Encadenado>  | lambda
     */

    public void expresionParentizadaF() {

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

     }

     /**
      * <AccesoVarF2 >::=  openBracket <Expresion> closeBracket <AccesoVarF3 > | <Encadenado>  | lambda
      */

     public void accesoVarF2() {

     }

     /**
      * <AccesoVarF3 > ::= <Encadenado>  | lambda
      */

     public void accesoVarF3() {

     }

     /**
      * <Llamada-MetodoF>:= <Encadenado>  | lambda
      */

     public void llamadaMetodoF() {

     }

     /**
      * <Llamada-Metodo-Estatico>:= idclass dot <Llamada-Metodo> <Llamada-Metodo-EstaticoF>
      */

     public void llamadaMetodoEstatico() {

         if (tokenAct.getType().equals("ClassID")) {
                 siguienteTerminal(tokenAct,List.of("ClassID"));
                 siguienteTerminal(tokenAct,List.of("dot"));
                 llamadaMetodoF();
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
     }

     /**
      * <Llamada-ConclassorF2 >::= <Encadenado>  | lambda
      */

     public void llamadaConclassorF2() {

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

     }

     /**
      * <Lista-Expresiones>::= <Expresion>  <Lista-ExpresionesF>
      */

     public void listaExpresiones() {

     }

     /**
      * <Lista-ExpresionesF> ::= comma <Lista-Expresiones> | lambda
      */

     public void listaExpresionesF() {

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
      * <EncadenadoF> ::= id <EncadenadoF2>
      */

     public void encadenadoF() {

     }

     /**
      * <EncadenadoF2> ::= <Argumentos-Actuales> <Llamada-Metodo-EncadenadoF> | <Acceso-Variable-EncadenadoF>
      */

     public void encadenadoF2() {

     }

     /**
      * <Llamada-Metodo-EncadenadoF> ::= <Encadenado>  | lambda
      */

     public void llamadaMetodoEncadenadoF() {

     }

     /**
      * <Acceso-Variable-EncadenadoF> ::= openBracket <Expresion> closeBracket <Acceso-Variable-EncadenadoF2> | <Encadenado>  | lambda
      */

     public void accesoVariableEncadenadoF() {

     }

     /**
      * <Acceso-Variable-EncadenadoF2> ::= <Encadenado>  | lambda
      */
     public void accesoVariableEncadenadoF2(){

     }






 }
