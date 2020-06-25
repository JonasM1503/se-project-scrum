package corewars.jmars;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class CopyPrintStream extends PrintStream {
    
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private final PrintStream copyOut = new PrintStream(content);

    public CopyPrintStream(OutputStream out) {
        super(out);
    }
    
    @Override
    public void println(String x) {
        super.println(x);
        copyOut.println(x);
        copyOut.flush();
    }
    
    public String getTrimmedContent() {
        return content.toString().trim();
    }
    
}
