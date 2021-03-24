package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass, db PSA                                                                                                    URL service (get request)                       name keyparam
///////////'psa'=>::psa{'urldb':'jdbc:mysql://192.168.0.121:3306/psa','login':'user123','pass':'password'},::psagetNumberfrom('url':'http://192.168.0.121:8080/psa/psa/num','keyparam':'department_id')
class PSADSLProcessor  : DSLProcessor() {
    var login: String=""
    var pass: String=""
    var dbUrl: String =""
    var urlPsanumberUrl: String =""
    var paramName: String =""
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }
    val psa: RoleHandler = {
        mapper.forEach {
                a->
            if (a.key.Name=="psa") {
                println("\n\n\ninto psa lambda")
                var psaTupple = mutableListOf<Any>()
                psaTupple = a.key.Param as MutableList<Any>
                var keyvalue = mutableMapOf<String, String>()
                psaTupple.forEach { b->{
                   // keyvalue.put(b.)
                    keyvalue = b as MutableMap<String, String>
                 //   if keyvalue.
                } }
            }
        }
    }
    val getPsaNumberfrom: RoleHandler = {
        mapper.forEach {
                a->
            if (a.key.Name=="enabled") {
                println("\n\n\ninto enable lambda")
              //  enabled = a.key.Param as String
            }
        }
    }
    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }

    fun appendRole(R: Role){
        print("Adding role ${R.Name}\n")
        when (R?.Name){
            "psa" -> mapper.put(R, psa)
            "getPSAnumberFrom" -> mapper.put(R, getPsaNumberfrom)

        }
    }
}