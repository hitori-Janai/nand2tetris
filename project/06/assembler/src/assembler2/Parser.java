package assembler2;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Parser
 */
public class Parser {

    /**
     * 所有的汇编指令 去除注释和空格
     */
    public String[] arrCommand;

    /**
     * arrCommand size
     */
    public int arrsize;

    /**
     * current command index
     */
    public int curIndex = 0;
    public String curCommand;

    /**
     * SymbolTable
     */
    public SymbolTable symbolTable;

    /**
     * 指令类型
     */
    public static enum CommandType {
        A_COMMAND, C_COMMAND, L_COMMAND
    }

    /**
     * 输入文件或流,为语法解析做准备
     * 
     * @param path
     * @throws IOException
     */
    public Parser(String path, SymbolTable symTable) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line;
        symbolTable = symTable;
        List<String> list = new ArrayList<String>();
        while (null != (line = in.readLine())) {
            line = line.replaceAll(" ", "");// 去空格
            if (line.equals("") || line.startsWith("//")) {
                continue;
            }
            // command and comment in one line
            if (line.indexOf("//") > 0) {
                line = line.substring(0, line.indexOf("//"));
            }
            // L_commandtype
            if (line.startsWith("(") && line.endsWith(")")) {
                symbolTable.addEntry(line.substring(1, line.length() - 1), curIndex);
                continue;
            }

            curIndex++; //point to the next command
            list.add(line);
        }
        arrsize = list.size();
        arrCommand = (String[]) list.toArray(new String[arrsize]);// 命令全放在数组里,方便啊
        in.close();

        curIndex = 0;//recover
    }

    public boolean hasMoreCommands() {
        return arrsize - curIndex > 0;
    }

    /**
     * 载入下一条汇编命令
     */
    public void advance() {
        curCommand = arrCommand[curIndex++];
    }

    /**
     * 返回当前命令的类型
     * 
     * @return
     */
    public CommandType commandType() {
        if (curCommand.startsWith("@")) {
            return CommandType.A_COMMAND;
        }

        if (curCommand.indexOf("=") > 0 || curCommand.indexOf(";") > 0) {
            return CommandType.C_COMMAND;
        }

        if (curCommand.startsWith("(") && curCommand.endsWith(")")) {
            return CommandType.L_COMMAND;
        }
        throw new RuntimeException("unknow command '" + curCommand + "'");
    }

    /**
     * when A/L_commandtype.
     * 
     * @return
     */
    public String symbol() {
        // A_commandtype
        if (curCommand.startsWith("@")) {
            // return Code.get2binary(curCommand.substring(1));
            return curCommand.substring(1);
        }

        // L_commandtype
        return curCommand.substring(1, curCommand.length() - 1);
    }

    /**
     * dest=comp
     * 
     * @return
     */
    public String dest() {
        int index = curCommand.indexOf("=");
        if (index > 0) {
            return curCommand.substring(0, index);
        }
        return "null";
    }

    /**
     * dest=comp;jmp
     * 
     * @return
     */
    public String comp() {
        int indexEqu = curCommand.indexOf("=");
        int indexSem = curCommand.indexOf(";");
        int start = indexEqu > 0 ? indexEqu + 1 : 0;
        int end = indexSem > 0 ? indexSem : curCommand.length();

        return curCommand.substring(start, end);
    }

    /**
     * comp;jmp
     * 
     * @return
     */
    public String jump() {
        int index = curCommand.indexOf(";");
        if (index > 0) {
            return curCommand.substring(index + 1, curCommand.length());
        }
        return "null";
    }

    public static boolean isNumeric(String str) {
        Pattern pa = Pattern.compile("[0-9]*");
        return pa.matcher(str).matches();
    }

}