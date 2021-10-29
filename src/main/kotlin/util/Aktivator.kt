package util

import se.roland.util.HTTPClient

class Aktivator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
                val startTime = System.nanoTime()
                var counter = 0
                val i= 156949
                HTTPClient.sendGet("http://192.168.0.2:15000/psa/psa/gettest?id=$i")

                val endTime = System.nanoTime()
                val duration = endTime - startTime
                println("time execution:: " + duration / 1000000000)

        }
    }
}