package DSLGuided.requestsx.SMS

import DSLGuided.requestsx.StringHandler

class SendSMS {
    companion object {
        fun sendSMS(msg: String, DSL: String, SMSProc: SMSDSLProcessor): String{
            val f: StringHandler = SMSProc.render(DSL) as StringHandler
            return  f(msg)
        }
    }
}