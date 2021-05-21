package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import java.io.File
import java.nio.file.Files

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
}