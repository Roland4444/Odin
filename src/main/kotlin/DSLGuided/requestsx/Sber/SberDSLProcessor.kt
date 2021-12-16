package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import se.roland.transport.SAAJ
import se.roland.xml.Transform
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

////////////Пример DSL для SberDSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'sber'=>::endpoint{https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl},::login{test_AVS-api},::pass{test_AVS},::enabled{'true'}
typealias simpleString = () -> String
typealias simpleInt = () -> Int
class SberDSLProcessor: DSLProcessor() {
    /////для первичного платежа
    /////'sber'=>::registerp2p{'amount':400, 'currency':643, 'orderNumber':555}.
    lateinit var TRANSPORT: SAAJ
    val DEFAULT_URL             = "DEFAULT"
    val TEMP_FILE_TO_TRANSFORM  = "temp_trans.xml"
    val TEMP_FILE_TO_RESULT     = "temp_result.xml"
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key) }
        headersecurity = {
            TEMPLATE_HS()
        }
        TRANSPORT= SAAJ(endpoint_())
        return "OK"
    }

    var TEMPLATE_HS: simpleString = {"""
        <soapenv:Header>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" env:mustUnderstand="1" soapenv:mustUnderstand="1">
        <wsse:UsernameToken wsu:Id="UsernameToken-UUID">
        <wsse:Username>${login_()}</wsse:Username>
        <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">${pass_()}</wsse:Password>
        </wsse:UsernameToken>        
    """.trimIndent()}

    val JUST_HEADER = {"""
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:p2p="http://engine.paymentgate.ru/webservices/p2p"
        xmlns:env="env">
    """.trimIndent()

    }




    var endpoint_: simpleString = {DEFAULT_URL}
    var login_: simpleString = {EMPTY_ATOM}
    var pass_: simpleString = {EMPTY_ATOM}
    var default_description_: simpleString = {"Лом черных или цветных металлов"}

    var binding_id_: simpleString = {EMPTY_ATOM}

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

    val binding_id: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="binding_id")
                binding_id_= {a.key.Param as String}
        }
    }

    val default_description: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="default_description")
                default_description_= {a.key.Param as String}
        }
    }

    val registerp2p: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="registerp2p") {
                var amount: simpleInt = {0}
                var currency: simpleInt = {643}
                var orderNumber: simpleInt = {0}
                val Lst = a.key.Param as MutableList<KeyValue>
                var TEMPLATE_P2P_REGISTER: simpleString = {
                    """
                    ${JUST_HEADER()}
                    ${headersecurity()}
        	    <soapenv:Body>
                <p2p:registerP2P>
                    <arg0 language="RU">
                        <amount>${amount()}</amount>
                        <currency>${currency()}</currency>
                        <orderNumber>${orderNumber()}</orderNumber>
                        <orderDescription>${default_description_()}</orderDescription>
                        <returnUrl>avs.com.ru</returnUrl>
                        <failUrl>https://avs.com.ru</failUrl>
                        <sessionTimeoutSecs>300</sessionTimeoutSecs>
                        <email>metal@avs.com.ru</email>
                        <params name="mark" value="1"/>
                        <transactionTypeIndicator>A</transactionTypeIndicator>
                        <features>
                            <feature>WITHOUT_FROM_CARD</feature>
                        </features>
                        <creditBindingId>${binding_id_()}</creditBindingId>
                        <sbpSenderParams account="?"/>
                    </arg0>
                </p2p:registerP2P>
            </soapenv:Body>
        </soapenv:Envelope>
    """.trimIndent()
                }
                Lst.forEach { A ->
                    run {
                        when (A.Key) {
                            "amount" -> amount = { A.Value.toString().toInt() }
                            "currency" -> currency = { A.Value.toString().toInt() }
                            "orderNumber" -> orderNumber = { A.Value.toString().toInt() }
                        }
                    }
                }
                send(TEMPLATE_P2P_REGISTER())
            }
        }
    }

    fun send(ToSend: String): String{
        val fos = FileOutputStream(TEMP_FILE_TO_TRANSFORM)
        val transform = Transform()
        fos.write(transform.transform(ToSend.toByteArray()))
        fos.close()
        TRANSPORT.send(TEMP_FILE_TO_TRANSFORM, TEMP_FILE_TO_RESULT)
        return String(Files.readAllBytes(File(TEMP_FILE_TO_RESULT).toPath()))
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