package org.compi.Semantic.SymbolTable;
import org.compi.Lexical.token;
import org.compi.Semantic.SymbolTable.*;
public class Type {
    //guardar el token, nombre
    private token token;
    private String name;

    public Type(token token){
        this.token=token;
        this.name=token.getType();
    }


}
