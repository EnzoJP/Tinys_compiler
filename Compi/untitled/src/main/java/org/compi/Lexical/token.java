package org.compi.Lexical;

public class token {
    private String type;
    private String lexeme;
    private int line;
    private int column;

    public token(String type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }


    
}
