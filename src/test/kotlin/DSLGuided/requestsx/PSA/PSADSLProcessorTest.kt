package DSLGuided.requestsx.PSA
import junit.framework.TestCase
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.abstractions.timeBasedUUID
import java.nio.file.Files

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
        psa.executor= psaconnector.executor!!
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
        psearch.executor = psaconnector.executor!!
        psa.executor= psaconnector.executor!!
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
        psearch.executor = psaconnector.executor!!
        psa.executor= psaconnector.executor!!
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
        psa.executor= psaconnector.executor!!
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
        psa.executor= psaconnector.executor!!
        psa.render(copy)
        println("rendred succes!")
       //// psa.processfarg(String(File("example.json").readBytes()))
    }

    fun testProcessfarg2() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor= psaconnector.executor!!
        psa.render(copy)
        println("rendred succes!")
    ////    psa.processfarg(String(File("example2.json").readBytes()))
    }

    fun testProcessfarg3() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor= psaconnector.executor!!
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
        psa.executor = psaconnector.executor!!
        PSASearchProcessor.executor = psaconnector.executor!!
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

    fun processfarg() {
        val input = """{"id":36181,"waybill":7,"date":"2021-04-15","time":"08:33:41","comment":"\u041b\u0451\u0448\u0430 ","exportId":7,"department":{"id":9,"name":"\u041f\u0417\u0423 2 \u0411\u0410\u0411\u0410\u0415\u0412\u0421\u041a\u041e\u0413\u041e \u0426\/\u041c","value":9,"text":"\u041f\u0417\u0423 2 \u0411\u0410\u0411\u0410\u0415\u0412\u0421\u041a\u041e\u0413\u041e \u0426\/\u041c"},"departmentId":9,"totalMass":2547.59,"totalPrice":744516.75,"weighings":[{"id":80835,"trash":1,"clogging":5,"tare":0,"brutto":492,"metal":{"id":15,"name":"\u041d\u0435\u0440\u0436\u0430\u0432\u0435\u0439\u043a\u0430","def":false,"psaid":2},"metalId":15,"mass":466.45,"price":"105.00","totalPrice":48977.25,"newPrice":"105.00","calculatedMass":"48977.25"},{"id":80836,"trash":0,"clogging":1,"tare":2,"brutto":215,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":210.87,"price":"110.00","totalPrice":23195.7,"newPrice":"110.00","calculatedMass":"23195.70"},{"id":80837,"trash":0,"clogging":1,"tare":459,"brutto":625,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":164.34,"price":"110.00","totalPrice":18077.4,"newPrice":"110.00","calculatedMass":"18077.40"},{"id":80838,"trash":0,"clogging":0.5,"tare":529,"brutto":911,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":380.09,"price":"110.00","totalPrice":41809.9,"newPrice":"110.00","calculatedMass":"41809.90"},{"id":80839,"trash":0,"clogging":15,"tare":459,"brutto":556,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":82.45,"price":"110.00","totalPrice":9069.5,"newPrice":"110.00","calculatedMass":"9069.50"},{"id":80840,"trash":0,"clogging":1,"tare":0,"brutto":8,"metal":{"id":11,"name":"\u0411\u0440\u043e\u043d\u0437\u0430 \u043a\u0443\u0441\u043a\u043e\u0432\u0430\u044f","def":false,"psaid":7},"metalId":11,"mass":7.92,"price":"365.00","totalPrice":2890.8,"newPrice":"365.00","calculatedMass":"2890.80"},{"id":80841,"trash":0,"clogging":10,"tare":0,"brutto":9,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":8.1,"price":"565.00","totalPrice":4576.5,"newPrice":"565.00","calculatedMass":"4576.50"},{"id":80842,"trash":0.6,"clogging":1,"tare":0,"brutto":40,"metal":{"id":10,"name":"\u0420\u0430\u0434\u0438\u0430\u0442\u043e\u0440 \u043b\u0430\u0442\u0443\u043d\u043d\u044b\u0439","def":false,"psaid":8},"metalId":10,"mass":39.01,"price":"350.00","totalPrice":13653.5,"newPrice":"350.00","calculatedMass":"13653.50"},{"id":80843,"trash":0,"clogging":1,"tare":0,"brutto":28,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":27.72,"price":"110.00","totalPrice":3049.2,"newPrice":"110.00","calculatedMass":"3049.20"},{"id":80844,"trash":0,"clogging":0.5,"tare":2,"brutto":449,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":444.77,"price":"565.00","totalPrice":251295.05,"newPrice":"565.00","calculatedMass":"251295.05"},{"id":80845,"trash":0.3,"clogging":5,"tare":0,"brutto":109,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":103.27,"price":"565.00","totalPrice":58347.55,"newPrice":"565.00","calculatedMass":"58347.55"},{"id":80846,"trash":0,"clogging":0.5,"tare":448,"brutto":714,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":264.67,"price":"565.00","totalPrice":149538.55,"newPrice":"565.00","calculatedMass":"149538.55"},{"id":80847,"trash":0,"clogging":0.6,"tare":459,"brutto":788,"metal":{"id":9,"name":"\u041b\u0430\u0442\u0443\u043d\u044c","def":false,"psaid":8},"metalId":9,"mass":327.03,"price":"345.00","totalPrice":112825.35,"newPrice":"345.00","calculatedMass":"112825.35"},{"id":80848,"trash":0,"clogging":5,"tare":0,"brutto":22,"metal":{"id":9,"name":"\u041b\u0430\u0442\u0443\u043d\u044c","def":false,"psaid":8},"metalId":9,"mass":20.9,"price":"345.00","totalPrice":7210.5,"newPrice":"345.00","calculatedMass":"7210.50"}],"customer":141,"totalPaidAmount":744516.75,"hasBeenPaid":true,"oldCustomer":141,"uuid":"44af4d94b8d5eb7b2837b75ed12286ed","summary":{"2":{"weight":466.45,"cost":48977.25,"median":105,"psaid":2},"6":{"weight":865.47,"cost":95201.7,"median":110,"psaid":6},"7":{"weight":7.92,"cost":2890.8,"median":365,"psaid":7},"5":{"weight":820.81,"cost":463757.64999999997,"median":565,"psaid":5},"8":{"weight":386.94,"cost":133689.35,"median":345.5,"psaid":8}}}"""
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."

        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor= psaconnector.executor!!
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.executor= psaconnector.executor!!
        psa.psearch=PSASearchProcessor
        PSADSLProcessor.processColorPSA(input, "44af4d94b8d5eb7b2837b75ed12286ed",copy, psa)

    }

    fun testpsanumber(){
        var psa  = PSADSLProcessor()
        val PSASearchProcessor = PSASearchProcessor()
        psaconnector.render(initDB)
        psa.executor = psaconnector.executor!!
        PSASearchProcessor.executor = psaconnector.executor!!
        psa.psearch = PSASearchProcessor
        val number = psa.getPSANumberviaDSL("2")
        println("\n\n\n$number")
    }

    fun Splitpsa() {
        val uuid = "47faa886-af17-11eb-a1d4-ef392b41b763"
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."

        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        psa.executor= psaconnector.executor!!
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.executor= psaconnector.executor!!
        psa.psearch=PSASearchProcessor
        psa.splitpsa(uuid)
    }

}