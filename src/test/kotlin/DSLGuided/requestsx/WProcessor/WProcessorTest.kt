package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import se.roland.util.HTTPClient
import java.io.File
import java.nio.file.Files
import java.util.*

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
        val dsl = """'wprocessor'=>::pathtoimgs{./IMG},::addresstoresend{https://db2.avs.com.ru/storage/purchase/import},::enabled{'true'}."""
        wProcessor.render(dsl);
        val Map = mapOf("t1" to "2", "t2"  to "T222", "t3" to "5.888")
        HTTPClient.sendPOST(Map as HashMap<String, String>, wProcessor.addresstoresend_)


        val hashMap = HashMap<String, String>()
        hashMap["BRUTTO"] = "90"
        hashMap["TARE"] = "90"
        hashMap["TRASH"] = "90"
        hashMap["WAYBILL"] = "90"

        HTTPClient.sendPOST(hashMap, wProcessor.addresstoresend_)


    }

    fun testGetW() {
        val dsl = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val Connector = DBConnector()
        Connector.render(dsl)
        val WProc = WProcessor()
        WProc.dbconnector = Connector
        val f = WProc.getW("7")?.let { WProc.getResultinLinkedList(it) }
        Saver.Saver.write(Saver.Saver.savedToBLOB(f), "linked.bin")
        println("RES $f")
    }

    fun testGetResultinLinkedList() {}
}