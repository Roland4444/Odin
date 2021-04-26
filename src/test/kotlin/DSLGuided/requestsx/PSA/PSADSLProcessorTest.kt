package DSLGuided.requestsx.PSA
import junit.framework.TestCase
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
        psa.executor=psaconnector.executor
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

}