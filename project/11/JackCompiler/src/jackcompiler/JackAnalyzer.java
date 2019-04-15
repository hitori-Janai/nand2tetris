package jackcompiler;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * JackAnalyzer
 */
public class JackAnalyzer {

    public static void main(String[] args) throws IOException {
        // String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\10\\ArrayTest\\Main.jack";
        // String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\10\\ExpressionLessSquare\\SquareGame.jack";
        // inpath = args[0];
        
        // JackTokenizer jk = new JackTokenizer(inpath);        
        // String outpath = inpath.substring(0, inpath.lastIndexOf(".jack")) + "T_.xml";
        // write(testTokenizer(jk), outpath);
        
        // String outpath = inpath.substring(0, inpath.lastIndexOf(".jack")) + "_.xml";
        // new CompilationEngine(jk).writeXML(outpath);
        
        String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\10\\ArrayTest";
        handleDir(inpath);
    }

    public static void handleDir(String inpath) throws IOException {
        File file = new File(inpath);

        if (file.isDirectory()) {
            FileFilter filter = new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().lastIndexOf(".jack") > 0;
				}
            };
            
            for (File var : file.listFiles(filter)) {
                inpath = var.getAbsolutePath();
                JackTokenizer jk = new JackTokenizer(inpath);
                String outpath = inpath.substring(0, inpath.lastIndexOf(".jack")) + "_.xml";
                new CompilationEngine(jk).writeXML(outpath);
            }
        }
    }

    public static Document testTokenizer(JackTokenizer jk) {
        Document doc = DocumentHelper.createDocument();
        Element token_root = doc.addElement("tokens") ;
        Element node = null;
        while (jk.hasMoreTokens()) {
            jk.advance();
            switch (jk.tokenType()) {
            case KEYWORD:
                System.out.println("keyword: " + jk.curToken);
                node = token_root.addElement("keyword");
                break;
            case SYMBOL:
                System.out.println("symbol: " + jk.curToken);
                node = token_root.addElement("symbol");
                break;
            case IDENTIFIER:
                System.out.println("identifier: " + jk.curToken);
                node = token_root.addElement("identifier");
                break;
            case INT_CONST:
                System.out.println("int_const: " + jk.curToken);
                node = token_root.addElement("integerConstant");
                break;
            case STRING_CONST:
                System.out.println("string_const: " + jk.curToken);
                node = token_root.addElement("stringConstant");
                break;
            }
            node.addText(jk.curToken);
        }
        return doc;
    }

    public static void write(Document doc,String outpath) throws IOException {
        File file = new File(outpath);
        if (file.exists() && file.isFile())
            file.delete();
        XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
        FileOutputStream fos = new FileOutputStream(outpath);
        writer.setOutputStream(fos);

        writer.write(doc);
        System.out.println("写出完毕!");
        writer.close();
    }
}