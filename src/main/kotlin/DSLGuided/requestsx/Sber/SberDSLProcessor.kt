package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import se.roland.transport.SAAJ
import se.roland.xml.Extractor
import se.roland.xml.Transform
import java.io.File
import java.io.FileOutputStream
import java.lang.Error
import java.nio.file.Files
import java.sql.ResultSet
import kotlin.collections.ArrayList

////////////Пример DSL для SberDSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'sber'=>::endpoint{https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl},::login{test_AVS-api},::pass{test_AVS},::enabled{'true'},::HOOK{true,'ordernumber':7878787}.
typealias simpleString = () -> String
typealias Str1Int = (A: Int) -> String
typealias simpleInt = () -> Int
class SberDSLProcessor: DSLProcessor() {
    /////для первичного платежа
    /////'sber'=>::registerp2p{'amount':400, 'currency':643, 'orderNumber':555}.
    val Extraktor = Extractor()
    lateinit var TRANSPORT: SAAJ
    lateinit var PSADSLProcessor: PSADSLProcessor
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
        </wsse:UsernameToken></wsse:Security>
</soapenv:Header>   
    """.trimIndent()}

    val JUST_HEADER = {"""
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
        xmlns:p2p="http://engine.paymentgate.ru/webservices/p2p"
        xmlns:env="env">
    """.trimIndent()

    }
    val NEW_ATOM        = "NEW"
    val REJECTED_ATOM   = "REJECTED"
    var endpoint_           : simpleString   = {DEFAULT_URL}
    var login_              : simpleString   = {EMPTY_ATOM}
    var pass_               : simpleString   = {EMPTY_ATOM}
    var default_description_: Str1Int        = {"хознужды"}

    var binding_id_         : simpleString   = {EMPTY_ATOM}
    var HOOKED              : simpleString   = {FALSE_ATOM}
    var headersecurity      : simpleString   = {EMPTY_ATOM}
    var HOOK_ORDERNUMBER    : simpleString   = {EMPTY_ATOM}
    var REJECT_NEW_         : simpleString   = {FALSE_ATOM}

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

    val REJECT_NEW: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="REJECT_NEW")
                REJECT_NEW_= {a.key.Param as String}
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

    val registerp2pviapsaid: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "registerp2p") {
                val psaid = a.key.Param.toString().toInt()
                render(constructDSL4registerP2p(psaid))
            }
        }
    }
    fun clearhooked(){
        HOOK_ORDERNUMBER =  {EMPTY_ATOM}
        HOOKED           =  {EMPTY_ATOM}
    }
    val HOOK: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "HOOK"){
                clearhooked()
                var Arr = a.key.Param as MutableList<Any>
                Arr.forEach { a ->
                    when (a) {
                        is KeyValue -> {
                            if (a.Key.equals("ordernumber")) {
                                HOOK_ORDERNUMBER = {a.Value as String}
                                println("HOOK ordernumber to=>${HOOK_ORDERNUMBER()}")
                            }

                        };
                        is String -> HOOKED = {a};
                    }
                    println("HOOKED:: ${HOOKED()}")
                }
            }
        }
    }
    val registerp2p: RoleHandler = {
        mapper.forEach { a->
            if (a.key.Name=="registerp2p") {
                var amount: simpleInt = {0}
                var currency: simpleInt = {643}
                var orderNumber: simpleInt = {0}
                var clientId: simpleInt = {0}
                val Lst = a.key.Param as MutableList<KeyValue>
                Lst.forEach { A ->
                    run {
                        when (A.Key) {
                            "amount"        -> amount        = { A.Value.toString().toInt() }
                            "currency"      -> currency      = { A.Value.toString().toInt() }
                            "orderNumber"   -> orderNumber   = { A.Value.toString().toInt() }
                            "clientId"      -> clientId      = { A.Value.toString().toInt() }
                        }
                    }
                }
                if (HOOKED.equals(TRUE_ATOM))
                    if (HOOK_ORDERNUMBER().length>0)
                        orderNumber = {HOOK_ORDERNUMBER().toInt()}
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
                        <orderDescription>${default_description_(clientId())}</orderDescription>
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
                println("RESPONCE::${saveOrderId(orderNumber(), send(TEMPLATE_P2P_REGISTER()))}")
            }
        }
    }

    val perfomp2pBybinding: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "perfomp2pBybinding") {

            }
        }
    }
    fun constructDSL4registerP2p(PsaID: Int): String{
        var param = ArrayList<Any?>()
        param.add(PsaID)
        var amount = 0
        var orderNumber = 0
        var clientId = 0

        val res: ResultSet = PSADSLProcessor.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`payments` WHERE `psa_id` = ?;", param)
        if (res.next()) {
            amount = (res.getFloat("amount")*100).toInt()
            orderNumber = res.getInt("id")
        };
        val res2: ResultSet = PSADSLProcessor.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`psa` WHERE `id` = ?;", param)
        if (res2.next()) {
            clientId = res2.getInt("passport_id")
        };
        if (HOOKED().equals(TRUE_ATOM))
            if (HOOK_ORDERNUMBER().length>0){
                println("\n\n\n\nin cionstruct DSL; HOOK SEKTION to ${HOOK_ORDERNUMBER()}")
                orderNumber = HOOK_ORDERNUMBER().toInt()
            }
        return "'sber'=>::registerp2p{'amount':$amount,'currency':643,'orderNumber':$orderNumber,'clientId':$clientId}.";
    }

    fun send(ToSend: String): String{
        val fos = FileOutputStream(TEMP_FILE_TO_TRANSFORM)
        val transform = Transform()
        fos.write(transform.transform(ToSend.toByteArray()))
        fos.close()
        TRANSPORT.send(TEMP_FILE_TO_TRANSFORM, TEMP_FILE_TO_RESULT)
        return String(Files.readAllBytes(File(TEMP_FILE_TO_RESULT).toPath()))
    }

    fun String.error_code(): String{
      return error_code_(this)
    }

    fun error_code_(Input: String): String{
        return Extraktor.extractAttribute(Input.toByteArray(), "errorCode")
    }

    fun String.order_id(): String{
        return order_id_(this)
    }

    fun order_id_(Input: String): String{
        return Extraktor.extractTagValue(Input, "orderId")
    }

    fun getPaymentStatus(PaymentId: Int): String {
        var param = ArrayList<Any?>()
        param.add(PaymentId)
        val res: ResultSet = PSADSLProcessor.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`payments` WHERE `id` = ?;", param)
        var status = ""
        if (res.next())
            status = res.getString("status")
        return status
    }

    fun setPaymentStatus(PaymentId: Int, Status: String){
        var prepared = PSADSLProcessor.psearch.psaconnector.executor!!.conn.prepareStatement(
            "UPDATE `psa`.`payments` SET  `status`=? WHERE `id`=? "
        );
        prepared.setString(1, Status)
        prepared.setInt(2, PaymentId)
        println("PREPARED:: $prepared")
        prepared.execute()

    }

    val MAP_STATUS = mapOf(0 to NEW_ATOM, 1 to REJECTED_ATOM)

    fun saveOrderId(orderNumber: Int, Responce: String): String{
        var param = ArrayList<Any?>()
        param.add(orderNumber)

        if (REJECT_NEW_().equals(TRUE_ATOM))
            if (getPaymentStatus(orderNumber).uppercase().equals(NEW_ATOM))
                return ""
        var prepared = PSADSLProcessor.psearch.psaconnector.executor!!.conn.prepareStatement(
            "UPDATE `psa`.`payments` SET `document_id`='${Responce.order_id()}', `status`='${MAP_STATUS.get(Responce.error_code().toInt())}' WHERE `id`='$orderNumber' "
        );
        println("prepared=> $prepared")
        if (prepared != null)
            prepared.execute()
        return Responce
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
            "endpoint"      -> mapper.put(R, endpoint)
            "login"         -> mapper.put(R, login)
            "pass"          -> mapper.put(R, pass)
            "registerp2p"   -> mapper.put(R, registerp2p)
            "enabled"       -> mapper.put(R, enable)
            "binding_id"    -> mapper.put(R, binding_id)
            "HOOK"          -> mapper.put(R, HOOK)
            "REJECT_NEW"    -> mapper.put(R, REJECT_NEW)
            "perfomp2pBybinding"    -> mapper.put(R, perfomp2pBybinding)


        }
    }

}