package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
////////////Пример DSL для SberDSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'sber'=>::endpoint{https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl},::login{test_AVS-api},::pass{test_AVS},::enabled{'true'}
typealias simpleString = () -> String
typealias simpleInt = () -> Int
class SberDSLProcessor: DSLProcessor() {
    /////для первичного платежа
    /////'sber'=>::registerp2p{'amount':400, 'currency':643, 'orderNumber':555}.

    val DEFAULT_URL = "DEFAULT"
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key) }
        headersecurity = {
            "NONE"
        }
        return "OK"
    }

    val TEMPLATE_HS = """
        <soapenv:Header>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" env:mustUnderstand="1" soapenv:mustUnderstand="1">
        <wsse:UsernameToken wsu:Id="UsernameToken-UUID">
        <wsse:Username></wsse:Username>
        <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText"></wsse:Password>
        </wsse:UsernameToken>
        
    """.trimIndent()


    var endpoint_: simpleString = {DEFAULT_URL}
    var login_: simpleString = {EMPTY_ATOM}
    var pass_: simpleString = {EMPTY_ATOM}

    var headersecurity = {EMPTY_ATOM}

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

    val registerp2p: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="registerp2p") {
                var amount: simpleInt = {0}
                var currency: simpleInt = {643}
                var orderNumber: simpleInt = {0}
                val Lst = a.key.Param as MutableList<KeyValue>
                Lst.forEach { A ->
                    run {
                        when (A.Key) {
                            "amount" -> amount = { A.Value.toString().toInt() }
                            "currency" -> currency = { A.Value.toString().toInt() }
                            "orderNumber" -> orderNumber = { A.Value.toString().toInt() }
                        }
                    }
                }
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
        when (R?.Name){
            "endpoint" -> mapper.put(R, endpoint)
            "login" -> mapper.put(R, login)
            "pass" -> mapper.put(R, pass)
            "registerp2p" -> mapper.put(R, registerp2p)

            "enabled" -> mapper.put(R, enable)
        }
    }

}