package DSLGuided.requestsx.PSA

import junit.framework.TestCase
import java.sql.ResultSet

class PSASearchProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testSimplesearch() {
        val search_dsl = "'search'=>::sql{'SELECT * FROM psa '}::numberpsa{'1900'},::department{'ПЗУ №3',''},::datarange{'12.06.1940':'12.07.1940'},::client{'ООО Артемий'},::platenumber{'KAMAZ K582HB30'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search_dsl)

        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        while (f?.next() == true){
            val number = f.getString("number")
            println(number)
        }
     //   psasearch.simplesearch()
    }
    fun testsearch() {
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::client{'ШАПУРИН АНАТОЛИЙ 'ВИКТОРОВИЧ'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        var counter = 0
        while (f?.next() == true){
            val number = f.getString("number")
            println("FOUND! ${++counter} $number")
        }
    }

    fun testsearchpzu3() {
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::department{'ПЗУ №3',''}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        assertNotNull(psasearch.executor )
        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        var counter = 0
        while (f?.next() == true){
            val number = f.getString("number")
            println("FOUND! ${++counter} $number")
        }
        //   psasearch.simplesearch()
    }

    fun testGetdepIdExecutor() {
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        assertEquals("2", psasearch.getdepIdExecutor("ПЗУ №3"))
    }

fun testsearchplatenumber(){

        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::platenumber{'VAZ P890BE30'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        assertNotNull(psasearch.executor )
        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        var counter = 0
        while (f?.next() == true){
            val number = f.getString("number")
            println("FOUND! ${++counter} $number")
        }
        //   psasearch.simplesearch()
    }

    fun testsearchplatenumber___(){

        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::platenumber{'VAZ P890BE30 and`client`=4352'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        assertNotNull(psasearch.executor )
        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        var counter = 0
        while (f?.next() == true){
            val number = f.getString("number")
            println("FOUND! ${++counter} $number")
        }
        //   psasearch.simplesearch()
    }
}

