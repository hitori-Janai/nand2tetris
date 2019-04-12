package jackcompiler;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * JackTokenizer
 */
public class JackTokenizer {
    /** 字元类型 */
    public static enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST;
        @Override
		public String toString() {
			String name = this.name().toLowerCase();
			if (INT_CONST.equals(this)) {
				name = "integerConstant";
			} else if (STRING_CONST.equals(this)) {
				name = "stringConstant";
			}
			return name;
		}
    }

    /** 关键字 */
    public static final String[] KEYWORDS = { "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while",
            "return" };
    /** SYMBOLS */
    public static final String[] SYMBOLS = { "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|",
            "<", ">", "=", "~" };

    public String[] arrToken;
    public int arrsize;

    /**nextTokenIndex */
    public int curIndex = 0;
    public String curToken;

    public JackTokenizer(String path) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line;
        boolean isMultLine = false;
        List<String> list = new ArrayList<String>();
        while (null != (line = in.readLine())) {
            if (line.indexOf("//") >= 0) { // 去注释
                line = line.substring(0, line.indexOf("//"));
            }
            if (!isMultLine && line.indexOf("/*") >= 0) {
                isMultLine = true;
            }
            if (isMultLine && line.indexOf("*/") >= 0) {
                isMultLine = false;
                continue;
            }
            if (isMultLine) {
                continue;
            }
            line = line.trim();// 去除首尾空格
            if (line.equals("")) {// 跳过空行
                continue;
            }
            // System.out.println("line: " + line);
            /** 纯代码行,去除注释后 提取token */
            String patternInt = "1234567890";
            String patternString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
            String patternSymbol = "{}()[],.;+-*/&|<>=~";
            int length = line.length();
            for (int i = 0, result = 0; i < length; i++) {
                /** 提取字符串 */
                result = matchString(line, i, length);
                if (result > i) {
                    // System.out.println(line.substring(i, result));
                    list.add(line.substring(i+1, result-1));
                    i = --result;
                    continue;
                }
                /** 提取<= >=多字符符号 */
                result = match(">=<", line, i, length);
                if (result > i) {
                    // System.out.println(line.substring(i, result));
                    list.add(line.substring(i, result));
                    i = --result;
                    continue;
                }
                /** 提取单字符符号 */
                if (isInString(line.charAt(i), patternSymbol)) {
                    // System.out.println(line.charAt(i));
                    list.add(String.valueOf(line.charAt(i)));
                }
                /** 提取数字 */
                result = match(patternInt, line, i, length);
                if (result > i) {
                    // System.out.println(line.substring(i, result));
                    list.add(line.substring(i, result));
                    i = --result;
                    continue;
                }
                /** 提取标识符 */
                result = match(patternString, line, i, length);
                if (result > i) {
                    // System.out.println(line.substring(i, result));
                    list.add(line.substring(i, result));
                    i = --result;
                }
            }

        }
        arrsize = list.size();
        arrToken = (String[]) list.toArray(new String[arrsize]);// 命令全放在数组里,方便啊
        in.close();
    }

    private static int matchString(String context, int start, int end) {
        if (context.charAt(start) != '"') {
            return start;
        }
        while (++start < end && context.charAt(start) != '"') {
        }
        return ++start;
    }

    private static int match(String pattern, String context, int start, int end) {
        while (start < end && isInString(context.charAt(start), pattern)) {
            start++;
        }
        return start;
    }

    private static boolean isInString(char a, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == a) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMoreTokens() {
        return arrsize - curIndex > 0;
    }

    public void advance() {
        if (!hasMoreTokens()) {
			throw new RuntimeException(
					"Current index is already at the end of the token array!");
		}
        curToken = arrToken[curIndex++];
    }

    /**预览下一个token */
    public String previewNextToken(){
        if (hasMoreTokens()) {
            return arrToken[curIndex];
        }
        return null;
    }
    public TokenType tokenType() {
        //关键字
        for (String var : KEYWORDS) {
            if (var.equals(curToken)) {
                return TokenType.KEYWORD;
            }
        }
        //符号
        for (String var : SYMBOLS) {
            if (var.equals(curToken)) {
                return TokenType.SYMBOL;
            }
        }
        // 标识符 正则表达式匹配标识符,字母 数字 下划线
        String IDENTIFIER = "^[a-zA-Z_]{1}[a-zA-Z0-9_]*";
        Pattern pa = Pattern.compile(IDENTIFIER);
        if (pa.matcher(curToken).matches()) {
            return TokenType.IDENTIFIER;
        }
        //数字
        Pattern paInt = Pattern.compile("[0-9]*");
        if(paInt.matcher(curToken).matches()){
            return TokenType.INT_CONST;
        }
        //字符常量
        // if (curToken.charAt(0)=='"') {}
        return TokenType.STRING_CONST;
        

        // throw new RuntimeException("unknow Token : " + curToken);
    }

    /** 返回预定义关键字.仅{@code tokenType()}返回{@code KEYWORD} */
    public String keyword() {
        return curToken;
    }

    /** 返回symbol 符号.仅{@code tokenType()}返回{@code SYMBOL} */
    public String symbol() {
        return curToken;
    }

    /** 字母下划线+数字组成的.仅{@code tokenType()}返回{@code IDENTIFIER} */
    public String identifier() {
        return curToken;
    }

    /** intval 整数常量.仅{@code tokenType()}返回{@code INT_CONST} */
    public int intval() {
        return Integer.parseInt(curToken);
    }

    /** 返回stringVal 字符串常量.仅{@code tokenType()}返回{@code STRING_CONST} */
    public String stringVal() {
        return curToken;
    }
}