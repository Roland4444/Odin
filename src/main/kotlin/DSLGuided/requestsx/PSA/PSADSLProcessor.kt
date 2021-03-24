package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass, db PSA                                                                                                    URL service (get request)                       name keyparam
///////////'psa'=>::psa{'urldb':'jdbc:mysql://192.168.0.121:3306/psa','login':'user123','pass':'password'},::psagetNumberfrom('url':'http://192.168.0.121:8080/psa/psa/num','keyparam':'department_id')

///////
///////'psa2'=>::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::psagetNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id}
class PSADSLProcessor  : DSLProcessor() {
    var login: String=""
    var pass: String=""
    var urldb: String =""
    var urlPsanumberUrl: String =""
    var keyparam_: String =""
    var dumb: String = ""
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }
    fun processPSASection(input:MutableList<Any>){
        println("into PSA section::")
        input.forEach{
            val f: KeyValue = it as KeyValue
            println("""KEY VALUE ${f.Key}::${f.Value}""")
            when ((it as KeyValue).Key){
                "login" -> login = it.Value as String;
                "pass"  -> pass  = it.Value as String;
            }
        }
    }
    val stupid: RoleHandler = {
        println("\n\n\nINTO DUMB\n\n\n")
        mapper.forEach { a ->
            if (a.key.Name == "stupid")
                dumb = a.key.Param as String
        }
    }

    val db: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "db")
                urldb = a.key.Param as String
        }
    }
    val keyparam: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "keyparam")
                keyparam_ = a.key.Param as String
        }
    }

    val psa: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psa")
                processPSASection(a.key.Param as MutableList<Any>)
        }
    }
    val getPsaNumberfrom: RoleHandler = {
        println("\n\n\n\n<<<<<<<>>>>>>>INTO getPsaNumberfrom")
        mapper.forEach { a ->
            if (a.key.Name == "getPsaNumberfrom")
                urlPsanumberUrl = a.key.Param as String
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
            "getPsaNumberfrom" -> mapper.put(R, getPsaNumberfrom)
            "db" -> mapper.put(R, db)
            "keyparam" -> mapper.put(R, keyparam)
        }
    }
}