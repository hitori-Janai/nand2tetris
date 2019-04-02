package app;

import java.util.regex.Pattern;

// import assembler.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello Java");
        // new Parser("project/06/add/Add.asm");
        // new Parser("project/04/sumto100.asm");
        // String greeting = "sadas  asd你 sd好 asdas";
        // System.out.println(greeting.substring(1,greeting.length()));   
        
        Pattern pa = Pattern.compile("[0-9]*");
        System.out.println(pa.matcher("123123123dsaa12312").matches());
        
    }
}