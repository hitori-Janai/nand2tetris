package jackcompiler;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * CompilationEngine
 */
public class CompilationEngine {
    private JackTokenizer jk;
    private VMWriter vmw;
    private SymbolTable st;
    Document doc;

    private String className;

    public static enum ElementType {
        classVarDec, subroutineDec, parameterList, subroutineBody, varDec, statements, letStatement, doStatement,
        ifStatement, whileStatement, returnStatement, expressionList, expression, term;
    }

    /** 标识符 */
    public static final String IDENTIFIER = ".*";// "^[a-zA-Z_]{1}[a-zA-Z0-9_]*";

    public CompilationEngine(JackTokenizer jk) throws IOException {
        this.jk = jk;
        CompileClass();
    }

    public CompilationEngine(JackTokenizer jk, String outpath) throws IOException {
        this.jk = jk;
        this.vmw = new VMWriter(outpath);
        this.st = new SymbolTable();
        CompileClass();
        this.vmw.close();
    }

    /** 匹配下一个终节点并添加 */
    private String matchTermNode(String regex, Element node) {
        if (jk.hasMoreTokens()) {
            jk.advance();
        }
        if (!jk.curToken.matches(regex)) {
            throw new RuntimeException("error Token : " + jk.curToken + " regex :" + regex);
        }
        System.out.println(jk.curToken);
        node.addElement(jk.tokenType().toString()).addText(jk.curToken);
        return jk.curToken;
    }

    /**
     * ok,class className { classVarDec* subroutineDec* }
     */
    public void CompileClass() throws IOException {
        doc = DocumentHelper.createDocument();
        Element root = doc.addElement("class");
        // class 关键字
        matchTermNode("class", root);
        // classname 字母数字_
        className = matchTermNode(IDENTIFIER, root);
        // {
        matchTermNode("\\{", root);
        // classVarDec* subroutineDec*
        while (!jk.previewNextToken().matches("\\}")) {
            // classVarDec
            if (jk.previewNextToken().matches("field|static")) {
                Element node = root.addElement(ElementType.classVarDec.toString());
                CompileClassVarDex(node);
                continue;
            }
            // subroutineDec
            if (jk.previewNextToken().matches("constructor|function|method")) {
                st.startSubroutine();
                if (jk.previewNextToken().matches("method")) {
                    st.isMethod = true;
                }
                Element node = root.addElement(ElementType.subroutineDec.toString());
                CompileSubRoutine(node);
                continue;
            }
            throw new RuntimeException("ERROR:classVarDec* subroutineDec*");
        }
        // }
        matchTermNode("\\}", root);
    }

    /** ok,field|static type varName (,varName)* */
    public void CompileClassVarDex(Element node) {
        // field|static
        String k = matchTermNode("field|static", node);
        // type
        String type = matchTermNode(IDENTIFIER, node);
        // varName
        String varName = matchTermNode(IDENTIFIER, node);

        st.define(varName, type, SymbolTable.Kind.getKind(k));

        while (!jk.previewNextToken().matches(";")) {
            // ,
            matchTermNode(",", node);
            // varName
            varName = matchTermNode(IDENTIFIER, node);

            st.define(varName, type, SymbolTable.Kind.getKind(k));
        }
        // ;
        matchTermNode(";", node);
    }

