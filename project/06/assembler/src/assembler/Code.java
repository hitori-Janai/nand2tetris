package assembler;

import java.util.HashMap;

/**
 * Hack 汇编语言(助记符)C指令转成二进制码
 */
public class Code {

    /**
     * C指令前缀字符串
     */
    public static final String C_PRIFIX = "111";

    /**
     * dest助记符转二进制码翻译表
     */
    public static HashMap<String, String> destTable;

    /**
     * comp助记符转二进制码翻译表
     */
    public static HashMap<String, String> compTable;

    /**
     * jump助记符转二进制码翻译表
     */
    public static HashMap<String, String> jumpTable;

    /**
     * 初始化码表
     */
    static {
        destTable = new HashMap<String, String>();
        compTable = new HashMap<String, String>();
        jumpTable = new HashMap<String, String>();

        destTable.put("null", "000");
        destTable.put("M", "001");
        destTable.put("D", "010");
        destTable.put("MD", "011");
        destTable.put("A", "100");
        destTable.put("AM", "101");
        destTable.put("AD", "110");
        destTable.put("AMD", "111");

        compTable.put("0", "0101010");
        compTable.put("1", "0111111");
        compTable.put("-1", "0111010");
        compTable.put("D", "0001100");
        compTable.put("A", "0110000");
        compTable.put("!D", "0001101");
        compTable.put("!A", "0110001");
        compTable.put("-D", "0001111");
        compTable.put("-A", "0110011");
        compTable.put("D+1", "0011111");
        compTable.put("A+1", "0110111");
        compTable.put("D-1", "0001110");
        compTable.put("A-1", "0110010");
        compTable.put("D+A", "0000010");
        compTable.put("D-A", "0010011");
        compTable.put("A-D", "0000111");
        compTable.put("D&A", "0000000");
        compTable.put("D|A", "0010101");
        compTable.put("M", "1110000");
        compTable.put("!M", "1110001");
        compTable.put("-M", "1110011");
        compTable.put("M+1", "1110111");
        compTable.put("M-1", "1110010");
        compTable.put("D+M", "1000010");
        compTable.put("D-M", "1010011");
        compTable.put("M-D", "1000111");
        compTable.put("D&M", "1000000");
        compTable.put("D|M", "1010101");

        jumpTable.put("null", "000");
        jumpTable.put("JGT", "001");
        jumpTable.put("JEQ", "010");
        jumpTable.put("JGE", "011");
        jumpTable.put("JLT", "100");
        jumpTable.put("JNE", "101");
        jumpTable.put("JLE", "110");
        jumpTable.put("JMP", "111");
    }

    public static String dest(String mnemonic) {
        String ret = destTable.get(mnemonic);
        if (ret == null) {
            throw new RuntimeException("Can not find dest expresstion '" + mnemonic + "'");
        }
        return ret;
    }

    public static String comp(String mnemonic) {
        String ret = compTable.get(mnemonic);
        if (ret == null) {
            throw new RuntimeException("Can not find comp expresstion '" + mnemonic + "'");
        }
        return ret;
    }

    public static String jump(String mnemonic) {
        String ret = jumpTable.get(mnemonic);
        if (ret == null) {
            throw new RuntimeException("Can not find jump expresstion '" + mnemonic + "'");
        }
        return ret;
    }

    /**
     * 转换成16b 二进制数字符串
     * 
     * @param address 10进制数 字符串
     * @return
     */
    public static String get2binary(String address) {

        String biStr = Integer.toBinaryString(Integer.parseInt(address));
        StringBuffer strB = new StringBuffer();
        for (int i = 0; i < 16 - biStr.length(); i++) {
            strB.append('0');
        }
        return strB.append(biStr).toString();
    }
}