package org.compi.Semantic.SymbolTable;
import org.compi.Lexical.token;

import java.util.Hashtable;

public class Class {
    //una clase tiene nombre, HTatr, token (q tiene la pos), constructor, herencia, HTMet
    private String name;
    private token token;
    private Hashtable<String,Attribute> attributeHashtable = new Hashtable<>();
    private Constructor constructor;
    private Class ancestor;
    private String ancestorName;
    private Hashtable<String,Method> methodHashtable = new Hashtable<>();
    private boolean hasImpl = false;
    public Class (token token){
        this.name= token.getLexeme();
        this.token=token;
    }


    public String getName(){
        return this.name;
    }

    public void setHasImpl(boolean hasImpl) {
        this.hasImpl=hasImpl;
    }

    public void setAncestor(Class ancestor) {
        this.ancestor=ancestor;
    }
    public void  setAncestorName(String ancestorName){
        this.ancestorName=ancestorName;
    }

    public Class getAncestor() {
        return ancestor;
    }
    public String getAncestorName(){
        return this.ancestorName;
    }
    public token getToken(){
        return this.token;
    }
}
