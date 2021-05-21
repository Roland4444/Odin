package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import java.io.File
import java.nio.file.Files

class WProcessorTest : TestCase() {

    fun testSaveImages() {
        val dsl = """'wprocessor'=>::pathtoimgs{./IMG},::enabled{'true'}."""
        val wProcessor = WProcessor()
        wProcessor.render(dsl)
        val Arr1 =  Files.readAllBytes(File("1.jpg").toPath())
        val Arr2 =  Files.readAllBytes(File("2.jpg").toPath())

        wProcessor.saveImages(Arr1, Arr2, "12", "2021-05-21", "12")
        assertTrue(File("./IMG/2021-05-21/12_12_1.jpg").exists())
    }
}