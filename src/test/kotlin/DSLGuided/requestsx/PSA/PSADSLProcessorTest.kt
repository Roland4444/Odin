package DSLGuided.requestsx.PSA
import junit.framework.TestCase
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.abstractions.timeBasedUUID
import java.nio.file.Files
import java.nio.file.Path

import java.util.HashMap
import kotlin.test.assertNotEquals
import java.io.File as File

class PSADSLProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testRender() {
      //  val initialdsl = "'psa2'=>::psa{'urldb':'jdbc:mysql://192.168.0.121:3306/psa','login':user123,'pass':password },::psagetNumberfrom('url':http://192.168.0.121:8080/psa/psa/num,'keyparam':department_id),::stupid{http://192.168.0.121:8080/psa/psa/num}"
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        psa.render(copy)
        assertEquals("jdbc:mysql://192.168.0.121:3306/psa", psa.urldb)
      //  assertEquals("http://192.168.0.121:8080/psa/psa/num", psa.dumb)
        assertEquals("123", psa.pass)
        assertEquals("root", psa.login)
        //val f: psaDraft = psa.createdraft
       // f(12f, "12", "fgfgf")
    }
    fun createdraft(){
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        var psa  = PSADSLProcessor()
        psa.render(copy)
        val f = psa.createdraft
        val uuid = timeBasedUUID.generate()
        println("UUID $uuid")
        f("12", "0.2","5А","17", "KIRILL_F15", uuid, "black")
        val m = psa.completePSA
         m("12", "1",  uuid)

    }//Brutto, Sor, DepId, PlateNumber, UUID, Type

    fun testcompanioncreatedraftpsa(){
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        var hash = mutableMapOf<String, String>()
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.executor = psaconnector.executor
        psa.executor=psaconnector.executor
        psa.psearch=psearch
        psa.render(copy)
        hash.put("Brutto", "12000")
        hash.put("Sor", "0.2")
        hash.put("Metal", "5А")
        hash.put("DepId", "25")
        hash.put("PlateNumber", "VTB100")
        hash.put("UUID",  timeBasedUUID.generate())
        hash.put("Type", "black")
        PSADSLProcessor.createdraftPSA(hash as HashMap<String, String>, copy, psa )

    }

    fun testprocessinvagning(){
        val parser = JSONParser()
        val inputjs = "{\"brutto\":16,\"calculatedMass\":\"1267.20\",\"metalId\":15,\"totalPrice\":0,\"tare\":0,\"price\":\"80\",\"clogging\":1,\"mass\":15.84,\"metal\":{\"def\":false,\"name\":\"Нержавейка\",\"psaid\":2,\"id\":15},\"id\":83580,\"newPrice\":\"80\",\"trash\":0}"
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        val uuid = "48bd68d2834fb341fdc03a92a02dd88a"
        var hash = mutableMapOf<String, String>()
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.executor = psaconnector.executor
        psa.executor=psaconnector.executor
        psa.psearch=psearch
        psa.processinvagning(parser.parse(inputjs) as JSONObject, uuid)
    }

    fun companioncreatedraftpsafrommypc(){
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        var hash = mutableMapOf<String, String>()
        var psa  = PSADSLProcessor()
        psa.render(copy)
        hash.put("Brutto", "12000")
        hash.put("Sor", "0.2")
        hash.put("Metal", "5А")
        hash.put("DepId", "25")
        hash.put("PlateNumber", "VTB100")
        hash.put("UUID",  timeBasedUUID.generate())
        hash.put("Type", "black")
        PSADSLProcessor.createdraftPSA(hash as HashMap<String, String>, copy, psa )

    }




    fun testGetMetalId() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        psa.render(copy)
        assertEquals(5, psa.getMetalId("Медь"))
        assertEquals(14, psa.getMetalId("3A"))


    }

    fun testGetinvägning() {
        val str: String = String(File("example.json").readBytes())
        var psa  = PSADSLProcessor()
        //assertNotEquals(null, psa.color(str))

    }

    fun testColor() {
        var psa  = PSADSLProcessor()
        assertEquals(1, psa.DepsMap.get(6))
        assertEquals(25, psa.DepsMap.get(9))

    }

    fun testProcessfarg() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        psa.render(copy)
        println("rendred succes!")
       //// psa.processfarg(String(File("example.json").readBytes()))
    }

    fun testProcessfarg2() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        psa.render(copy)
        println("rendred succes!")
    ////    psa.processfarg(String(File("example2.json").readBytes()))
    }

    fun testProcessfarg3() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        psa.render(copy)
        println("rendred succes!")
     /////   psa.processfarg(String(File("example3.json").readBytes()))
    }

    fun testNoneJson(){
        val etalon = "http://192.168.0.126:8888/psa/psa/num"
        val copy="'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::json{http://192.168.0.126:8888/psa/psa/num}."
        var psa  = PSADSLProcessor()
        psa.render(copy)
        assertEquals(etalon, psa.json_)

    }

    fun GetPSANumberviaDSL() {
        var psa  = PSADSLProcessor()
        val PSASearchProcessor = PSASearchProcessor()
        psaconnector.render(initDB)
        psa.executor = psaconnector.executor
        PSASearchProcessor.executor = psaconnector.executor
        psa.psearch = PSASearchProcessor
      //  assertEquals("2942", psa.getPSANumberviaDSL("1"))
    //    assertEquals("3323", psa.getPSANumberviaDSL("2"))
     //   assertEquals("5591", psa.getPSANumberviaDSL("24"))
        assertEquals("3358", psa.getPSANumberviaDSL("25"))
        assertEquals("1", psa.getPSANumberviaDSL("26"))

        assertEquals("6", psa.getPSANumberviaDSL("27"))
    }

    fun testProcessinvagning() {}

    fun testExtractSummary() {
        val input = String(Files.readAllBytes(File("input.js").toPath()))
        val etalon = """[{"cost":4736.16,"median":52,"weight":91.08,"psaid":12},{"cost":1767.15,"median":105,"weight":16.83,"psaid":2},{"cost":54934.950000000004,"median":565,"weight":97.23,"psaid":5},{"cost":49829.45,"median":99.75,"weight":499.53,"psaid":6},{"cost":21968.25,"median":345.47,"weight":63.59,"psaid":8},{"cost":1425.95,"median":95,"weight":15.01,"psaid":9}]""";
        assertNotEquals(null, input)
      ////  println(input)
        val psa = PSADSLProcessor()
        val sum = psa.extractSummary(input)
        assertNotEquals(null, sum)
        println("\n\n\n\nsummary $sum")
        assertEquals(etalon, psa.convertToList(sum))
        assertNotEquals(null, psa.convertToListJSON(psa.convertToList(sum)))
    }

    fun testprocessfarg() {
        val input = """{"id":37115,"waybill":18,"date":"2021-04-27","time":"17:32:06","comment":"\u0441\u043b\u0430\u0432\u0430","exportId":18,"department":{"id":10,"name":"\u041f\u0417\u0423 3 \u0420\u041e\u0416\u0414 \u0426\/\u041c","value":10,"text":"\u041f\u0417\u0423 3 \u0420\u041e\u0416\u0414 \u0426\/\u041c"},"departmentId":10,"totalMass":88.35,"totalPrice":7951.5,"weighings":[{"id":83100,"trash":1,"clogging":5,"tare":0,"brutto":94,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":88.35,"price":"90.00","totalPrice":7951.5,"newPrice":"90.00","calculatedMass":"7951.50"}],"customer":48,"totalPaidAmount":7951.5,"hasBeenPaid":true,"oldCustomer":48,"uuid":"288c6e828f7067d624a4b7a46e2857cd","summary":{"6":{"weight":88.35,"cost":7951.5,"median":90,"psaid":6}}}"""
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."

        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor=psaconnector.executor
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.executor=psaconnector.executor
        psa.psearch=PSASearchProcessor
        PSADSLProcessor.processColorPSA(input, "288c6e828f7067d624a4b7a46e2857cd",copy, psa)

    }

}