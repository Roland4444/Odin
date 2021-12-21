package util;

import junit.framework.TestCase;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class KeyUtilTest extends TestCase {

    public void testLoadPublicKey() throws FileNotFoundException {
        InputStream pubKeyInpStream = new FileInputStream("pub.key");
        AsymmetricKeyParameter publKey = KeyUtil.loadPublicKey(pubKeyInpStream);
        assertNotNull(publKey);
    }
}