package assembler2;
import java.util.HashMap;

/**
 * SymbolTable
 */
public class SymbolTable {

    /**
     * symTable
     */
    private HashMap<String, Integer> symTable;

    /**
     * Point to the next space cell
     */
    private int index = 16;

    /**
     * Constructor
     */
    public SymbolTable(){
        symTable = new HashMap<String, Integer>();
        symTable.put("R0", 0);
		symTable.put("R1", 1);
		symTable.put("R2", 2);
		symTable.put("R3", 3);
		symTable.put("R4", 4);
		symTable.put("R5", 5);
		symTable.put("R6", 6);
		symTable.put("R7", 7);
		symTable.put("R8", 8);
		symTable.put("R9", 9);
		symTable.put("R10", 10);
		symTable.put("R11", 11);
		symTable.put("R12", 12);
		symTable.put("R13", 13);
		symTable.put("R14", 14);
		symTable.put("R15", 15);
		
		symTable.put("SP", 0);
		symTable.put("LCL", 1);
		symTable.put("ARG", 2);
		symTable.put("THIS", 3);
		symTable.put("THAT", 4);
		symTable.put("SCREEN", 16384);
		symTable.put("KBD", 24576);
    }

    /**
     * L_command
     * add (symbol,address) to symTable.
     * if symtable contains symbol,add fail.
     * @param symbol
     * @param address
     */
    public void addEntry(String symbol,int address){
        if(contains(symbol)){
            return;
        }
        symTable.put(symbol, address);
    }

    /**
     * add A_command symbol to symtable
     * @param symbol
     */
    public void addEntry(String symbol){        
        if(contains(symbol)){
            return;
        }
        symTable.put(symbol, index++);
    }

    /**
     * Whether it contains the specified symbol?
     * @param symbol
     * @return
     */
    public boolean contains(String symbol){
        return symTable.containsKey(symbol);
    }

    /**
     * getValue(key)
     * @param symbol
     * @return
     */
    public int getAddress(String symbol){
        return symTable.get(symbol);
    }
}