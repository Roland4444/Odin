package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.psaDraft
import junit.framework.TestCase

class PSADSLProcessorTest : TestCase() {

    fun testRender() {
      //  val initialdsl = "'psa2'=>::psa{'urldb':'jdbc:mysql://192.168.0.121:3306/psa','login':user123,'pass':password },::psagetNumberfrom('url':http://192.168.0.121:8080/psa/psa/num,'keyparam':department_id),::stupid{http://192.168.0.121:8080/psa/psa/num}"
        val copy= "'psa2'=>::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id}"
        var psa  = PSADSLProcessor()
        psa.render(copy)
        assertEquals("jdbc:mysql://192.168.0.121:3306/psa", psa.urldb)
      //  assertEquals("http://192.168.0.121:8080/psa/psa/num", psa.dumb)
        assertEquals("password", psa.pass)
        assertEquals("user123", psa.login)
        assertEquals("http://192.168.0.121:8080/psa/psa/num?department_id", psa.urlPsanumberUrl)
        assertEquals("department_id", psa.keyparam_)
        //val f: psaDraft = psa.createdraft
       // f(12f, "12", "fgfgf")
    }
}