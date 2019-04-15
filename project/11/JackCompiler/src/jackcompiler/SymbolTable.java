package jackcompiler;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * SymbolTabel
 */
public class SymbolTable {

    /** name, (type,kind,index) */
    private HashMap<String, Object[]> classTable;
    /** name, (type,kind,index) */
    private HashMap<String, Object[]> subroutineTable;

    private int indexLableWhile = 0;
    private int indexLableIf = 0;
    public boolean isMethod = false; 

    public static enum Kind {
        STATIC, FIELD, ARG, VAR, NONE;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static Kind getKind(String k){
            if (k.equals("static")) {
                return Kind.STATIC;
            }
            if (k.equals("field")) {
                return Kind.FIELD;
            }
            throw new RuntimeException("illegal string : " + k);
        }
    }

    public SymbolTable() {
        classTable = new LinkedHashMap<String, Object[]>();
        subroutineTable = new LinkedHashMap<String, Object[]>();
        isMethod = false;
    }

    /** 子程序符号表重置 */
    public void startSubroutine() {
        subroutineTable.clear();
        indexLableWhile = 0;
        indexLableIf = 0;
    }

    /** 定义新标识符 */
    public void define(String name, String type, Kind k) {
        if (k.toString().matches("static|field")) {
            if (classTable.get(name) != null) {
                throw new RuntimeException("duplicate variable" + name);
            }
            classTable.put(name, new Object[] { type, k, varCount(k) });
        } else if (!k.toString().matches("none")) {
            subroutineTable.put(name, new Object[] { type, k, varCount(k) });
        }
    }

    /** 返回当前作用域 k的数量,可以作为define的index,以0为起始 */
    public int varCount(Kind k) {
        int count = 0;
        HashMap<String, Object[]> hMap = null;
        if (k.toString().matches("static|field")) {
            hMap = classTable;
        } else if (k.toString().matches("none")) {
            return count;
        } else {
            hMap = subroutineTable;
        }

        for (String var : hMap.keySet()) {
            if (hMap.get(var)[1].equals(k)) {
                count++;
            }
        }
        return count;
    }

    /** 返回当前标识符的种类,未知为NONE */
    public Kind kindOf(String name) {
        if (subroutineTable.containsKey(name)) {
            return (Kind) subroutineTable.get(name)[1];
        }
        if (classTable.containsKey(name)) {
            return (Kind) classTable.get(name)[1];
        }
        return Kind.NONE;
    }

    /** 当前标识符的类型 */
    public String typeOf(String name) {

        if (subroutineTable.containsKey(name)) {
            return (String) subroutineTable.get(name)[0];
        }
        if (classTable.containsKey(name)) {
            return (String) classTable.get(name)[0];
        }

        // throw new RuntimeException("unknow type of variable " + name);
        return null;
    }

    /** 标识符索引 */
    public int indexOf(String name) {
        int hasThis = 0;
        if (subroutineTable.containsKey(name)) {
            if (isMethod && this.kindOf(name).toString().equals("arg")) {
                hasThis = 1;
            }
            return (int) subroutineTable.get(name)[2] + hasThis;
        }
        if (classTable.containsKey(name)) {
                return (int) classTable.get(name)[2];
        }

        // throw new RuntimeException("unknow index of variable " + name);
        return -1;
    }

    public String getWhileLable(){
        return "WHILE_EXP" + indexLableWhile++;
    }

    public String getWhileEndLable(String whileLable){
        return "WHILE_END" + whileLable.substring(whileLable.indexOf("P")+1);
    }
    
    public String getIfTrue(){
        return "IF_TRUE" + indexLableIf++;
    }

    public String getIfFalse(String trueLable){
        return "IF_FALSE" + trueLable.substring(trueLable.indexOf("E")+1);
    }

    public String getIfEnd(String trueLable){
        return "IF_END" + trueLable.substring(trueLable.indexOf("E")+1);
    }

}