package DSLGuided.requestsx.EcoProcessor

import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSASearchProcessor
import abstractions.KeyValue
import junit.framework.TestCase

class EcoProcessorTest : TestCase() {
    fun testRender() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::enabled{'false'}."
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        assertEquals(4, EcoProc.quarter)
        assertEquals(2019, EcoProc.year)
        if (EcoProc.department is ArrayList<*>)
            print("THERE LIST!")
        var Arr = ArrayList<String>()
        Arr.add("ПЗУ №3")
        Arr.add("ПЗУ №2")
        assertEquals(Arr, EcoProc.department)
    }


    fun Render2() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':'ПЗУ №3'},::enabled{'false'}."
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        assertEquals(4, EcoProc.quarter)
        assertEquals(2019, EcoProc.year)
        if (EcoProc.department is ArrayList<*>)
            print("THERE LIST!")
        assertEquals("ПЗУ №3", EcoProc.department)
    }
    fun WriteToDocumentPSA() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::enabled{'false'}."
        val EcoProc = EcoProcessor()
        var psasearch = PSASearchProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        var Arr = mutableListOf<KeyValue>()
        val KeyValue1 = KeyValue("key", "value")
        val KeyValue2 = KeyValue("key", "value")
        val KeyValue3 = KeyValue("key", "value")
        Arr.add(KeyValue1)
        Arr.add(KeyValue2)
        Arr.add(KeyValue3)
        val Sheet = EcoProc.Book.createSheet("sheet")
        val pos = EcoProc.writeToDocumentPSA("20-06-2021", "bugaga", 0, Sheet, Arr)
        assertEquals(3, pos)
        EcoProc.writeToDocumentPSA("20-06-2021", "CLIENT", pos, Sheet, Arr)
        EcoProc.finalizeBook()
    }

    fun GetQuarterMap() {
        val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
        val psaconnector = PSAConnector()
        psaconnector.r(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
        val dsl = "'eco'=>::generatefor{'quarter':1,'year':2021,'department':['ПЗУ №1','ПЗУ №2','ПЗУ №3', 'ПЗУ №12']},::enabled{'false'}."///'ПЗУ №3', 'ПЗУ №2', 'ПЗУ №12'
        val EcoProc = EcoProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        assertNotNull(EcoProc.DateRange)
        println(EcoProc.DateRange)
        EcoProc.process()
    }

    fun testquatermap(){
        val dsl = "'eco'=>::generatefor{'quarter':1,'year':2019,'department':['ПЗУ №3', 'ПЗУ №2']},::quartermap{'1':'year-01-01'/'year-02-31','2':''year-04-01'/'year-06-30'','3':''year-07-01'/'year-9-30'','4':''year-10-01'/'year-12-31''},::enabled{'true'}."
        val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
        val psaconnector = PSAConnector()
        psaconnector.r(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
        val EcoProc = EcoProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        assertEquals("'year-01-01':'year-02-31'", EcoProc.QuarterMap.get(1) )
    }

    fun testprocesspatchedmapap() {
        val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
        val psaconnector = PSAConnector()
        psaconnector.r(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
        val dsl = "'eco'=>::quartermap{'1':'year-01-01'/'year-01-31','2':''year-04-01'/'year-04-04'','3':''year-07-01'/'year-07-04'','4':''year-10-01'/'year-10-04''}," +
                  "::generatefor{'quarter':1,'year':2021,'department':['ПЗУ №2','ПЗУ №3']},::enabled{'true'}."///'ПЗУ №3', 'ПЗУ №2', 'ПЗУ №12', ,'ПЗУ №12','ПЗУ №1'
        val EcoProc = EcoProcessor()
        EcoProc.PSASearchProcessor = psasearch
        EcoProc.r(dsl)
        assertNotNull(EcoProc.DateRange)
        println(EcoProc.DateRange)
        //EcoProc.process()
    }

    fun testMap() {
        var map = mutableMapOf("1" to 2,  "2" to 2 )
        val key = "1"
        val value = map.get(key)
        val value2 = value?.plus(1)
        if (value2 != null) {
            map.put(key, value2)
        }
        assertEquals(3, map.get(key))
        var v0 = map.get("3")
        if (v0 == null){
            map.put("3",0)
        }
        v0 = map.get("3")
//        val v = map.getOrDefault("3", 0)
//        println("V=$v")
        println("V0=$v0")
    }

    fun test_clone(){
        val eco = EcoProcessor()
        assertEquals("Description","Description" )
        eco.clone("template.xlsx", "copy.xlsx", "Description")
    }


}