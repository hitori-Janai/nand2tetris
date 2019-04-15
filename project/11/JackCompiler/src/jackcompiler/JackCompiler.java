package jackcompiler;
import java.io.IOException;
import java.io.File;
import java.io.FileFilter;
/**
 * JackCompiler
 */
public class JackCompiler {
    
    public static void main(String[] args) throws IOException {
        String inpath = "C:\\Users\\11054\\Desktop\\nand2tetris\\project\\11\\ComplexArrays";
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
                String outpath = inpath.substring(0, inpath.lastIndexOf(".jack")) + "_.vm";
                new CompilationEngine(jk,outpath);
            }
        }
    }
}