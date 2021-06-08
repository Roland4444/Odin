package DSLGuided.requestsx.PaymentDSL

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role

class PaymentProcessor:  DSLProcessor() {
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }
    lateinit var ApiKey: String
    lateinit var BasicLicenceId: String
    lateinit var rooturl_: String
    lateinit var key_: String

    val lisstattus = "/liststatus"
    val checkpay = "/checkpay/"

    fun lisstatuses(User: String, Pass: String){

    }

    val rooturl: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "rooturl")
                rooturl_ = a.key.Param as String
        }
    }
    val key: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "key")
                key_ = a.key.Param as String
        }
    }
    val apikey: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "apikey")
                ApiKey = a.key.Param as String
        }
    }
    val basiclicenceid: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "basiclicenceid")
                BasicLicenceId = a.key.Param as String
        }
    }
    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }
    fun appendRole(R: Role){
        when (R?.Name){
            "apikey" -> mapper.put(R, apikey)
            "rooturl" -> mapper.put(R, rooturl)
            "key" -> mapper.put(R, key)
            "basiclicenceid" -> mapper.put(R, basiclicenceid)
            "enabled" -> mapper.put(R, enable)
        }
    }



}