package DSLGuided.requestsx.PSA

import abstractions.RSet
import junit.framework.TestCase
import org . json . simple . JSONArray
import org . json . simple . JSONObject
import org.json.simple.parser.JSONParser

////////////Пример DSL для PSASearchProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'search'=>::sql('SELECT * FROM psa')
//               ::numberpsa{[1900, 1902]},
//               ::department('ПЗУ№1'),
//               ::datarange('12.06.1940':'12.07.1940'),
//               ::client('ООО Артемий'),
//               ::typepayment('cash','bank'),
//               ::platenumber('KAMAZ K582HB30'),
//               ::passcheckurl(https://passport.avs.com.ru/),
//               ::limit{200}.
class PSASearchProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testSimplesearch() {
        val search_dsl = "'search'=>::sql{'SELECT * FROM psa '},::numberpsa{'1900'},::department{'ПЗУ №3',''},::datarange{'12.06.1940':'12.07.1940'},::client{'ООО Артемий'},::platenumber{'KAMAZ K582HB30'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
        psasearch.render(search_dsl)

        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        while (f?.next() == true){
            val number = f.getString("number")
        }
     //   psasearch.simplesearch()
    }
    fun testsearch() {
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::client{'ШАПУРИН АНАТОЛИЙ ВИКТОРОВИЧ'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
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
        psasearch.psaconnector= psaconnector
        psasearch.render(search3)
        assertNotNull(psasearch.psaconnector.executor )
        assertNotNull(psasearch.getPSA())
        val f = psasearch.getPSA()
        var counter = 0
        while (f?.next() == true){
            val number = f.getString("number")
            println("FOUND! ${++counter} $number")
        }
        //   psasearch.simplesearch()
    }

    fun test_search_depsretricted(){
        val search_dsl = "'search'=>::sql{'SELECT * FROM psa '},::numberpsa{'4926'}."
        var psasearch = PSASearchProcessor()
        psaconnector.render(initDB)
        psasearch.psaconnector = psaconnector
        val Res = PSASearchProcessor.search(search_dsl, psasearch, "24" )
        val JSON: JSONArray = JSONParser().parse(Res) as JSONArray
        println(JSON.size)
        val Res2 = PSASearchProcessor.search(search_dsl, psasearch )
        val JSON2: JSONArray = JSONParser().parse(Res2) as JSONArray
        println(JSON2.size)

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
        psasearch.psaconnector= psaconnector
        assertEquals("2", psasearch.getdepIdExecutor("ПЗУ №3"))
    }

fun testsearchplatenumber(){

        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::platenumber{'VAZ P890BE30'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector= psaconnector
        psasearch.render(search3)
        assertNotNull(psasearch.psaconnector.executor )
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
        psasearch.psaconnector= psaconnector
        psasearch.render(search3)
        assertNotNull(psasearch.psaconnector.executor )
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
        psasearch.psaconnector= psaconnector
        psasearch.render(search3)
        assertNotNull(psasearch.psaconnector.executor )
        assertNotNull(psasearch.getPSA())
        assertNotNull(psasearch.createJSONResponce(psasearch.getPSA()))
        println(psasearch.createJSONResponce(psasearch.getPSA()))
    }

    fun testCreateJSONResponcetimer() {
        val startTime = System.nanoTime()
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::datarange{'2020-01-01':'2021-08-04'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch. psaconnector=psaconnector
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


    fun testCounterBlack() {
        val startTime = System.nanoTime()
        val search3 =  "'search'=>::sql{'SELECT * FROM psa '},::datarange{'2021-01-01':'2021-01-31'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch.psaconnector=psaconnector
        psasearch.render(search3)
        val R  = psasearch.getPSA()
        var counter = 0
        while (R?.next() == true) {
            val psaId = R.getString("id")
            val RR = psasearch.getWViaPSAId(psaId)
            while (RR?.next()==true)
                println("COUNTER ${counter++}")
        }
        ///var r = psasearch.createJSONResponce(psasearch.getPSA())
        // println(r)
        val endTime = System.nanoTime()
        val duration = endTime - startTime
        ////     val fos = FileOutputStream("result.json")
        ////    fos.write(r.encodeToByteArray())
        ////     fos.close()
        println("time execution:: " + duration / 1000000000)
    }
    fun countMaxNuberPSA(){
        val search6 =  "'search'=>::sql{'SELECT * FROM psa '},::department{'Test',''},::datarange{'2021-01-01':'2021-04-26'}."
        val search6_ = "'search'=>::sql{'SELECT * FROM psa '},::department{'ПЗУ №12',''},::datarange{'2021-01-01':'2021-04-26'}."

        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch. psaconnector=psaconnector
        psasearch.render(search6)
        val res = psasearch.getPSA()
        var counter = 0
        while (res?.next() == true){
            println(counter++)
        }
        assertEquals(6, ++counter)
        val etalon25 = 3353
        psasearch.render(search6_)
        val res_ = psasearch.getPSA()
        counter = 0
        while (res_?.next() == true){
            println(counter++)
        }
        assertEquals(etalon25, ++counter)
    }


    fun testsearchcountMaxNuberPSA(){
        val search6 =  "'search'=>::sql{'SELECT * FROM psa '},::datarange{'2021-10-12':'2021-10-15'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch. psaconnector=psaconnector
        psasearch.render(search6)
        val res = psasearch.getPSA()
        var counter = 0
        while (res!!.next())
            println("COUNTER::${counter++}")
    }

    fun testsearchcount(){
        val search6 =  "'search'=>::sql{'SELECT * FROM logs '},::date{'2021-10-12':'2021-10-15'},::users{'20','25','26','15','34'}."
        psaconnector.render(initDB)
        var psasearch = PSASearchProcessor()
        psasearch. psaconnector=psaconnector
        psasearch.render(search6)
        println("SQL::\n ${psasearch.initialString}")
    }







    //  ЯВЛЯЕТСЯ НЕДЕЙСТВИТЕЛЬНЫМ!
    fun testDBConnectorTestGetPSAIdViaUUID() {
      psaconnector.render(initDB)
      var psasearch = PSASearchProcessor()
      psasearch. psaconnector=psaconnector
      val id = psasearch.getPSAIdViaUUID("7487548745867458678746756754y764y674576547685764586754684765874")
      assertEquals(-1, id)
    }


}

