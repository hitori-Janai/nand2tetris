package app;
import java.io.*;
import java.util.*;
/**
 * Test
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\08\\VMtranslator\\src\\app\\t.asm";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inpath), "UTF-8"));

        String line;
        List<String> list = new ArrayList<String>();
        while (null != (line = in.readLine())) {
            if (line.lastIndexOf("//") >= 0) { // 去注释
                line = line.substring(0, line.indexOf("//"));
            }
            line = line.trim();// 去除首尾空格
            if (line.equals("")) {// 跳过空行
                continue;
            }
            line = line.replaceAll("\\s+", "");// 删除多余空格
            System.err.print(line + "\\n");
            list.add(line);
        }
    }

}