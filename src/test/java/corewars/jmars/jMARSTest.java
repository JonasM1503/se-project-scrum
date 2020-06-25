package corewars.jmars;

import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Simple Unit Tests for jMARS.
 */
public class jMARSTest 
{
    private CopyPrintStream copyOut;
    private PrintStream originalOut;
    
    @BeforeEach
    public void setUpStreams() 
    {
        originalOut = System.out;
        copyOut = new CopyPrintStream(originalOut);
        System.setOut(copyOut);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testStdOutCopy() {
        System.out.println("stdout redirected");
        assertEquals("stdout redirected", copyOut.getTrimmedContent(), "Failure to copy stdout");
    }

    @Test
    public void testDwarf2vsImp() throws InterruptedException {
        jMARS.main(new String[] { "war\\dwarf2.red", "war\\imp.red" });
        Thread.sleep(500); // Wait until all war rounds are finished ;-)
        assertTrue(copyOut.getTrimmedContent().endsWith("Next Dwarf: 10"), "Wrong statistic result: <" + copyOut.getTrimmedContent() + ">");
    }
}
