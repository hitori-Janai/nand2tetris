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

    Document doc;

    public static enum ElementType {
        classVarDec, subroutineDec, parameterList, subroutineBody, varDec, statements, letStatement, doStatement,
        ifStatement, whileStatement, returnStatement, expressionList, expression, term;
    }

    /** 标识符 */
    public static final String IDENTIFIER = ".*";// "^[a-zA-Z_]{1}[a-zA-Z0-9_]*";

    public CompilationEngine(JackTokenizer jk) {
        this.jk = jk;
        CompileClass();
    }

    /** 匹配下一个终节点并添加 */
    private void matchTermNode(String regex, Element node) {
        if (jk.hasMoreTokens()) {
            jk.advance();
        }
        if (!jk.curToken.matches(regex)) {
            throw new RuntimeException("error Token : " + jk.curToken + " regex :" + regex);
        }
        System.out.println(jk.curToken);
        node.addElement(jk.tokenType().toString()).addText(jk.curToken);
    }

    /** ok,class className { classVarDec* subroutineDec* } */
    public void CompileClass() {
        doc = DocumentHelper.createDocument();
        Element root = doc.addElement("class");
        // class 关键字
        matchTermNode("class", root);
        // classname 字母数字_
        matchTermNode(IDENTIFIER, root);
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
        matchTermNode("field|static", node);
        // type
        matchTermNode(IDENTIFIER, node);
        // varName
        matchTermNode(IDENTIFIER, node);
        while (!jk.previewNextToken().matches(";")) {
            // ,
            matchTermNode(",", node);
            // varName
            matchTermNode(IDENTIFIER, node);
        }
        // ;
        matchTermNode(";", node);
    }

    /**
     * ok , constructor|function|method type subroutineName(parameterList)
     * subroutineBody
     */
    public void CompileSubRoutine(Element node) {
        // constructor|function|method
        matchTermNode(IDENTIFIER, node);
        // type void int char boolean className
        matchTermNode(IDENTIFIER, node);
        // subroutineName
        matchTermNode(IDENTIFIER, node);
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
        CompileSubRoutineBody(subroutineBody);
    }

    /** ok , { varDec* statements } */
    public void CompileSubRoutineBody(Element node) {
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
        matchTermNode(IDENTIFIER, node);
        // varName
        matchTermNode(IDENTIFIER, node);
        while (!jk.previewNextToken().matches("\\)")) {
            // ,
            matchTermNode(",", node);
            // type
            matchTermNode(IDENTIFIER, node);
            // varName
            matchTermNode(IDENTIFIER, node);
        }
    }

    /** ok , var type varName [,varName]* ; */
    public void CompileVarDec(Element node) {
        // var
        matchTermNode("var", node);
        // type
        matchTermNode(IDENTIFIER, node);
        // varName
        matchTermNode(IDENTIFIER, node);
        while (!jk.previewNextToken().matches(";")) {
            // ,
            matchTermNode(",", node);
            // varName
            matchTermNode(IDENTIFIER, node);
        }
        // ;
        matchTermNode(";", node);
    }

    /** ok 函数体里去除括号和变量声明部分. */
    public void CompileStatements(Element node) {
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
    public void CompileDo(Element node) {
        // do
        matchTermNode("do", node);
        // subroutineName | (className | varName)
        matchTermNode(IDENTIFIER, node);
        if (jk.previewNextToken().matches("\\.")) {
            // .
            matchTermNode("\\.", node);
            // subroutineName
            matchTermNode(IDENTIFIER, node);
        }
        // (
        matchTermNode("\\(", node);
        // expressionList
        Element expressionList = node.addElement(ElementType.expressionList.toString());
        if (!jk.previewNextToken().matches("\\)")) {
            CompileExpressionList(expressionList);
        }
        // )
        matchTermNode("\\)", node);
        // ;
        matchTermNode(";", node);
    }

    /** ok , let varName [expression]? = expression ; */
    public void CompileLet(Element node) {
        // let
        matchTermNode("let", node);
        // varName
        matchTermNode(IDENTIFIER, node);
        // [expression] ?
        if (jk.previewNextToken().matches("\\[")) {
            // [
            matchTermNode("\\[", node);
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            // ]
            matchTermNode("\\]", node);
        }
        // =
        matchTermNode("=", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // ;
        matchTermNode(";", node);
    }

    /** ok , while ( expression) { statements } */
    public void CompileWhile(Element node) {
        // while
        matchTermNode("while", node);
        // (
        matchTermNode("\\(", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // )
        matchTermNode("\\)", node);
        // {
        matchTermNode("\\{", node);
        // statements
        Element statements = node.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }
        matchTermNode("\\}", node);

    }

    /** ok , return expression? ; */
    public void CompileReturn(Element node) {
        // return
        matchTermNode("return", node);
        // expression?
        if (!jk.previewNextToken().matches(";")) {
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
        }
        // ;
        matchTermNode(";", node);
    }

    /** ok , if (expression) {statments} else{statments}? */
    public void CompileIf(Element node) {
        // if
        matchTermNode("if", node);
        // (
        matchTermNode("\\(", node);
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        // )
        matchTermNode("\\)", node);
        // {
        matchTermNode("\\{", node);
        // statements
        Element statements = node.addElement(ElementType.statements.toString());
        CompileStatements(statements);
        // }
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
    }

    /** ok, term (op term)* */
    public void CompileExpression(Element node) {
        // term
        Element term = node.addElement(ElementType.term.toString());
        CompileTerm(term);
        // op +-*/&\>=< >= <=
        while (jk.previewNextToken().matches("\\+|-|\\*|/|&|\\||>|=|<|>=|<=")) {
            matchTermNode(".*", node);
            term = node.addElement(ElementType.term.toString());
            CompileTerm(term);
        }
    }

    /** not ok , unaryop term */
    public void CompileTerm(Element node) {
        // - ~ term
        if (jk.previewNextToken().matches("-|~")) {
            matchTermNode(".*", node);
            // term
            Element term = node.addElement(ElementType.term.toString());
            CompileTerm(term);
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
        matchTermNode(".*", node);
        // 数组
        if (jk.previewNextToken().matches("\\[")) {
            // [
            matchTermNode("\\[", node);
            // expression
            Element expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
            // ]
            matchTermNode("\\]", node);
            // 函数
        } else if (jk.previewNextToken().matches("\\.|\\(")) {
            if (jk.previewNextToken().matches("\\.")) {
                // .
                matchTermNode("\\.", node);
                // subroutineName
                matchTermNode(IDENTIFIER, node);
            }
            // (
            matchTermNode("\\(", node);
            // expressionList
            Element expressionList = node.addElement(ElementType.expressionList.toString());
            if (!jk.previewNextToken().matches("\\)")) {
                CompileExpressionList(expressionList);
            }
            // )
            matchTermNode("\\)", node);
            
        } 
        // else {// 标识符 前面已经出现了
        //     // matchTermNode(".*", node);
        // }
    }

    /** ok , expression (, expression)* */
    public void CompileExpressionList(Element node) {
        // expression
        Element expression = node.addElement(ElementType.expression.toString());
        CompileExpression(expression);
        while (jk.previewNextToken().matches(",")) {
            // ,
            matchTermNode(",", node);
            expression = node.addElement(ElementType.expression.toString());
            CompileExpression(expression);
        }
    }

    public void writeXML(String outpath) throws IOException {
        JackAnalyzer.write(doc, outpath);
    }
}