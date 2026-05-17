package org.compi.Semantic.SymbolTable;
import org.compi.Semantic.SymbolTable.SemanticExceptions;
import org.compi.Lexical.token;
import java.util.*;
public class Method {
    //tengo que guardar HTParam, tipo retorno, es estático, HTVar, token, nombre
    private String name;
    private token token;
    private boolean isStatic;
    private Type returnT;
    private Hashtable<String,Parameter> parameterHashtable = new Hashtable<>();
    private Hashtable<String,Variable> variableHashtable =new Hashtable<>();

    public Method(Type type,token token, Boolean isStatic){
        this.token=token;
        this.name=token.getLexeme();
        this.isStatic=isStatic;
        this.returnT=type;
    }

}
