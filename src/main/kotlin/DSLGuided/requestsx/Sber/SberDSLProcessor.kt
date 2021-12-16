package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
////////////Пример DSL для SberDSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'sber'=>::endpoint{https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl},::login{test_AVS-api},::pass{test_AVS},::enabled{'true'}
typealias simpleString = () -> String
class SberDSLProcessor: DSLProcessor() {

    val DEFAULT_URL = "DEFAULT"
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key) }
        return "OK"
    }
    var endpoint_: simpleString = {DEFAULT_URL}
    var login_: simpleString = {EMPTY_ATOM}
    var pass_: simpleString = {EMPTY_ATOM}

    val endpoint: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="endpoint")
                endpoint_= {a.key.Param as String}
        }
    }

    val login: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="login")
                login_= {a.key.Param as String}
        }
    }

    val pass: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="pass")
                pass_= {a.key.Param as String}
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
        when (R?.Name){
            "endpoint" -> mapper.put(R, endpoint)
            "login" -> mapper.put(R, login)
            "pass" -> mapper.put(R, pass)

            "enabled" -> mapper.put(R, enable)
        }
    }

}