    /***
     * ok , constructor|function|method type subroutineName(parameterList)
     * subroutineBody
     */
    public void CompileSubRoutine(Element node) throws IOException {
        // constructor|function|method
        String kindOfFun = matchTermNode("constructor|function|method", node);
        // type void int char boolean className
        matchTermNode(IDENTIFIER, node);
        // subroutineName
        String nameOfFun = matchTermNode(IDENTIFIER, node);
        // (
        matchTermNode("\\(", node);
        // parameterList
        Element parameterList = node.addElement(ElementType.parameterList.toString());
        if (!jk.previewNextToken().matches("\\)")) {
            CompileParameterList(parameterList);
        }
        // )
        matchTermNode("\\)", node);
        // subroutineBody
        Element subroutineBody = node.addElement(ElementType.subroutineBody.toString());
        // { varDec* statements }
        // {
        matchTermNode("\\{", subroutineBody);
        // varDec*
        while (!jk.previewNextToken().matches("\\}")) {
            if (jk.previewNextToken().matches("var")) {
                Element varDec = subroutineBody.addElement(ElementType.varDec.toString());
                CompileVarDec(varDec);
                continue;
            }
            break;
        }

        if (kindOfFun.matches("function")) {
            vmw.writeFunction(className + "." + nameOfFun, st.varCount(SymbolTable.Kind.VAR));
        } else if (kindOfFun.equals("constructor")) {
            vmw.writeFunction(className + "." + nameOfFun, st.varCount(SymbolTable.Kind.VAR));
            vmw.writePush(Segment.CONST, st.varCount(SymbolTable.Kind.FIELD));
            vmw.writeCall("Memory.alloc", 1);
            vmw.writePop(Segment.POINTER, 0);
        } else if (kindOfFun.matches("method")) {
            vmw.writeFunction(className + "." + nameOfFun, st.varCount(SymbolTable.Kind.VAR));
            vmw.writePush(Segment.ARG, 0);
            vmw.writePop(Segment.POINTER, 0);
        }
        // statements
        Element statements = subroutineBody.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }
        matchTermNode("\\}", subroutineBody);
    }

    /** useless ok , { varDec* statements } */
    @Deprecated
    public void CompileSubRoutineBody(Element node) throws IOException {
        // {
        matchTermNode("\\{", node);
        // varDec*
        while (!jk.previewNextToken().matches("\\}")) {
            if (jk.previewNextToken().matches("var")) {
                Element varDec = node.addElement(ElementType.varDec.toString());
                CompileVarDec(varDec);
                continue;
            }
            break;
        }
        // statements
        Element statements = node.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }
        matchTermNode("\\}", node);
    }

    /** ok , type varName [,varName]* */
    public void CompileParameterList(Element node) {
        // type
        String type = matchTermNode(IDENTIFIER, node);
        // varName
        String varName = matchTermNode(IDENTIFIER, node);
        st.define(varName, type, SymbolTable.Kind.ARG);
        while (!jk.previewNextToken().matches("\\)")) {
            // ,
            matchTermNode(",", node);
            // type
            type = matchTermNode(IDENTIFIER, node);
            // varName
            varName = matchTermNode(IDENTIFIER, node);
            st.define(varName, type, SymbolTable.Kind.ARG);
        }
    }

    /** ok , var type varName [,varName]* ; */
    public void CompileVarDec(Element node) {
        // var
        matchTermNode("var", node);
        // type
        String type = matchTermNode(IDENTIFIER, node);
        // varName
        String varName = matchTermNode(IDENTIFIER, node);
        st.define(varName, type, SymbolTable.Kind.VAR);
        while (!jk.previewNextToken().matches(";")) {
            // ,
            matchTermNode(",", node);
            // varName
            varName = matchTermNode(IDENTIFIER, node);
            st.define(varName, type, SymbolTable.Kind.VAR);
        }
        // ;
        matchTermNode(";", node);
    }

    /** ok 函数体里去除括号和变量声明部分. */
    public void CompileStatements(Element node) throws IOException {
        while (!jk.previewNextToken().matches("\\}")) {
            // letStatment
            if (jk.previewNextToken().matches("let")) {
                Element letStatment = node.addElement(ElementType.letStatement.toString());
                CompileLet(letStatment);
                continue;
            }
            // doStatment
            if (jk.previewNextToken().matches("do")) {
                Element doStatment = node.addElement(ElementType.doStatement.toString());
                CompileDo(doStatment);
                continue;
            }
            // ifStatment
            if (jk.previewNextToken().matches("if")) {
                Element ifStatment = node.addElement(ElementType.ifStatement.toString());
                CompileIf(ifStatment);
                continue;
            }
            // whileStatment
            if (jk.previewNextToken().matches("while")) {
                Element whileStatment = node.addElement(ElementType.whileStatement.toString());
                CompileWhile(whileStatment);
                continue;
            }
            // returnStatment
            if (jk.previewNextToken().matches("return")) {
                Element returnStatment = node.addElement(ElementType.returnStatement.toString());
                CompileReturn(returnStatment);
                continue;
            }
            throw new RuntimeException("ERROR:unknow statment");
        }

    }

    /**
     * ok , do (subroutineName | (className | varName).subroutineName)
     * (expressionList?)
     */
    public void CompileDo(Element node) throws IOException {
        int numArgOFCall = 0;

        // do
        matchTermNode("do", node);
        // subroutineName | (className | varName)
        String nameOfFun = matchTermNode(IDENTIFIER, node);
        if (jk.previewNextToken().matches("\\.")) {
            // .
            matchTermNode("\\.", node);
            // subroutineName
            nameOfFun = nameOfFun + "." + matchTermNode(IDENTIFIER, node);
        }

        // 若obj.method
        if (nameOfFun.contains(".")) {
            String obj = nameOfFun.substring(0, nameOfFun.indexOf("."));
            String type = st.typeOf(obj);
            if (type != null) {
                if (st.kindOf(obj).toString().matches("field")) {
                    // this.obj.method
                    vmw.writePush(Segment.THIS, st.indexOf(obj));

                } else if (st.kindOf(obj).toString().matches("var|arg|static")) {
                    vmw.writePush(Segment.getSegment(st.kindOf(obj)), st.indexOf(obj));
                }
                nameOfFun = type + "." + nameOfFun.substring(nameOfFun.indexOf(".") + 1);
                numArgOFCall++;
            }
        } else { // (this.)method
            vmw.writePush(Segment.POINTER, 0);
            nameOfFun = className + "." + nameOfFun;
            numArgOFCall++;
        }

        // (
        matchTermNode("\\(", node);
        // expressionList
        Element expressionList = node.addElement(ElementType.expressionList.toString());
        if (!jk.previewNextToken().matches("\\)")) {
            numArgOFCall = numArgOFCall + CompileExpressionList(expressionList);
        }
        // )
        matchTermNode("\\)", node);
        // ;
        matchTermNode(";", node);

        // 函数
        vmw.writeCall(nameOfFun, numArgOFCall);
        vmw.writePop(Segment.TEMP, 0);
    }

    /**
     * ok , let varName [expression]? = expression ;
     */
    public void CompileLet(Element node) throws IOException {
        boolean isArray = false;
        // let
        matchTermNode("let", node);
        // varName
        String var = matchTermNode(IDENTIFIER, node);
        // [expression] ?
        if (jk.previewNextToken().matches("\\[")) {
            // [
            matchTermNode("\\[", node);
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            // ]
            matchTermNode("\\]", node);

            isArray = true;
        }

        if (isArray) {
            vmw.writePush(Segment.getSegment(st.kindOf(var)), st.indexOf(var));
            vmw.writeArithmetic(Command.ADD);
        }

        // =
        matchTermNode("=", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // ;
        matchTermNode(";", node);

        // 编译 let 变量赋值
        if (!isArray) {
            vmw.writePop(Segment.getSegment(st.kindOf(var)), st.indexOf(var));
        } else {
            vmw.writePop(Segment.TEMP, 0);
            vmw.writePop(Segment.POINTER, 1);
            vmw.writePush(Segment.TEMP, 0);
            vmw.writePop(Segment.THAT, 0);
        }
    }

    /**
     * ok , while ( expression) { statements }
     */
    public void CompileWhile(Element node) throws IOException {
        String whileLable = st.getWhileLable();
        String whileEndLable = st.getWhileEndLable(whileLable);
        vmw.writeLabel(whileLable);

        // while
        matchTermNode("while", node);
        // (
        matchTermNode("\\(", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // )
        matchTermNode("\\)", node);

        vmw.writeArithmetic(Command.NOT);
        vmw.writeIf(whileEndLable);

        // {
        matchTermNode("\\{", node);
        // statements
        Element statements = node.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }
        matchTermNode("\\}", node);

        vmw.writeGoto(whileLable);
        vmw.writeLabel(whileEndLable);
    }

    /**
     * ok , return expression? ;
     */
    public void CompileReturn(Element node) throws IOException {
        // return
        matchTermNode("return", node);
        // expression?
        if (!jk.previewNextToken().matches(";")) {
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
        } else {
            vmw.writePush(Segment.CONST, 0);
        }
        // ;
        matchTermNode(";", node);

        vmw.writeReturn();
    }

    /**
     * ok , if (expression) {statments} else{statments}?
     */
    public void CompileIf(Element node) throws IOException {
        String trueLable = st.getIfTrue();
        String falseLable = st.getIfFalse(trueLable);
        String endLable = st.getIfEnd(trueLable);

        // if
        matchTermNode("if", node);
        // (
        matchTermNode("\\(", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // )
        matchTermNode("\\)", node);

        vmw.writeIf(trueLable);
        vmw.writeGoto(falseLable);
        vmw.writeLabel(trueLable);

        // {
        matchTermNode("\\{", node);
        // statements
        Element statements = node.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }

        vmw.writeGoto(endLable);
        vmw.writeLabel(falseLable);

        matchTermNode("\\}", node);
        if (jk.previewNextToken().matches("else")) {
            // else
            matchTermNode("else", node);
            //
            matchTermNode("\\{", node);
            // statements
            statements = node.addElement(ElementType.statements.toString());
            CompileStatements(statements);
            // }
            matchTermNode("\\}", node);
        }

        vmw.writeLabel(endLable);
    }

    /**
     * ok, term (op term)*
     */
    public void CompileExpression(Element node) throws IOException {
        // term
        Element term = node.addElement(ElementType.term.toString());
        CompileTerm(term);
        // op +-*/&\>=< >= <=
        while (jk.previewNextToken().matches("\\+|-|\\*|/|&|\\||>|=|<|>=|<=")) {
            String op = matchTermNode(".*", node);
            term = node.addElement(ElementType.term.toString());
            CompileTerm(term);
            /** op, writeArithmetic */
            vmw.writeArithmetic(Command.getCommand(op));
        }
    }

    /** ok , unaryop term */
    public void CompileTerm(Element node) throws IOException {
        // - ~ term
        if (jk.previewNextToken().matches("-|~")) {
            String negOrNot = matchTermNode(".*", node);
            // term
            Element term = node.addElement(ElementType.term.toString());
            CompileTerm(term);
            if (negOrNot.matches("-")) {
                vmw.writeArithmetic(Command.NEG);
            } else { // ~
                vmw.writeArithmetic(Command.NOT);
            }
            return;
        }
        // expression
        if (jk.previewNextToken().matches("\\(")) {
            // (
            matchTermNode("\\(", node);
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            // )
            matchTermNode("\\)", node);
            return;
        }
        // constant a[expression] b(xxx)a.b(xxx)
        String cab = matchTermNode(".*", node);
        // 数组
        if (jk.previewNextToken().matches("\\[")) {
            // [
            matchTermNode("\\[", node);
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            // ]
            matchTermNode("\\]", node);

            vmw.writePush(Segment.getSegment(st.kindOf(cab)), st.indexOf(cab));
            vmw.writeArithmetic(Command.ADD);
            vmw.writePop(Segment.POINTER, 1);
            vmw.writePush(Segment.THAT, 0);

            // 函数
        } else if (jk.previewNextToken().matches("\\.|\\(")) {
            int numArgOFCall = 0;
            String nameOfFun = cab;
            if (jk.previewNextToken().matches("\\.")) {
                // .
                matchTermNode("\\.", node);
                // subroutineName
                nameOfFun = nameOfFun + "." + matchTermNode(IDENTIFIER, node);
            }

            // 若obj.method
            if (nameOfFun.contains(".")) {
                String obj = nameOfFun.substring(0, nameOfFun.indexOf("."));
                String type = st.typeOf(obj);
                if (type != null) {
                    if (st.kindOf(obj).toString().matches("field")) {
                        // this.obj.method
                        vmw.writePush(Segment.THIS, st.indexOf(obj));

                    } else if (st.kindOf(obj).toString().matches("var|arg|static")) {
                        vmw.writePush(Segment.getSegment(st.kindOf(obj)), st.indexOf(obj));
                    }
                    nameOfFun = type + "." + nameOfFun.substring(nameOfFun.indexOf(".") + 1);
                    numArgOFCall++;
                }
            } else { // (this.)method
                vmw.writePush(Segment.POINTER, 0);
                nameOfFun = className + "." + nameOfFun;
                numArgOFCall++;
            }

            // (
            matchTermNode("\\(", node);
            // expressionList
            Element expressionList = node.addElement(ElementType.expressionList.toString());
            if (!jk.previewNextToken().matches("\\)")) {
                numArgOFCall = numArgOFCall + CompileExpressionList(expressionList);
            }
            // )
            matchTermNode("\\)", node);
            // 编译call
            vmw.writeCall(nameOfFun, numArgOFCall);
        } else {
            // 编译expression term 数字常量 关键字常量或 变量 参数
            if (cab.matches("[0-9]*")) {
                vmw.writePush(Segment.CONST, Integer.parseInt(cab));
            } else if (cab.matches("true")) {
                vmw.writePush(Segment.CONST, 0);
                vmw.writeArithmetic(Command.NOT);
            } else if (cab.matches("null|false")) {
                vmw.writePush(Segment.CONST, 0);
            } else if (cab.matches("this")) {
                vmw.writePush(Segment.POINTER, 0);
            } else if (cab.matches("that")) {
                vmw.writePush(Segment.POINTER, 1);
            } else if (cab.contains("\"")) { // 字符串
                cab = cab.substring(1, cab.length() - 1);
                vmw.writePush(Segment.CONST, ((String) cab).length());
                vmw.writeCall("String.new", 1);
                for (char c : ((String) cab).toCharArray()) {
                    vmw.writePush(Segment.CONST, c);
                    vmw.writeCall("String.appendChar", 2);
                }
            } else {
                vmw.writePush(Segment.getSegment(st.kindOf(cab)), st.indexOf(cab));
            }
        }

        // else {// 标识符 前面已经出现了
        // // matchTermNode(".*", node);
        // }
    }

    /**
     * ok , expression (, expression)*
     */
    public int CompileExpressionList(Element node) throws IOException {
        int numArgOFCall = 0;

        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        numArgOFCall++;
        while (jk.previewNextToken().matches(",")) {
            // ,
            matchTermNode(",", node);
            expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            numArgOFCall++;
        }
        return numArgOFCall;
    }

    public void writeXML(String outpath) throws IOException {
        JackAnalyzer.write(doc, outpath);
    }
}