package util

import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import se.roland.util.HTTPClient

class Aktivator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
                val startTime = System.nanoTime()
                var counter = 0
                val i= 159183
                HTTPClient.sendGet("http://192.168.0.126:15000/psa/psa/gettest?id=$i")

                val endTime = System.nanoTime()
                val duration = endTime - startTime
                println("time execution:: " + duration / 1000000000)

        }
    }
//    fun compelted(){
//        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
//        var psa  = PSADSLProcessor()
//
//        psaconnector.render(psaconnstr)
//        val PSASearchProcessor = PSASearchProcessor()
//        PSASearchProcessor.psaconnector= psaconnector
//        psa.psearch=PSASearchProcessor
//        val psastr = "'psa'=>::psaIDtoSEhooK{'true','3':'1'},::HOOK{'true','section':'20007'},::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.126:8888/psa/psa/num},::keyparam{department_id},::enabled{'true'}."
//        psa.render(psastr)
//        assertEquals(false, psa.checkpsacompleted("ghdfgjhdjgdhjgdhgj"))
//    }


}