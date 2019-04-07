package vmtranslator;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 拆分一行命令
 */
public class Parser {
    // 算术逻辑命令 9个
    public static final String[] TYPE_ARI_LOG = { "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not" };
    public static final String TYPE_ARI_LOG_ADD = "add";
    public static final String TYPE_ARI_LOG_SUB = "sub";
    public static final String TYPE_ARI_LOG_NEG = "neg";
    public static final String TYPE_ARI_LOG_EQ = "eq";
    public static final String TYPE_ARI_LOG_GT = "gt";
    public static final String TYPE_ARI_LOG_LT = "lt";
    public static final String TYPE_ARI_LOG_AND = "and";
    public static final String TYPE_ARI_LOG_OR = "or";
    public static final String TYPE_ARI_LOG_NOT = "not";

    // 存储器存取命令 2个
    public static final String TYPE_PUSH = "push";
    public static final String TYPE_POP = "pop";

    // segments 8个
    public static final String[] TYPE_SEGMENTS = { "argument", "local", "static", "constant", "this", "that", "pointer","temp" };
    public static final String TYPE_SEG_ARGU = "argument";
    public static final String TYPE_SEG_LOCA = "local";
    public static final String TYPE_SEG_STAT = "static";
    public static final String TYPE_SEG_CONS = "constant";
    public static final String TYPE_SEG_THIS = "this";
    public static final String TYPE_SEG_THAT = "that";
    public static final String TYPE_SEG_POIN = "pointer";
    public static final String TYPE_SEG_TEMP = "temp";
    
    // 程序流程控制
    public static final String TYPE_FLO_LABEL = "label";
    public static final String TYPE_FLO_GOTO = "goto";
    public static final String TYPE_FLO_IFGOTO = "if-goto";
    
    // 函数调用命令
    public static final String TYPE_FUN_FUNC = "function";
    public static final String TYPE_FUN_RETU = "return";
    public static final String TYPE_FUN_CALL = "call";

    /**
     * 所有的汇编指令 去除注释和多余空格
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

    public Parser(String path) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line;
        List<String> list = new ArrayList<String>();
        while (null != (line = in.readLine())) {
            if (line.lastIndexOf("//") >= 0) { // 去注释
                line = line.substring(0, line.lastIndexOf("//"));
            }
            line = line.trim();// 去除首尾空格
            if (line.equals("")) {// 跳过空行
                continue;
            }
            line = line.replaceAll("\\s+", " ");// 删除多余空格

            list.add(line);
        }
        arrsize = list.size();
        arrCommand = (String[]) list.toArray(new String[arrsize]);// 命令全放在数组里,方便啊
        in.close();

    }

    public boolean hasMoreCommands() {
        return arrsize - curIndex > 0;
    }

    public void advance() {
        curCommand = arrCommand[curIndex++];
    }

    public String commandType() {
        String command = curCommand.split(" ")[0];
        return command;

    }

    /**
     * 返回第一个参数 当arithmetic,返回命令本身 return 不能调用此函数
     * 
     * @return
     */
    public String arg1() {
        //用不到这段吧
        for (String var : TYPE_ARI_LOG) {
            if (curCommand.split(" ")[0].equals(var))
                return curCommand.split(" ")[0];
        }

        return curCommand.split(" ")[1];
    }

    /**
     * 返回第二个参数 仅当push pop function call.
     * 第二个参数不为数字,throw exception
     * @return curcommand 的第二个参数
     */
    public int Arg2() {
        String args2 = curCommand.split(" ")[2];
        if (!isNumeric(args2)) {
            throw new RuntimeException("unknow Arg2 '" + args2 + "'");
        }
        return Integer.parseInt(args2);
    }

    public static boolean isNumeric(String str) {
        Pattern pa = Pattern.compile("[0-9]*");
        return pa.matcher(str).matches();
    }
}