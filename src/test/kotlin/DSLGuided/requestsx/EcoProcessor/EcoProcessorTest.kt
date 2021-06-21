package DSLGuided.requestsx.EcoProcessor

import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSASearchProcessor
import abstractions.KeyValue
import junit.framework.TestCase

class EcoProcessorTest : TestCase() {

    fun testRender() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::enabled{'false'}."
        val PSAConnector = PSAConnector()
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.render(dsl)
        assertEquals(4, EcoProc.quarter)
        assertEquals(2019, EcoProc.year)
        if (EcoProc.department is ArrayList<*>)
            print("THERE LIST!")
        var Arr = ArrayList<String>()
        Arr.add("ПЗУ №3")
        Arr.add("ПЗУ №2")
        assertEquals(Arr, EcoProc.department)
    }


    fun testRender2() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':'ПЗУ №3'},::enabled{'false'}."
        val PSAConnector = PSAConnector()
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.render(dsl)
        assertEquals(4, EcoProc.quarter)
        assertEquals(2019, EcoProc.year)
        if (EcoProc.department is ArrayList<*>)
            print("THERE LIST!")
        assertEquals("ПЗУ №3", EcoProc.department)
    }
    fun testWriteToDocumentPSA() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::enabled{'false'}."
        val PSAConnector = PSAConnector()
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.render(dsl)
        var Arr = mutableListOf<KeyValue>()
        val KeyValue1 = KeyValue("key", "value")
        val KeyValue2 = KeyValue("key", "value")
        val KeyValue3 = KeyValue("key", "value")
        Arr.add(KeyValue1)
        Arr.add(KeyValue2)
        Arr.add(KeyValue3)
        val Sheet = EcoProc.Book.createSheet("sheet")
        val pos = EcoProc.writeToDocumentPSA("20-06-2021", 0, Sheet, Arr)
        assertEquals(3, pos)
        EcoProc.writeToDocumentPSA("20-06-2021", pos, Sheet, Arr)
        EcoProc.finalizeBook()
    }

    fun testGetQuarterMap() {
        val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
        val psaconnector = PSAConnector()
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor= psaconnector.executor!!
        val dsl = "'eco'=>::generatefor{'quarter':1,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::enabled{'false'}."
        val PSAConnector = PSAConnector()
        val EcoProc = EcoProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.render(dsl)
        assertNotNull(EcoProc.DateRange)
        println(EcoProc.DateRange)
        EcoProc.process()
    }
}