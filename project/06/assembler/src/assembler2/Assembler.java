package assembler2;

import java.io.*;

/**
 * Assembler
 */
public class Assembler {

    public static void main(String[] args) throws IOException {
        String inpath = "project/06/max/Max.asm";
        inpath = args[0];
        String outpath = inpath.substring(0,inpath.lastIndexOf(".")) + ".hack";
        delFile(outpath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath), "UTF-8"));

        SymbolTable symbolTable = new SymbolTable();
        Parser p = new Parser(inpath,symbolTable);
        while (p.hasMoreCommands()) {
            p.advance();
            switch (p.commandType()) {
            case A_COMMAND:
                System.out.println("A: " + p.curCommand);                
                System.out.println(symbol2binary(p.symbol(),symbolTable));
                out.write(symbol2binary(p.symbol(),symbolTable) + "\n");
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
                System.out.println(symbol2binary(p.symbol(),symbolTable));
                out.write(symbol2binary(p.symbol(),symbolTable) + "\n");
                break;

            default:
                break;
            }
        }
        out.close();
    }

    /**
     * delete existing file 
     * @param path
     */
    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile())
            file.delete();
    }

    /**
     * A/L_command symbol
     * @param symbol
     * @return
     */
    public static String symbol2binary(String symbol,SymbolTable symbolTable){
        if(Parser.isNumeric(symbol)){
           return Code.get2binary(symbol);
        }
        symbolTable.addEntry(symbol);
        String address =  Integer.toString(symbolTable.getAddress(symbol));        
        return Code.get2binary(address);
    }
}