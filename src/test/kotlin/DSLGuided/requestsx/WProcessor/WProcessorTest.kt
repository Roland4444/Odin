package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import se.roland.util.HTTPClient
import java.io.File
import java.nio.file.Files
import java.util.*

class WProcessorTest : TestCase() {
    val dsl = """'wprocessor'=>::pathtoimgs{./IMG},::addresstoresend{db2.avs.com.ru/storage/purchase/import},::enabled{'true'}."""
    val wProcessor = WProcessor()

    fun SaveImages() {
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
        val f =  WProc.getResultinLinkedList("7")
        Saver.Saver.write(Saver.Saver.savedToBLOB(f), "linked.bin")
        println("RES $f")
    }

    fun testGetResultinLinkedList() {}

    fun testtestmode(){
        val dsl = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val WProc = WProcessor()
        WProc.render(dsl)
        assertFalse(WProc.testmode_)
        val dsl2 = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600},::testmode{true},::example{88.bin}."
        WProc.render(dsl2)
        assertTrue(WProc.testmode_)
        assertEquals("88.bin", WProc.exampleListFile)
    }
    fun testGetDepIdViaName() {
        val dsl = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val Connector = DBConnector()
        Connector.render(dsl)
        val WProc = WProcessor()
        WProc.dbconnector = Connector
        assertEquals("8", WProc.getDepIdViaName("Кутум"))
    }

    fun testGetTestmode_() {
        val dsl = """'wprocessor'=>::usedepsmap{'true','1':'ACKK','2':'Kutum','24':'Babaevskogo'},::pathtoimgs{./IMG},::addresstoresend{db2.avs.com.ru/storage/purchase/import},::enabled{'true'}."""
        val wProcessor = WProcessor()
        wProcessor.render(dsl)
        assertEquals(wProcessor.TRUE_ATOM, wProcessor.UseDepsMap)
        assertEquals(wProcessor.DepsMap.size,3)
        assertEquals(wProcessor.DepsMap.get("1"),"ACKK")
        assertEquals(wProcessor.DepsMap.get("2"),"Kutum")
        assertEquals(wProcessor.DepsMap.get("24"),"Babaevskogo")

    }
}