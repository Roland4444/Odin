package DSLGuided.requestsx.SMS

import DSLGuided.requestsx.StringHandler
import junit.framework.TestCase
import util.Saver

class SMSDSLProcessorTest : TestCase() {
    val msg = "DSL rulez white"
    fun testGetSendto() {
        val dsl: String = "'sms'=>::login{'avs'}, ::pass{'7BBAP7nkTCA4L3r'},::sendto{'89608607763','89996013370',  '89171998113'},::enabled{'true'}.  в sendto должно быть минимум два отправителя"
        val smsDSL: SMSDSLProcessor = SMSDSLProcessor()
        val f: StringHandler = smsDSL.render(dsl) as StringHandler
        smsDSL.add(2)
        print(smsDSL.str("aaa"))
        val nsg:String = f(msg)
        val b = nsg.encodeToByteArray()
        Saver.write(b, "report.log")
    }
}