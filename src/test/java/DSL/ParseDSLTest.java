package DSL;

import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class ParseDSLTest extends TestCase {

    @Test
    public void testSimpleadd(){
        assertEquals(2,2);
        ParseDSL SDSL = new ParseDSL();
        assertEquals(8, SDSL.add(4,4));
    }

}