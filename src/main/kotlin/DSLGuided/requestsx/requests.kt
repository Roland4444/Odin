package DSLGuided.requestsx

import abstractions.Role
import com.avs.ParseDSL

class requests : DSLProcessor() {
    var parser = ParseDSL()
    fun mock(): String {
        return "hi"
    }

    fun parseroleDSL(DSL: String?): String {
        return parser.parseRole(DSL!!).toString()
    }

    fun parseroles(DSL: String?): String {
        return parser.parseRoles(DSL!!).toString()
    }

    override fun render(DSL: String?): String? {
        return "hi"
    }

    override fun parseRoles(DSL: String?): List<Role?>? {
        return parser.parseRoles(DSL!!)
    }

    fun applyrules(DSL: String, Function1:(String, String) -> String, Function2:()->Unit, Function3:()->Unit, Function4:()->Unit):String{
        return ""
    }



    fun read() {
    }

    fun write() {
    }


}
