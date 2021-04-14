package DSLGuided.requestsx.PSA

import junit.framework.TestCase
import org . json . simple . JSONArray
import org . json . simple . JSONObject

////////////Пример DSL для PSASearchProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'search'=>::sql('SELECT * FROM psa')
//               ::numberpsa{[1900, 1902]},
//               ::department('ПЗУ№1'),
//               ::datarange('12.06.1940':'12.07.1940'),
//               ::client('ООО Артемий'),
//               ::typepayment('cash','bank'),
//               ::platenumber('KAMAZ K582HB30').
class PSASearchProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testSimplesearch() {
        val search_dsl = "'search'=>::sql{'SELECT * FROM psa '},::numberpsa{'1900'},::department{'ПЗУ №3',''},::datarange{'12.06.1940':'12.07.1940'},::client{'ООО Артемий'},::platenumber{'KAMAZ K582HB30'}."
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
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::client{'ШАПУРИН АНАТОЛИЙ ВИКТОРОВИЧ'}."
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

    fun testjsonbuilder(){
        var JSONObj = JSONObject()
        var JSONArr = JSONArray()
        var obj = JSONObject()
        obj.put("client", "nameclient")
        JSONArr.add(obj)
        print(JSONArr.toString())

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

    fun testCreateJSONResponce() {
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::platenumber{'VAZ P890BE30'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        assertNotNull(psasearch.executor )
        assertNotNull(psasearch.getPSA())
        assertNotNull(psasearch.createJSONResponce(psasearch.getPSA()))
        println(psasearch.createJSONResponce(psasearch.getPSA()))
    }

    fun testCreateJSONResponcetimer() {
        val startTime = System.nanoTime()
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::datarange{'2020-01-01':'2021-08-04'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.executor=psaconnector.executor
        psasearch.render(search3)
        psasearch.getPSA()
        ///var r = psasearch.createJSONResponce(psasearch.getPSA())
       // println(r)
        val endTime = System.nanoTime()
        val duration = endTime - startTime
   ////     val fos = FileOutputStream("result.json")
    ////    fos.write(r.encodeToByteArray())
   ////     fos.close()
        println("time execution:: " + duration / 1000000000)
    }

    fun testPostRequest() {
        var psasearch = PSASearchProcessor()
        println(psasearch.postRequest("1203", "855467","http://192.168.0.126:4567/checkpost"))

    }

}

