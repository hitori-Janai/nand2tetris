package assembler;

import java.io.*;

/**
 * Assembler
 */
public class Assembler {

    public static void main(String[] args) throws IOException {
        String inpath = "project/06/add/Add.asm";
        inpath = args[0];
        String outpath = inpath.substring(0,inpath.lastIndexOf(".")) + ".hack";
        delFile(outpath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath), "UTF-8"));

        Parser p = new Parser(inpath);
        while (p.hasMoreCommands()) {
            p.advance();
            switch (p.commandType()) {
            case A_COMMAND:
                System.out.println("A: " + p.curCommand);
                System.out.println(Code.get2binary(p.symbol()));

                out.write(Code.get2binary(p.symbol()) + "\n");
                break;
            case C_COMMAND:
                System.out.println("C: " + p.curCommand);
                String dest = Code.dest(p.dest());
                String comp = Code.comp(p.comp());
                String jump = Code.jump(p.jump());
                System.out.println("111" + comp + dest + jump);
                out.write("111" + comp + dest + jump + "\n");
                break;
            case L_COMMAND:
                System.out.println("L: " + p.curCommand);
                break;

            default:
                break;
            }

        }
        out.close();
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile())
            file.delete();
    }
}