package vmtranslator2;

import java.io.*;

/**
 * VMtranslator
 */
public class VMtranslator {

    public static void main(String[] args) throws IOException {
        String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\07\\StackArithmetic\\SimpleAdd\\SimpleAdd.vm";
        inpath = args[0];

        String outpath = inpath.substring(0, inpath.lastIndexOf(".")) + ".asm";
        delFile(outpath);

        Parser p = new Parser(inpath);
        CodeWriter cw = new CodeWriter(outpath);
        while (p.hasMoreCommands()) {
            p.advance();
            switch (p.commandType()) {
            case Parser.TYPE_PUSH:
            case Parser.TYPE_POP:
                System.out.println(p.curCommand + "\t:memory");
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
                System.out.println(p.curCommand + "\t\t:arithmetic");
                cw.writerArithmetic(p.curCommand);
                break;
            default:
                throw new RuntimeException("unknow command : " + p.curCommand + "'");
            }
        }
        cw.close();
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile())
            file.delete();
    }

}