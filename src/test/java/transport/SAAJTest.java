package transport;

import junit.framework.TestCase;
import org.junit.Test;
import se.roland.transport.SAAJ;
import se.roland.xml.Transform;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotEquals;

public class SAAJTest extends TestCase {

    public void testSend() throws Exception {
        String file = "xml4test/input.xml";
        String regs = "xml4test/registerp2p.xml";
        String work_txt = "xml4test/worktxt.xml";
        String work_xml = "xml4test/work.txt.xml";
        String transformed = "xml4test/work.txt.xml.trans";
        String test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl";
        String prod = "https://securepayments.sberbank.ru/payment/webservices/p2p";
        SAAJ saa= new SAAJ(test);
        FileOutputStream fos = new FileOutputStream(transformed);
        Transform transform = new Transform();
        fos.write(transform.transform(Files.readAllBytes(Path.of(work_xml))));
        fos.close();
        assertNotEquals(null, saa.send(transformed, "results.xml"));
    }


    @Test
    public void testsend2() throws Exception {
        String file = "xml4test/1withoutcert.xml";
        SAAJ saa= new SAAJ("http://smev3-n0.test.gosuslugi.ru:7500/ws?wsdl");
        String s = saa.send(file, "results.xml");
        assertNotEquals(null, s);
    }
}