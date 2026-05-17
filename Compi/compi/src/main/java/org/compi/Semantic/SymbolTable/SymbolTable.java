package org.compi.Semantic.SymbolTable;
import java.util.*;
import org.compi.Semantic.SymbolTable.Class;
public class SymbolTable {
    //tengo q guardar HTClases, MetActual, ClaseActual y Start
    private static Hashtable<String,Class> classHashtable = new Hashtable<>();
    private static Method currentMethod;
    private static Class currentClass;
    private static Method startMethod;

   public static void addClass(Class newClass) throws SemanticExceptions{
       //Una clase puede declararse muchas veces, no debe dar error si ya esta declarada
       Class clase = SymbolTable.classHashtable.get(newClass.getName());
       if(clase==null){
           classHashtable.put(newClass.getName(),newClass);
       }
   }

   public static void addClass_OnlyImpl(Class newClass){
       Class clase = SymbolTable.classHashtable.get(newClass.getName());
       if(clase==null){
           newClass.setHasImpl(true);
           classHashtable.put(newClass.getName(), newClass);
       }else{
           clase.setHasImpl(true);
       }
   }
    public void resolveInheritance() throws SemanticExceptions {
        for (Class c : classHashtable.values()) {
            String ancestorName = c.getAncestorName();
            if (ancestorName != null) {
                Class ancestor = classHashtable.get(ancestorName);
                if (ancestor == null) {
                    throw new SemanticExceptions(
                            "La clase '" + ancestorName + "' no está declarada"
                    );
                }
                if (ancestor == c) {
                    throw new SemanticExceptions(
                            "La clase '" + c.getName() + "' no puede heredar de sí misma"
                    );
                }
                c.setAncestor(ancestor);
            }
        }
        for (Class c : classHashtable.values()) {
            checkCircularInheritance(c);
        }
   }
   public void checkCircularInheritance(Class c) throws SemanticExceptions{
       List<Class> visited=new ArrayList<>();
       Class current = c;
       while(current.getAncestor()!=null){
           Class ancestor = current.getAncestor();
           if(visited.contains(ancestor)){
               throw new SemanticExceptions("ERROR SEMANTICO DECLARACIONES: Herencia circular detectada en la clase "+c.getName());
           }
           visited.add(ancestor);
           current=ancestor;
       }

   }
   public static Class getCurrentClass(){
       return currentClass;
   }
   public static Hashtable<String,Class> getClassHashtable(){
       return classHashtable;
   }
   public static void setCurrentMethod(Method newMethod){
       SymbolTable.currentMethod=newMethod;
   }
   public static void setCurrentClass(Class newClass){
       SymbolTable.currentClass=newClass;
   }
}
