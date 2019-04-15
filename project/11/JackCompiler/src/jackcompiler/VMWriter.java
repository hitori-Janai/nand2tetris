package jackcompiler;

import java.io.*;

/**
 * VMWriter
 */
public class VMWriter {

    private BufferedWriter out;

    public VMWriter(String outpath) throws UnsupportedEncodingException, FileNotFoundException {
        File file = new File(outpath);
        if (file.exists() && file.isFile())
            file.delete();
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath), "UTF-8"));
    }

    public void writePush(Segment seg, int index) throws IOException {
        out.write("push " + seg + " " + index + "\n");
    }

    public void writePop(Segment seg, int index) throws IOException {
        out.write("pop " + seg + " " + index + "\n");
    }

    public void writeArithmetic(Command cd) throws IOException {
        if (Command.MUT.equals(cd)) {
            writeCall("Math.multiply", 2);
        } else if (Command.DIV.equals(cd)) {
            writeCall("Math.divide", 2);
        } else {
            out.write(cd + "\n");
        }       
    }

    public void writeLabel(String label) throws IOException {
        out.write("label " + label + "\n");
    }

    public void writeGoto(String label) throws IOException {
        out.write("goto " + label + "\n");
    }

    /** if-goto */
    public void writeIf(String label) throws IOException {
        out.write("if-goto " + label + "\n");
    }

    public void writeCall(String name, int nAgrs) throws IOException {
        out.write("call " + name + " " + nAgrs + "\n");
    }

    public void writeFunction(String name, int nAgrs) throws IOException {
        out.write("function " + name + " " + nAgrs + "\n");
    }

    public void writeReturn() throws IOException {
        out.write("return" + "\n");
    }

    public void close() throws IOException {
        out.close();
    }
}

enum Segment {
    CONST("constant"), ARG("argument"), LOCAL("local"), STATIC("static"), THIS("this"), THAT("that"),
    POINTER("pointer"), TEMP("temp");

    private String value;

    private Segment(final String str) {
        value = str;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Segment getSegment(SymbolTable.Kind k){
        Segment seg = null;
        if (k.toString().equals("static")) {
            seg = Segment.STATIC;
        }else if (k.toString().equals("var")) {
            seg = Segment.LOCAL;
        }else if (k.toString().equals("field")) {
            seg = Segment.THIS;
        }else if (k.toString().equals("arg")){
            seg = Segment.ARG;
        }
        return seg;
    }
}

enum Command {
    ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT, MUT, DIV;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /** 二元操作符 + - * / > < = & | */
    public static Command getCommand(String op) {
        Command ope = null;
        if (op.equals("+")) {
            ope = Command.ADD;
        } else if (op.equals("-")) {
            ope = Command.SUB;
        } else if (op.equals("*")) {
            ope = Command.MUT;
        } else if (op.equals("/")) {
            ope = Command.DIV;
        } else if (op.equals(">")) {
            ope = Command.GT;
        } else if (op.equals("<")) {
            ope = Command.LT;
        } else if (op.equals("=")) {
            ope = Command.EQ;
        } else if (op.equals("&")) {
            ope = Command.AND;
        } else if (op.equals("|")) {
            ope = Command.OR;
        } else {
            throw new RuntimeException("ERROR:illegal operator!!!");
        }
        return ope;
    }
}