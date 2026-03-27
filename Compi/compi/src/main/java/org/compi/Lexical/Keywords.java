package org.compi.Lexical;

/**
 * Enum que representa las palabras clave del lenguaje en el análisis léxico.
 * @author Enzo Palau
 */

public enum Keywords {
    pclass ,
    pimpl,
    pelse,
    pfalse ,
    pif,
    pret,
    pwhile,
    ptrue ,
    pnil ,
    pnew,
    pfn,
    pst,
    ppub,
    pself ,
    pdiv,
    pfor,
    pin,
    pstart,
    pstr,
    pvoid,
    pint,
    pbool,
    parray;



    // constantes para las palabras clave para comparar con los tokens
    public static final String PCLASS   = "class";
    public static final String PIMPL    = "impl";
    public static final String PELSE    = "else";
    public static final String PFALSE   = "false";
    public static final String PIF      = "if";
    public static final String PRET     = "ret";
    public static final String PWHILE   = "while";
    public static final String PTRUE    = "true";
    public static final String PNIL     = "nil";
    public static final String PNEW     = "new";
    public static final String PFN      = "fn";
    public static final String PST      = "st";
    public static final String PPUB     = "pub";
    public static final String PSELF    = "self";
    public static final String PDIV     = "div";
    public static final String PFOR     = "for";
    public static final String PIN      = "in";
    public static final String PSTART   = "start";
    public static final String PSTR     = "str";
    public static final String PVOID    = "void";
    public static final String PINT     = "int";
    public static final String PBOOL    = "bool";

}
