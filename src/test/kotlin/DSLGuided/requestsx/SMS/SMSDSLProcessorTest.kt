package DSLGuided.requestsx.SMS

import DSLGuided.requestsx.StringHandler
import junit.framework.TestCase

class SMSDSLProcessorTest : TestCase() {
    val msg = "FFX"
    fun testGetSendto() {
        val dsl: String = "'sms'=>::login{'avs'}, ::pass{'7BBAP7nkTCA4L3r'},::sendto{'89608607763',''},::enabled{'true'}"
        val smsDSL: SMSDSLProcessor = SMSDSLProcessor()
        val f: StringHandler = smsDSL.render(dsl) as StringHandler
        smsDSL.add(2)
        smsDSL.str("aaa")
        f(msg)
    }
}