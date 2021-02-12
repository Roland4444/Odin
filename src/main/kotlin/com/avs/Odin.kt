package com.avs

class Odin {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            println("Odin run!!")
        }
    }

    fun marinafunc(foo: String, bar: String): String="Σ="+(Integer.valueOf(foo)+Integer.valueOf(bar)).toString()
    fun guest(a: String, b: String): String=""

    fun result(foo: String, bar: String, Function:(String, String) -> String): String{
        val result = Function(foo, bar)
        return """<h1>$foo</h1><br>h1>$bar</h1><br>$result"""
    }

    fun callmarina():String{
        val foo = "3"
        val bar = "7"
        return (result(foo,bar, ::marinafunc))
    }

    fun parseDSL(Input: String): String = Input.substring(0,6)
}