package DSLGuided.requestsx.SMS

import com.avs.ParseDSL
import junit.framework.TestCase

class SMSDSLProcessorTest : TestCase() {

    fun testGetSendto() {
        var login ="avs"
        var pass = "7BBAP7nkTCA4L3r"
        val msg = "FFX"
        val dsl: String = "'sms'=>::login{'$login'}, ::pass{'$pass'},::sendto{'89608607763','89996013370',::enabled{'true'}}"
        val parser: ParseDSL
        val smsDSL: SMSDSLProcessor = SMSDSLProcessor()
        val f = smsDSL.render(dsl)
        f("high")


    }
}