package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import se.roland.util.HTTPClient
import java.io.File
import java.nio.file.Files
import java.util.HashMap

class WProcessorTest : TestCase() {
    val dsl = """'wprocessor'=>::pathtoimgs{./IMG},::addresstoresend{db2.avs.com.ru/storage/purchase/import},::enabled{'true'}."""
    val wProcessor = WProcessor()

    fun testSaveImages() {
        wProcessor.render(dsl)
        val Arr1 =  Files.readAllBytes(File("1.jpg").toPath())
        val Arr2 =  Files.readAllBytes(File("2.jpg").toPath())

        wProcessor.saveImages(Arr1, Arr2, "2", "2021-05-21", "7")
        assertTrue(File("./IMG/2021-05-21/2_7_1.jpg").exists())
    }

    fun testAddressToResend(){
        wProcessor.render(dsl)
        assertEquals("db2.avs.com.ru/storage/purchase/import", wProcessor.addresstoresend_)
    }

    fun testResenddata() {
        val dsl = """'wprocessor'=>::pathtoimgs{./IMG},::addresstoresend{http://192.168.0.126:4567/testresend},::enabled{'true'}."""
        wProcessor.render(dsl);
        val Map = mapOf("t1" to "2", "t2"  to "T222", "t3" to "5.888")
        HTTPClient.sendPOST(Map as HashMap<String, String>, wProcessor.addresstoresend_)

    }
}