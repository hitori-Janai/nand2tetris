package vmtranslator;

import java.io.*;
import java.util.*;

/**
 * VMtranslator
 */
public class VMtranslator {

    public static void main(String[] args) throws IOException {
        String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\08\\FunctionCalls\\FibonacciElement";
        inpath = args[0];
        List<String>  inoutBuff = checkIn(inpath);
        String outpath = inoutBuff.get(0);
        delFile(outpath);
        CodeWriter cw = new CodeWriter(outpath);

        for (int i = 2; i < inoutBuff.size(); i++) {
                if (inoutBuff.get(i).lastIndexOf("Sys.vm") > 0) {
                    cw.writerInit();
                }
            }
        for (int i = 1; i < inoutBuff.size(); i++) {
            Parser p = new Parser(inoutBuff.get(i));
            write(cw, p);
        }
        
        cw.close();
    }

    public static void write(CodeWriter cw,Parser p) throws IOException {
        while (p.hasMoreCommands()) {
            p.advance();
            switch (p.commandType()) {
            case Parser.TYPE_PUSH:
            case Parser.TYPE_POP:
                // System.out.println(p.curCommand + "\t:memory");
                cw.WriterPushPop(p.commandType(), p.arg1(), p.Arg2());
                break;
            case Parser.TYPE_ARI_LOG_ADD:
            case Parser.TYPE_ARI_LOG_SUB:
            case Parser.TYPE_ARI_LOG_NEG:
            case Parser.TYPE_ARI_LOG_EQ:
            case Parser.TYPE_ARI_LOG_GT:
            case Parser.TYPE_ARI_LOG_LT:
            case Parser.TYPE_ARI_LOG_AND:
            case Parser.TYPE_ARI_LOG_OR:
            case Parser.TYPE_ARI_LOG_NOT:
                // System.out.println(p.curCommand + "\t\t:arithmetic");
                cw.writerArithmetic(p.curCommand);
                break;
            case Parser.TYPE_FLO_LABEL:
                cw.writerLabel(p.arg1());
                break;
            case Parser.TYPE_FLO_GOTO:
                cw.writerGoto(p.arg1());
                break;
            case Parser.TYPE_FLO_IFGOTO:
                cw.writerIf(p.arg1());
                break;
            case Parser.TYPE_FUN_FUNC:
                cw.writerFunction(p.arg1(),p.Arg2());
                break;
            case Parser.TYPE_FUN_RETU:
                cw.writerReturn();
                break;
            case Parser.TYPE_FUN_CALL:
                cw.writerCall(p.arg1(), p.Arg2());
                break;
            default:
                throw new RuntimeException("unknow command : " + p.curCommand + "'");
            }
        }
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile())
            file.delete();
    }

    public static List<String> checkIn(String inpath){
        List<String> list = new ArrayList<String>();
        File file = new File(inpath);

        if (file.isDirectory()) {
            list.add(inpath + "\\" + file.getName() + ".asm");
            FileFilter filter = new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().lastIndexOf(".vm") > 0;
				}
            };
            for (File var : file.listFiles(filter)) {
                list.add(var.getAbsolutePath());
            }
            if(list.size() < 2){
                throw new RuntimeException("There arent any .vm files in Directory");
            }
            // for (int i = 2; i < list.size(); i++) {
            //     if (list.get(i).lastIndexOf("Sys.vm") > 0) {
            //         list.add(1,list.remove(i));
            //     }
            // }
        }else{
            list.add(inpath.substring(0, inpath.lastIndexOf(".")) + ".asm");
            list.add(inpath);
        }
        
        return list;
    }

}