package DSLGuided.requestsx.PSA
import junit.framework.TestCase
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.abstractions.timeBasedUUID
import se.roland.util.HTTPClient
import java.nio.file.Files
import java.util.ArrayList

import java.util.HashMap
import java.io.File as File

class PSADSLProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testjs(){
        val js = "{\"a\":12, \"b\":22}"
        val obj = JSONParser().parse(js) as JSONObject
        assertNull(obj.get("dd"))
        val some = obj.get("kkk")
        if (some == null)
            print("test passed")
        assertNull(some)
    }

    fun testRender() {
      //  val initialdsl = "'psa2'=>::psa{'urldb':'jdbc:mysql://192.168.0.121:3306/psa','login':user123,'pass':password },::psagetNumberfrom('url':http://192.168.0.121:8080/psa/psa/num,'keyparam':department_id),::stupid{http://192.168.0.121:8080/psa/psa/num}"
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
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
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
        psa.render(copy)
        hash.put("Brutto", "12000")
        hash.put("Sor", "0.2")
        hash.put("Metal", "5А")
        hash.put("DepId", "25")
        hash.put("PlateNumber", "VTB100")
        hash.put("UUID",  timeBasedUUID.generate())
        hash.put("Type", "black")
        hash.put("Section", "1")
        PSADSLProcessor.createdraftPSA(hash as HashMap<String, String>, copy, psa )

    }

    fun processinvagning(){
        val parser = JSONParser()
        val inputjs = "{\"brutto\":16,\"calculatedMass\":\"1267.20\",\"metalId\":15,\"totalPrice\":0,\"tare\":0,\"price\":\"80\",\"clogging\":1,\"mass\":15.84,\"metal\":{\"def\":false,\"name\":\"Нержавейка\",\"psaid\":2,\"id\":15},\"id\":83580,\"newPrice\":\"80\",\"trash\":0}"
        val copy= "'psa2'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}"
        val uuid = "48bd68d2834fb341fdc03a92a02dd88a"
        var hash = mutableMapOf<String, String>()
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
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
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
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
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
        psa.render(copy)
        println("rendred succes!")
       //// psa.processfarg(String(File("example.json").readBytes()))
    }

    fun testProcessfarg2() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
        psa.render(copy)
        println("rendred succes!")
    ////    psa.processfarg(String(File("example2.json").readBytes()))
    }

    fun testProcessfarg3() {
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val psearch = PSASearchProcessor()
        psearch.psaconnector = psaconnector
        psa.psearch=psearch
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
        PSASearchProcessor.psaconnector = psaconnector
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
        //assertNotEquals(null, input)
      ////  println(input)
        val psa = PSADSLProcessor()
        val sum = psa.extractSummary(input)
      //  assertNotEquals(null, sum)
        println("\n\n\n\nsummary $sum")
        assertEquals(etalon, psa.convertToList(sum))
        //assertNotEquals(null, psa.convertToListJSON(psa.convertToList(sum)))
    }

    fun processfarg() {
        val input = """{"id":36181,"waybill":7,"date":"2021-04-15","time":"08:33:41","comment":"\u041b\u0451\u0448\u0430 ","exportId":7,"department":{"id":9,"name":"\u041f\u0417\u0423 2 \u0411\u0410\u0411\u0410\u0415\u0412\u0421\u041a\u041e\u0413\u041e \u0426\/\u041c","value":9,"text":"\u041f\u0417\u0423 2 \u0411\u0410\u0411\u0410\u0415\u0412\u0421\u041a\u041e\u0413\u041e \u0426\/\u041c"},"departmentId":9,"totalMass":2547.59,"totalPrice":744516.75,"weighings":[{"id":80835,"trash":1,"clogging":5,"tare":0,"brutto":492,"metal":{"id":15,"name":"\u041d\u0435\u0440\u0436\u0430\u0432\u0435\u0439\u043a\u0430","def":false,"psaid":2},"metalId":15,"mass":466.45,"price":"105.00","totalPrice":48977.25,"newPrice":"105.00","calculatedMass":"48977.25"},{"id":80836,"trash":0,"clogging":1,"tare":2,"brutto":215,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":210.87,"price":"110.00","totalPrice":23195.7,"newPrice":"110.00","calculatedMass":"23195.70"},{"id":80837,"trash":0,"clogging":1,"tare":459,"brutto":625,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":164.34,"price":"110.00","totalPrice":18077.4,"newPrice":"110.00","calculatedMass":"18077.40"},{"id":80838,"trash":0,"clogging":0.5,"tare":529,"brutto":911,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":380.09,"price":"110.00","totalPrice":41809.9,"newPrice":"110.00","calculatedMass":"41809.90"},{"id":80839,"trash":0,"clogging":15,"tare":459,"brutto":556,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":82.45,"price":"110.00","totalPrice":9069.5,"newPrice":"110.00","calculatedMass":"9069.50"},{"id":80840,"trash":0,"clogging":1,"tare":0,"brutto":8,"metal":{"id":11,"name":"\u0411\u0440\u043e\u043d\u0437\u0430 \u043a\u0443\u0441\u043a\u043e\u0432\u0430\u044f","def":false,"psaid":7},"metalId":11,"mass":7.92,"price":"365.00","totalPrice":2890.8,"newPrice":"365.00","calculatedMass":"2890.80"},{"id":80841,"trash":0,"clogging":10,"tare":0,"brutto":9,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":8.1,"price":"565.00","totalPrice":4576.5,"newPrice":"565.00","calculatedMass":"4576.50"},{"id":80842,"trash":0.6,"clogging":1,"tare":0,"brutto":40,"metal":{"id":10,"name":"\u0420\u0430\u0434\u0438\u0430\u0442\u043e\u0440 \u043b\u0430\u0442\u0443\u043d\u043d\u044b\u0439","def":false,"psaid":8},"metalId":10,"mass":39.01,"price":"350.00","totalPrice":13653.5,"newPrice":"350.00","calculatedMass":"13653.50"},{"id":80843,"trash":0,"clogging":1,"tare":0,"brutto":28,"metal":{"id":14,"name":"\u0410\u043b\u044e\u043c\u0438\u043d\u0438\u0439 \u0445\u043b\u0430\u043c","def":false,"psaid":6},"metalId":14,"mass":27.72,"price":"110.00","totalPrice":3049.2,"newPrice":"110.00","calculatedMass":"3049.20"},{"id":80844,"trash":0,"clogging":0.5,"tare":2,"brutto":449,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":444.77,"price":"565.00","totalPrice":251295.05,"newPrice":"565.00","calculatedMass":"251295.05"},{"id":80845,"trash":0.3,"clogging":5,"tare":0,"brutto":109,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":103.27,"price":"565.00","totalPrice":58347.55,"newPrice":"565.00","calculatedMass":"58347.55"},{"id":80846,"trash":0,"clogging":0.5,"tare":448,"brutto":714,"metal":{"id":8,"name":"\u041c\u0435\u0434\u044c \u043c\u0438\u043a\u0441","def":false,"psaid":5},"metalId":8,"mass":264.67,"price":"565.00","totalPrice":149538.55,"newPrice":"565.00","calculatedMass":"149538.55"},{"id":80847,"trash":0,"clogging":0.6,"tare":459,"brutto":788,"metal":{"id":9,"name":"\u041b\u0430\u0442\u0443\u043d\u044c","def":false,"psaid":8},"metalId":9,"mass":327.03,"price":"345.00","totalPrice":112825.35,"newPrice":"345.00","calculatedMass":"112825.35"},{"id":80848,"trash":0,"clogging":5,"tare":0,"brutto":22,"metal":{"id":9,"name":"\u041b\u0430\u0442\u0443\u043d\u044c","def":false,"psaid":8},"metalId":9,"mass":20.9,"price":"345.00","totalPrice":7210.5,"newPrice":"345.00","calculatedMass":"7210.50"}],"customer":141,"totalPaidAmount":744516.75,"hasBeenPaid":true,"oldCustomer":141,"uuid":"44af4d94b8d5eb7b2837b75ed12286ed","summary":{"2":{"weight":466.45,"cost":48977.25,"median":105,"psaid":2},"6":{"weight":865.47,"cost":95201.7,"median":110,"psaid":6},"7":{"weight":7.92,"cost":2890.8,"median":365,"psaid":7},"5":{"weight":820.81,"cost":463757.64999999997,"median":565,"psaid":5},"8":{"weight":386.94,"cost":133689.35,"median":345.5,"psaid":8}}}"""
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."

        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        PSADSLProcessor.processColorPSA(input, "44af4d94b8d5eb7b2837b75ed12286ed",copy, psa)

    }

    fun testpsanumber(){
        var psa  = PSADSLProcessor()
        val PSASearchProcessor = PSASearchProcessor()
        psaconnector.render(initDB)
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        val number = psa.getPSANumberviaDSL("2")
        println("\n\n\n$number")
    }

    fun Splitpsa() {
        val uuid = "47faa886-af17-11eb-a1d4-ef392b41b763"
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."

        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.splitpsa(uuid)
    }

    fun hookSplitpsa() {
        val uuid = "47faa886-af17-11eb-a1d4-ef392b41b763"
        val copy= "'psa'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(initDB)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(copy)
        assertEquals("12", psa.HOOKSECTION)
        assertEquals("555", psa.HOOKUUID)
    }


    fun testGetNONE() {
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        val filename = "dsl.dump"
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        var JSON = String(Files.readAllBytes(File("color-psa-with-secton.js").toPath()))
        PSADSLProcessor.processColorPSA(JSON, timeBasedUUID.generate(), psastr, psa)
        if (!File(filename).exists())
            Saver.Saver.write(psa.external_searchdsl.toByteArray(), filename)
        PSASearchProcessor.render(String(Files.readAllBytes(File(filename).toPath())))
        val res = PSASearchProcessor.getPSA()
        var counter = 0
        while (res!!.next())
            counter++
     ///   assertEquals(1, counter)
    }

    fun testPSAIDHOOK() {
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.TRUE_ATOM, psa.PSAIDHOOK )
        assertEquals(psa.SECTION, "1" )
        assertEquals(psa.PSAID, "3" )
    }

    val json ="{\"id\":42631,\"waybill\":4,\"date\":\"2021-08-12\",\"time\":\"14:17:31\",\"section\":\"2\",\"comment\":\"\\u0430\\u0431\\u044b\\u0440\\u0432\\u0430\\u043b\\u0433\",\"exportId\":4,\"uuid\":\"aeb38bcfa3ee84eb0d43fbceb7d9fdea\",\"department\":{\"id\":10,\"name\":\"\\u041f\\u0417\\u0423 3 \\u0420\\u041e\\u0416\\u0414 \\u0426\\/\\u041c\",\"value\":10,\"text\":\"\\u041f\\u0417\\u0423 3 \\u0420\\u041e\\u0416\\u0414 \\u0426\\/\\u041c\"},\"departmentId\":10,\"totalMass\":12,\"totalPrice\":2664,\"weighings\":[{\"id\":96745,\"trash\":0,\"clogging\":0,\"tare\":0,\"brutto\":12,\"metal\":{\"id\":65,\"name\":\"5\\u0410\\u0416\\u0414\",\"def\":false,\"psaid\":3},\"metalId\":65,\"mass\":12,\"price\":\"55\",\"totalPrice\":2664,\"newPrice\":\"55\",\"calculatedMass\":\"660.00\"}],\"customer\":381,\"totalPaidAmount\":0,\"hasBeenPaid\":false,\"oldCustomer\":381,\"summary\":{\"3\":{\"weight\":12,\"cost\":660,\"median\":55,\"psaid\":3}}}"
    fun testPSAIDHOOK2() {
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.TRUE_ATOM, psa.PSAIDHOOK )
        assertEquals(psa.SECTION, "1" )
        assertEquals(psa.PSAID, "3" )

        PSADSLProcessor.processColorPSA(json, "22222",psastr,psa)
    }

    fun testCheckUnique() {
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.COMPANY_ATOM, psa.getUniqueClient("3017032528").first)
        assertEquals(0, psa.getUniqueClient("1210").size)
    }

    fun testF() {
        var psa  = PSADSLProcessor()
        val FIO = "Краснов Кирилл Вадимович"
        assertEquals("Краснов", psa.F_(FIO))
        assertEquals("Кирилл", psa.I_(FIO))
        assertEquals("Вадимович", psa.O_(FIO))
    }

    fun testCalculateUnique(){
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        val FIO = "Краснов Кирилл Вадимович"
        val R = psa.getUniqueClient(FIO)
        assertEquals(2, R.size)
        println(R.last)
        assertEquals(28372, R.last)
    }

    fun testCalculateMegafon(){
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        val COMPANY = "МегаФон"
        val R = psa.getUniqueClient(COMPANY)
        assertEquals(2, R.size)
        println(R.last)
        assertEquals(3, R.last)
        val trans = "транслом"
        val R_ = psa.getUniqueClient(trans)
        println("::${R_.last}")
    }


    fun testcreatePSAfromJSwithclient() {
        val input = String(Files.readAllBytes(File("client.js").toPath()))
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        var psa  = PSADSLProcessor()
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        PSADSLProcessor.processColorPSA(input,"565656565", psastr, psa)
    }

    fun testGetActivatePSA() {
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::activatePSA{false},::urltoActivate{url},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.FALSE_ATOM, psa.ACTIVATE_PSA)
        assertEquals("url", psa.URL_TO_ACTIVATE)
    }

    fun testConstructURLwithId() {
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.TRUE_ATOM, psa.ACTIVATE_PSA)
        assertEquals("http://192.168.0.126:15000/psa/psa/gettest?id=12", psa.constructURLwithId(12))
    }

    fun testCheckpass() {
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(false, psa.checkpass("1203","855467"))
        assertEquals(true, psa.checkpass("1203","855464"))
        assertEquals(true, psa.checkpass("HHHH","opoppoop"))
    }

    fun testStandartize() {
        val psa = PSADSLProcessor()
        val str1 = "0.1"
        val str2 = "0.02"
        val str3 = "1.8"
        val str4 = "0.18"
        assertEquals("00.10", psa.standartize(str1))
        assertEquals("00.02", psa.standartize(str2))
        assertEquals("01.80", psa.standartize(str3))
        assertEquals("00.18", psa.standartize(str4))


    }

    fun ActivatePSA() {
        val input= """[
            {"uuid":"55dbdbf8aa68cea9e95da072cf37132a"},
            {"uuid":"0e80a9f66f4dc1e4e7311a2d2beaa854"},
            {"uuid":"bdc650902f27b1d62a6df75544c299ab"},
            {"uuid":"9aabe3550b110ae1d506157545805b80"},
            {"uuid":"888263a34c1b5c18b90f1e5a7feb3c57"},
            {"uuid":"39a318de5b8bc59b8dc212cbc3c18ed2"},
            {"uuid":"767fa1044f038db0a687626ba1694d4d"},
            {"uuid":"fbb84df73beabad4cb1e273b3c454965"},
            {"uuid":"40bc564b9ddb6a3ccd0b6008d92df4a5"},
            {"uuid":"d13baa0e940c3d94cb547dbc9aff9130"},
            {"uuid":"288aa24eb0fe406900f65446d2280e14"},
            {"uuid":"a0cdb1e8c76a91a61ece4760e1a09f22"},
            {"uuid":"04d42902e168c14a8bd62756c5401634"},
            {"uuid":"40633ad4032d4d498880fe0ed48bedaa"},
            {"uuid":"8a8dad0a68b19d3d55a0e5d9e66dfc13"},
            {"uuid":"feb82260d718460d515e788e8ff463f7"},
            {"uuid":"c0344ac21271acd396ec13d51e39567d"},
            {"uuid":"705940ab57e694e30e59391d6f580ce3"},
            {"uuid":"834a00cc909972aee4fa7b1822565d0b"},
            {"uuid":"4b3c09ddad01ffb74d821a77209e5abd"},
            {"uuid":"314a69b47ab865dd408776c7cec5aeaa"},
            {"uuid":"b8f6e01c87f8c432e6abf87f87aef395"},
            {"uuid":"e0a8e7ec2c9ab2308cc1e1d42ec44177"},
            {"uuid":"63fcfdd5d805dbf2e7a45887bfc9bea8"},
            {"uuid":"aa699f54102413cfda0ca671b196813b"},
            {"uuid":"30732690659e5d5b001bd21d2082b2b1"},
            {"uuid":"f0e8a964630479002c12cedd1cea995f"},
            {"uuid":"b6e1bda490db822df840c8330aaa05ba"},
            {"uuid":"65a12476d8fbf6ec9b3d0f2b7973521a"},
            {"uuid":"f89b675854d77a8b74583141b4989780"},
            {"uuid":"b5129a6ea8e9fc7e1db4af2983438551"},
            {"uuid":"8efaefb7b5d7e42156b5d39b6ed0307f"},
            {"uuid":"6a66f6baf25acdfc28a6a8d3368201ec"},
            {"uuid":"2b59e0f9768475b67ecd7ba0d0f5613d"},
            {"uuid":"c6cb1c8b90236e1e2a4a9863b3ff29b3"},
            {"uuid":"40ccc62e725ae0ba54fad27da1014057"},
            {"uuid":"5a14f0841de46aec971ddba968190c6f"},
            {"uuid":"c5bf10f2ae258517ebadf35f8535259b"},
            {"uuid":"a5dedf7f25d9a6448afbfbc01df99374"},
            {"uuid":"8e7693de851850e7b0955ca7685d1aec"},
            {"uuid":"05de91ffe134c127fa014de6fb4f172d"},
            {"uuid":"7d0e97463dd61b0d68ae07aac91f1962"},
            {"uuid":"6f7cdc314322d1e776a11f1f4f3ec0ee"},
            {"uuid":"778d1da136d3f2bd4b3f1fd5c86e61fd"},
            {"uuid":"cbf41d7cf2c30ef7e73f8d412241a95c"},
            {"uuid":"8ed017def5bb15103f46bbaf9405f26b"},
            {"uuid":"c230ebc276ea2c28a1ed71432a7ee2ba"},
            {"uuid":"298958d45423a88d11de613b8eee4f61"},
            {"uuid":"0495d60d6937ada8b5bc7728afb1b95e"},
            {"uuid":"d5f474de6f6e454122fbf9f6a8637664"},
            {"uuid":"9bffceebf7ab36785e6a593bb0874704"},
            {"uuid":"364bc766ea31549b555a04447f49b52c"},
            {"uuid":"288804d0549e50109fe6196605daea64"},
            {"uuid":"993b276015ce311476dff1935ceea3c3"},
            {"uuid":"1d752ce9fd63218ec13c65889a41d79f"},
            {"uuid":"7184aba4015f66362d51b81c8ea33138"},
            {"uuid":"36a17fc546565bfe6152a4808da8b69b"},
            {"uuid":"124354aec28b0ab52cd925a9f51faab3"},
            {"uuid":"8ebb0e7b1f9909a1aaa4e07732242c55"},
            {"uuid":"c3cd4fbe49e722563154ca878b639390"},
            {"uuid":"71a5f2dbd152f93b05e41a42410708f9"},
            {"uuid":"79522e51ff0cce1d04fa9538b998866f"},
            {"uuid":"15844fb6488c8d2c094d625b364789b4"},
            {"uuid":"ca59c12fd989177f44f7e30ef9508d85"},
            {"uuid":"983e48f45f83f3f899d39a4e3ad042c1"},
            {"uuid":"706102cceb1fd5d2384ba8a465b06434"},
            {"uuid":"38530a953195b3f22dfdcf94d36380b5"},
            {"uuid":"f7ccc93927243205a6e280c7343a46da"},
            {"uuid":"c0f950b0423b7714b4006808f3500fbe"},
            {"uuid":"61f2c8afc8fb38dfab67075e09f74147"},
            {"uuid":"34e07320baca1517602035697cbfe9e2"},
            {"uuid":"821dbc083137e07d280e406e408e9497"},
            {"uuid":"a28449c0ceb44af83ea72733573bbf19"},
            {"uuid":"b6266d941f3b85c42d0b7341c322e075"},
            {"uuid":"3d21dae222ac843a2943bff28447b315"},
            {"uuid":"33b895ed4231ccfb454bf73dfdc6cece"},
            {"uuid":"71beedb881a38a86d3e9f7004839de95"},
            {"uuid":"39bc5276fb8f63fd734d6082ccd0f363"},
            {"uuid":"cc5ecae9cc2f6aea7525bdb5fabb09eb"},
            {"uuid":"1c975352874c47c4c3425903ca0130f2"},
            {"uuid":"d0ad71e8a08b192387fd11309f6c5527"},
            {"uuid":"7189b62665fcff7587eeb86b01441e75"},
            {"uuid":"93b54ff4120e36b8a4405affad9239f3"},
            {"uuid":"29a94fa091c39cd534d6ca3eb4f50573"},
            {"uuid":"248c6bc63ca027e9065f37011de0f539"},
            {"uuid":"d8fb3ec3466542f9a0588c1c26649d47"},
            {"uuid":"3dd9cc348ba058344bc2d8032654c334"},
            {"uuid":"f200345cb8dc4df8b15aed12258c8d38"},
            {"uuid":"71c96f3e1643b0a85d836828fc1b3bb1"},
            {"uuid":"31b2a437cb899eda36b1f44f3753b463"},
            {"uuid":"9cb0621290624d2493a1d5cb6f308ebd"},
            {"uuid":"71b37385020449bfcf9ded354898673d"},
            {"uuid":"4b23d51f37e2c0f11600b6406b3a7689"},
            {"uuid":"99d789cebeb52acf405e66497aa23199"},
            {"uuid":"e13e378a801d353e1d3b284d3468c516"},
            {"uuid":"d12d254f6ae70ad65159f9b7bc6a3bb0"},
            {"uuid":"8c9de75bac264ccf37b50868af9e1be8"},
            {"uuid":"fc17f33d1787bb00b5af444bca153209"},
            {"uuid":"661b99774ab6d08ccf995c8f7e9d33f7"},
            {"uuid":"e5d8f7d1a1a77c0a1042f091bd6f7b76"},
            {"uuid":"5ab01de0b400dbf075b4df3ec02b9741"},
            {"uuid":"8bfc17c6d33ced7c90dbfd908bfd5c33"},
            {"uuid":"7ba30abf24744769fbed355b83fdfb98"},
            {"uuid":"0e3344d88608a38a3231ea7885ff81fa"},
            {"uuid":"ad1e1cfaf32f825caf8096a215f242d8"},
            {"uuid":"e4825d2e3df7fa3a89ad7f64f7687282"},
            {"uuid":"3fe98f5c772e102a1f79d9dd1d6c4a7c"},
            {"uuid":"63e87971e8faa4820066c30e865d9eb3"},
            {"uuid":"203e0335ecba9bbe209122820d629411"},
            {"uuid":"f33b54bca7a79fa1f9675dfd7882530f"},
            {"uuid":"3fffda1aa6e062b709902448bf4f89a3"},
            {"uuid":"782e8f827c3983ec33ce2c9d99d704e2"},
            {"uuid":"a7c3a0cb58cecd91025ce7eb8b146a1f"},
            {"uuid":"b787ac69d7c5490337495ab87b101e11"},
            {"uuid":"0ba5a64f682cd04a4a01446263c22794"},
            {"uuid":"ed3f1153635afa699a3f942a4240be19"},
            {"uuid":"3a899f635c917edb7f7e554f2f2dba53"},
            {"uuid":"d134fb39b45862ae517b627490463fca"},
            {"uuid":"8dc78ee3c9fb7fd2b7f9482f1ae39141"},
            {"uuid":"65a62b1caf103afe46fdf595cf5b81b4"},
            {"uuid":"16e5380b674d25f556b3fbd87acde9af"},
            {"uuid":"58a080903354366cb7d4cbe53c8930cf"},
            {"uuid":"ebf8e8c98600e9da608de2d237777f2a"},
            {"uuid":"054a98cf3511168114b0afc88937059a"},
            {"uuid":"d260d17f75667658613f85ff28e2f10b"},
            {"uuid":"70530b5e34a2828a738e67e3a5146df5"},
            {"uuid":"8336104ed10871edae0acbd3570dfb82"},
            {"uuid":"18b9ffa4b7c55765763c5117ee4e98a7"},
            {"uuid":"61dd515ae9a3d82150c8138f836f3430"},
            {"uuid":"bb1d3bcccd0f9160adb46b513a2031b9"},
            {"uuid":"0fa517d952eb9f84d7c0f287182bde46"},
            {"uuid":"61a09b52e4638310a3028ab882f3a138"},
            {"uuid":"7b7c8d028d82d084fb31edb3bb088fc4"},
            {"uuid":"aaddeafd224fc10a0e56c30231e22761"},
            {"uuid":"b42adcdc643ff1c8b52ae8b06370a4a3"},
            {"uuid":"792b51542752a21d1c43e3f2c0e1f864"},
            {"uuid":"caa935f4e3defbe8e111e24b9e825bf2"},
            {"uuid":"78c3f547076bcffe3f0bde85d3bffeca"},
            {"uuid":"1702ce5cc4112b3ade2ecd7ba2e12189"},
            {"uuid":"759caee62ef907fb8a40854dde66465d"},
            {"uuid":"5483b864b79b9a9e8a1cb313d91bbadf"},
            {"uuid":"f2980770496493b0cd89ea896ea740c8"},
            {"uuid":"9a364ccddb13bd61a37d6a57a2e00e0c"},
            {"uuid":"8c6d88b780de30df599c7a06f52f2ec6"},
            {"uuid":"58507163c4aaaac755bffcc8507f6c89"},
            {"uuid":"1f892a49c3e6cf53e307b468862a4b92"},
            {"uuid":"732b0640ec7c94624a304899a7b76069"},
            {"uuid":"7527ffeaee32fc79c0c8c4229600637e"},
            {"uuid":"c73a56872a7acbf077c26fac6b9129db"},
            {"uuid":"5d7172954308d84738ead9a708159f0e"},
            {"uuid":"ae4472420e4484f26ffdb1eef67e2607"},
            {"uuid":"4a14a710615a092e8b808fd8363d55e0"},
            {"uuid":"da07a410997a763bb3e9190a55897fa5"},
            {"uuid":"e1486d6922e58ea24d688c47541b7ea0"},
            {"uuid":"b6013001cef95fad31dc3ecd163012a4"},
            {"uuid":"fce69c8b046b185800b74f4d5a3c606d"},
            {"uuid":"486186a6ec22d9619a6087dad7515519"},
            {"uuid":"b5d21c6dbfca594e5fa0d3f9734d34de"},
            {"uuid":"c41077f4016ae78c77334d7a35d484f7"},
            {"uuid":"e9bfe35351dfa41eb5ab541660e8ec48"},
            {"uuid":"6a9fd965c68b3cfe68935039a037954f"},
            {"uuid":"e91d2dadad05ce346fcebfdd280ec0ec"},
            {"uuid":"667919517ce410a981d7ed207c40f4e8"},
            {"uuid":"c204c6c29156c98b59f07b1956920a57"},
            {"uuid":"1afdb86b0b3a5890c91c0d462a3b3125"},
            {"uuid":"719988154d18c65e12db3c7985d7229e"},
            {"uuid":"e162a844d7ad1f7dc3eeaa5ead86d366"},
            {"uuid":"b721bda85501593eebc7f170caedcb60"},
            {"uuid":"6507b83e24da8581b839d2c5ada0afb4"},
            {"uuid":"8e95f1f6275bbf9518b777cc025c05a7"},
            {"uuid":"069b1f34533bf80b4ebb8cbc520357c0"},
            {"uuid":"90f4171ffef235520a73508e161de86a"},
            {"uuid":"57c74d52f2f2edb105c37837472eba4e"},
            {"uuid":"ff744ad55522fdb22b98ad2241bef46b"},
            {"uuid":"87cf365ea044dc8b2419d7aa20d0fa5f"},
            {"uuid":"d964e2fa0c835098e222d134e7dd93fe"},
            {"uuid":"d1676d8d879ee2ba7e823dcee2db0d1e"},
            {"uuid":"97ee23ff29b745ffb9b4251e59e8c02a"},
            {"uuid":"56a0f1e78dc8bf5c84ebaba77c5055d1"},
            {"uuid":"322768e9c634b8d29dce7602001bf657"},
            {"uuid":"aad6a4d8be0c5bda01c95f964fdebe3b"},
            {"uuid":"84489d6f7f5cfb8ef73ba15cf71e4da2"},
            {"uuid":"8a52622f7d1006545b0f6f60a6eb121c"},
            {"uuid":"ad7f2405d57969ddac7861a02f5374bd"},
            {"uuid":"13863d99c1bb954a95c8df2ffa1afbb2"},
            {"uuid":"26cd56088d2bd79ce8a2846b982fdc2f"},
            {"uuid":"af1a0e82e750902bbf5d3a4c5c55fa88"},
            {"uuid":"3cdae89acc2a5cc6f49fe14d9aad2da2"},
            {"uuid":"548f9ca5c97e766ace8c869d6374cb0c"},
            {"uuid":"cf71d46f8422ee9d1503ba28a8f6fa4f"},
            {"uuid":"b32615c2dc031f1ba10f5cce52a3dd75"},
            {"uuid":"147f16bed79f4b10698973fe114c9b7c"},
            {"uuid":"3c732089dd39989a46d3a0d6ce57413f"},
            {"uuid":"bdd2423f70f1a8d8dc727313441bc519"},
            {"uuid":"0669aae0a8e0816710d1ec63b450a389"},
            {"uuid":"1433a70fd6264e09d865ee6ead524971"},
            {"uuid":"7949e5be40eefe7382bd6b98729c72fb"},
            {"uuid":"155926265e9ad060892942973fdc75ee"},
            {"uuid":"2f55f12ccb9699d823aa8950099fd4be"},
            {"uuid":"d208772e7728973d3daf3bec3e3559b9"},
            {"uuid":"c7fbeac2928e1ca3ee4b283de746554a"},
            {"uuid":"36c0df55e36b836663831262cf59c22c"},
            {"uuid":"d5e70e3036d55d9002531ec82faffe7c"},
            {"uuid":"51507c264fc143ecab25420f0dde5cdd"},
            {"uuid":"36c40f8cf820544c538055ea8d8cddae"},
            {"uuid":"367459d35523e9f9fd6ed4caae35ed93"},
            {"uuid":"d2e840c9a35757b58eefb8c4f2aa48a6"},
            {"uuid":"1bf9ec8db273c4022a0fb0c2d53542c6"},
            {"uuid":"243dcd51fbe71f29f88dcf4f0e363a2b"},
            {"uuid":"8bd21b396078bd929d339ce0f483eaa3"},
            {"uuid":"5ec892249dbe57a0408a36d14908bd5b"},
            {"uuid":"9a8e87b3770147aa1764c6f8a3be4274"},
            {"uuid":"90c022fb0f7e0f2992135f3a66fc718c"},
            {"uuid":"894bf1fe5f68ad9c7a20966756d9faf5"},
            {"uuid":"96d5d1909656651f852f85814a16daa4"},
            {"uuid":"69aac4d620ca668f010e7bb044562135"},
            {"uuid":"b1687446625599e478f2bb3340c8866d"},
            {"uuid":"4e6689524b0c1768a4af8cd4abfd4f4f"},
            {"uuid":"6ba11a8dec0c007248d1141ac92b3cb6"},
            {"uuid":"fe705134fc2f7183a544a20d46ecbf41"},
            {"uuid":"afd30c35722d2dea899509285ca35145"},
            {"uuid":"9ffffb25815449b8e6fcb1bdb1a5e393"},
            {"uuid":"f27a79a27ac140f1c0461f890e984c2f"},
            {"uuid":"fcd6a282d683286abc14004aae3f88a7"},
            {"uuid":"395b461438a6812b34cda46db7c0fe09"},
            {"uuid":"3e64849c594ca9b024d91187b1cac00e"},
            {"uuid":"25ee80726a7d8e97a50ec1e4f8757d66"},
            {"uuid":"b15f6de35d0450842f6c75f2f34b7186"},
            {"uuid":"4208e22837fc173604e5463b3d109f9e"},
            {"uuid":"f458df88e86eacd39b5e32ed06866467"},
            {"uuid":"4310b9502a6aebe5950f33684c75206d"},
            {"uuid":"45ed79622f2ae14c877472e3d4b50054"},
            {"uuid":"bbdde3551a319f75497396c2e369a5f8"},
            {"uuid":"805ce631792745418ebdfacb73b9481d"},
            {"uuid":"d53ac48979c1b5ff137f800a9ba48fb6"},
            {"uuid":"8e5c8961e164a66847c57d44b569b4b6"},
            {"uuid":"9ea5d6abea5134defca3d5210a5f6f6f"},
            {"uuid":"90b6f9f1573aab6672c306672537b88f"},
            {"uuid":"3edc0fa831b63b4b21ba27e62ae6a8e1"},
            {"uuid":"2024b2f7562ddf6a15f35e68202f369a"},
            {"uuid":"cdc63293b493e9ffdf487cbadf41f3a3"},
            {"uuid":"4b49225185fb0ac17340610f937c67af"},
            {"uuid":"2361d0175e31daabc16c6586a36523e9"},
            {"uuid":"697a8d45cee4682c69c5ba5bca83a517"},
            {"uuid":"5ec02c8771eb52f823f9e15f2b00bd87"},
            {"uuid":"d7bfcb2d4736ac034e40471ee067f958"},
            {"uuid":"20a1a8a2e21503ad8f56f85fe4bbe0ff"},
            {"uuid":"3cae68899bc8dcb80ea62507f9b50de1"},
            {"uuid":"b3f7342f93f3451d7f9f92739ee05a5f"},
            {"uuid":"5b19d538aa85bc655f8b64aa93c19935"},
            {"uuid":"12a6ba064e6becea8e445ade81857366"},
            {"uuid":"386d34ceb1898858cbda4823362206c3"},
            {"uuid":"981aa5ff84de2ef13d2297046aee5a63"},
            {"uuid":"97973cc807c5591c94cd534cbb9b11f9"},
            {"uuid":"3c76123bbc58210c4ddfdf8212d88f8a"}
          ]  
        """;
        val JS = JSONParser();
        val JSARR : JSONArray = JS.parse(input) as JSONArray;
        var counter = 0
        JSARR.forEach { a ->
            val A: JSONObject = a as JSONObject;
            val UUID: String = A.get("uuid").toString()
            println("${counter++} AKTIVERA PSA @$UUID")
            HTTPClient.sendGet("http://192.168.0.2:4567/aktivera?uuid=${UUID}")
        }

    }

    fun activateviauuid(){
        val startTime = System.nanoTime()
        var counter = 0
        for (i in 149000..152000){
            println("${counter++} STEP, activate id $i")
            HTTPClient.sendGet("http://192.168.0.2:15000/psa/psa/gettest?id=$i")
        }
        val endTime = System.nanoTime()
        val duration = endTime - startTime
        println("time execution:: " + duration / 1000000000)
    }

    fun etsummary() {
            val res=HTTPClient.sendGet("http://192.168.0.126:15000/psa/psa/r?id=141763")
            print(res)
    }

    fun testGetEFFECT() {
        var psa  = PSADSLProcessor()
        val psastr = "'psa'=>::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psa.render(psastr)
        assertEquals(psa.TRUE_ATOM, psa.NUMBER_AT_2_W)
        val psastr2 = "'psa'=>::number_at_2_w{false},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psa.render(psastr2)
        assertEquals(psa.FALSE_ATOM, psa.NUMBER_AT_2_W)

    }

    fun testGetDeletePSA() {
        val uuid ="da08fe57-162b-11ec-af50-ed3d91637dad"
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::deletePSA{$uuid}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
    }

    fun testupdateplatenumber(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        psa.setupPlatenumber("fff0ce28-f030-11eb-98d6-052cbb92572e","kamaz", "belaz" )
        val ARR = ArrayList<Any>()
        ARR.add("fff0ce28-f030-11eb-98d6-052cbb92572e")
        val res = psa.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM psa where `uuid`=?", ARR)
        var Plate = ""
        if (res.next())
            Plate = res.getString("plate_number")
        assertEquals("kamaz belaz", Plate)
    }

    fun testlog(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::log{'true':'1.filename'}, ::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        assertEquals(psa.TRUE_ATOM, psa.ENABLED_LOG)
        assertEquals("1.filename", psa.FILENAME_LOG)
        psa.setupPlatenumber("fff0ce28-f030-11eb-98d6-052cbb92572e","kamaz", "belaz" )
        val ARR = ArrayList<Any>()
        ARR.add("fff0ce28-f030-11eb-98d6-052cbb92572e")
        val res = psa.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM psa where `uuid`=?", ARR)
        var Plate = ""
        if (res.next())
            Plate = res.getString("plate_number")
        assertEquals("kamaz belaz", Plate)
        assertEquals(true, File("1.filename").exists())
        val Str = String(Files.readAllBytes(File("1.filename").toPath()))
        println(Str)
    }


    fun testpsacomleted(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::log{'true':'1.filename'}, ::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007', 'uuid':'146000000'},::enabled{'true'}."
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)

        assertEquals(true, psa.checkpsacompleted("zxzxzxzxzxzxzxzxzxxzxzxzxzxzxxzxzxzxxzxzxzxzx"))
    }


}