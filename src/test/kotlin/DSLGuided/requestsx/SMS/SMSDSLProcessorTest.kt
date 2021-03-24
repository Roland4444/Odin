package DSLGuided.requestsx.SMS

import DSLGuided.requestsx.StringHandler
import junit.framework.TestCase
import util.Saver
typealias simple2 = (in1: Int, in2: Int) -> Unit
class SMSDSLProcessorTest : TestCase() {
    val msg = "DSL rulez white"
    fun testGetSendto() {
        val dsl: String = "'sms'=>::login{'avs'}, ::pass{'7BBAP7nkTCA4L3r'},::sendto{'89608607763','89996013370',  '89171998113'},::enabled{'false'}.  в sendto должно быть минимум два отправителя"
        val smsDSL: SMSDSLProcessor = SMSDSLProcessor()
        val f: StringHandler = smsDSL.render(dsl) as StringHandler
        smsDSL.add(2)
        print(smsDSL.str("aaa"))
        val nsg:String = f(msg)
        val b = nsg.encodeToByteArray()
        Saver.write(b, "report.log")
    }

    fun sum(a: Int, b: Int): Int{
        return a+b
    }
    fun testsimple2(){
        val f : simple2
        f = {
            in1, in2 -> run { println(in1); sum(in1, in2)}
        }
        assertEquals(4, f(2,2))
    }
}

