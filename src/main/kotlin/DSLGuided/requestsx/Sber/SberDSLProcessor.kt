package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import se.roland.crypto.Gost3411Hash.getBytesFromBase64
import se.roland.transport.SAAJ
import se.roland.xml.Extractor
import se.roland.xml.Transform
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import kotlin.collections.ArrayList


////////////Пример DSL для SberDSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'sber'=>::endpoint{https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl},::login{test_AVS-api},::pass{test_AVS},::enabled{'true'},::HOOK{true,'ordernumber':7878787}.
typealias simpleString = () -> String
typealias Str1Int = (A: Int) -> String
typealias simpleInt = () -> Int
class SberDSLProcessor: DSLProcessor() {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            println("SEK RULEZ ANYTIME")
            val S = SberDSLProcessor()
            println(S.DIRECT_SETOKEN())
        }

        fun registerPayment(DSL: String, Sber: SberDSLProcessor, PsaID: String): String{
            Sber.r(DSL)
            val DSLSTR = Sber.constructDSL4registerP2p(PsaID.toInt())
            println("CONSTRUCTED DSL FOR $PsaID:: $DSLSTR")
            Sber.r(DSLSTR)
            return Sber.error_message_(Sber.LAST_RESPONCE())
        }
    }
    /////для первичного платежа
    /////'sber'=>::registerp2p{'amount':400, 'currency':643, 'orderNumber':555}.

    val DIRECT_ORDER_ID: simpleString = {
        "cbbd4e6d-2f35-7bfc-a90f-5aa72823c181"
    }

    val DIRECT_TIMESTAMP: simpleString = {
        "2021-12-24T17:11:06+03:00"
    }

    val DIRECT_SETOKEN: simpleString ={
"${DIRECT_TIMESTAMP()}/6cc2cc38-3677-7330-9b6b-54b62823c181/4111111111111111///${DIRECT_ORDER_ID()}"
    }
    val SIGNED_SETOKEN: simpleString = {
        "Se/PHZChFyQneNOH900bxbtxfiRi7hBpnt8/enyNYWisGWzi99xACdYtOb4i+RI3pquv28nIA1JpAKbfDecdNJ1Oln1vRQ+20jpPyRtTW+aMjr587SWvjLK1hQcfD8eiZscNY3M2EpBHWCv+HAGEDXvroTcz3sfSSrQOEdilzv9nFvhDV0IqPcerXnltEiUGgBvYvmkGAKGo/TPP9Zo+RzgPiWq5FY0rnWK8ZfslWMOeq7XgYX37D9tvkYvpZZw8VvMkazYF56WF2ZEJglEU1dPF/SxvB6oJoKqCPeHteq/OPTnxfgRgENQbbnx1CdKwi8NV34Mtz6IDClhnjAnAEQ=="
    }
    val DIRECT: simpleString = {
        """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:p2p="http://engine.paymentgate.ru/webservices/p2p" xmlns:env="env">
	<soapenv:Header>
		<wsse:Security env:mustUnderstand="1" soapenv:mustUnderstand="1" 
			xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
			xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
			<wsse:UsernameToken wsu:Id="UsernameToken-UUID">
				<wsse:Username>test_AVS-api</wsse:Username>
				<wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">test_AVS</wsse:Password>
		</wsse:UsernameToken></wsse:Security>
	</soapenv:Header>
		<soapenv:Body>
			<p2p:performP2PByBinding> 
				<arg0 language="ru"> 
					<orderId>${DIRECT_ORDER_ID()}</orderId> 
					<fromCard>
						<bindingId>6cc2cc38-3677-7330-9b6b-54b62823c181</bindingId>
					</fromCard> 
					<toCard>
						<seToken>${SIGNED_SETOKEN()}</seToken> 
					</toCard> 
				</arg0> 
			</p2p:performP2PByBinding>
		</soapenv:Body>
		</soapenv:Envelope>
        """.trimIndent()
    }

val STR = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:p2p=\"http://engine.paymentgate.ru/webservices/p2p\" xmlns:env=\"env\">\n" +
        "\t<soapenv:Header>\n" +
        "\t\t<wsse:Security env:mustUnderstand=\"1\" soapenv:mustUnderstand=\"1\" \n" +
        "\t\t\txmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"\n" +
        "\t\t\txmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
        "\t\t\t<wsse:UsernameToken wsu:Id=\"UsernameToken-UUID\">\n" +
        "\t\t\t\t<wsse:Username>test_AVS-api</wsse:Username>\n" +
        "\t\t\t\t<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">test_AVS</wsse:Password>\n" +
        "\t\t</wsse:UsernameToken></wsse:Security>\n" +
        "\t</soapenv:Header>\n" +
        "\t\t<soapenv:Body>\n" +
        "\t\t\t<p2p:performP2PByBinding> \n" +
        "\t\t\t\t<arg0 language=\"ru\"> \n" +
        "\t\t\t\t\t<orderId>8563aa00-6b32-7168-8264-91ac2823c181</orderId> \n" +
        "\t\t\t\t\t<fromCard>\n" +
        "\t\t\t\t\t\t<bindingId>6cc2cc38-3677-7330-9b6b-54b62823c181</bindingId>\n" +
        "\t\t\t\t\t</fromCard> \n" +
        "\t\t\t\t\t<toCard>\n" +
        "\t\t\t\t\t\t<seToken>AkHYc4kCTl84YNymdx+sx1L0k5ue0rOXCyX2SFl4M4hmdXreEJV8Veqb/DiLAchvgX+LzXfh9OknHoI+vgsaqnZRmvW9+oqJsKE/jXspJ37Z5p3mahqGvhNFxpQV6Z0gLfe9f/enicbHYV3zVaKLqrz75j/vBnLIebenRo1k7XpL4gnCHaHa0l7mVeaMQhOic4sRXhLpUPRZ3Tn3EX2rdpZhs1Pi43SryaNF8Jx3WBUu3X/0vxEv6eg9MgCRvfhc9fMiTY1k6iM3gV4Skt6gHBsunRONSbHP9JkV1XUnz/PElWEN75vTikn81OgNMXI5NvY86e4HHgWdV+yQKS145Q==</seToken> \n" +
        "\t\t\t\t\t</toCard> \n" +
        "\t\t\t\t</arg0> \n" +
        "\t\t\t</p2p:performP2PByBinding>\n" +
        "\t\t</soapenv:Body>\n" +
        "\t</soapenv:Envelope>"

    val Extraktor = Extractor()
    lateinit var TRANSPORT: SAAJ
    lateinit var PSADSLProcessor: PSADSLProcessor
    val DEFAULT_URL             = "DEFAULT"
    val TEMP_FILE_TO_TRANSFORM  = "temp_trans.xml"
    val TEMP_FILE_TO_RESULT     = "temp_result.xml"
    override fun r(DSL: String): Any {
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key) }
        headersecurity = {
            TEMPLATE_HS()
        }
        TRANSPORT= SAAJ(endpoint_())
        return "OK"
    }



    fun String.error_message(): String{
        return Extraktor.extractAttribute(this.toByteArray(), "errorMessage")
    }

    fun error_message_(input: String): String{
        return input.error_message()
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
    var LAST_RESPONCE                        = {EMPTY_ATOM}
    val NEW_ATOM                             = "New"
    val REJECTED_ATOM                        = "Rejected"
    val IN_PROGRESS_ATOM                     = "InProgress"
    val COMPLETED_ATOM                       = "Completed"
    val PAY_ERROR_ATOM                       = "PayError"
    var endpoint_           : simpleString   = {DEFAULT_URL}
    var login_              : simpleString   = {EMPTY_ATOM}
    var pass_               : simpleString   = {EMPTY_ATOM}
    var default_description_: Str1Int        = {"хознужды"}

    var binding_id_         : simpleString   = {EMPTY_ATOM}
    var HOOKED              : simpleString   = {FALSE_ATOM}
    var headersecurity      : simpleString   = {EMPTY_ATOM}
    var HOOK_ORDERNUMBER    : simpleString   = {EMPTY_ATOM}
    var REJECT_NEW_         : simpleString   = {FALSE_ATOM}
    var PUBLIC_KEY          : simpleString   = {EMPTY_ATOM}
    var PRIVATE_KEY         : simpleString   = {EMPTY_ATOM}
    var SETOKEN             : simpleString   = {EMPTY_ATOM}


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
                r(constructDSL4registerP2p(psaid))
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
                        <returnUrl>https://avs.com.ru</returnUrl>
                        <failUrl>https://avs.com.ru</failUrl>
                        <sessionTimeoutSecs>600</sessionTimeoutSecs>
                        <email>metal@avs.com.ru</email>
                        <params name="mark" value="1"/>
                        <transactionTypeIndicator>A</transactionTypeIndicator>
                        <features>
                            <feature></feature>
                        </features>
                        <bindingId>${binding_id_()}</bindingId>
                        <creditBindingId></creditBindingId>
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

    val KEY: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "KEY") {
                var Arr = a.key.Param as MutableList<KeyValue>
                Arr.forEach{a->
                    when (a.Key){
                        "public"-> PUBLIC_KEY = {String(Saver.Saver.readBytes(a.Value as String)).replace("BEGINRSAPRIVATEKEY", "BEGIN RSA PRIVATE KEY")
                        .replace("ENDRSAPRIVATEKEY", "END RSA PRIVATE KEY")
                        .replace("BEGINPUBLICKEY", "BEGIN PUBLIC KEY")
                        .replace("ENDPUBLICKEY", "END PUBLIC KEY")
                        .replace("-----BEGIN PUBLIC KEY-----\n", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        }

                        "private"-> PRIVATE_KEY = {String(Saver.Saver.readBytes(a.Value as String)).replace("BEGINRSAPRIVATEKEY", "BEGIN RSA PRIVATE KEY")
                            .replace("ENDRSAPRIVATEKEY", "END RSA PRIVATE KEY")
                            .replace("BEGINPUBLICKEY", "BEGIN PUBLIC KEY")
                            .replace("ENDPUBLICKEY", "END PUBLIC KEY")
                            .replace("-----BEGIN RSA PRIVATE KEY-----\n", "")
                            .replace("-----END RSA PRIVATE KEY-----", "")
                        }
                    }
                }
                println("PUBLIC KEy::${PUBLIC_KEY()}")
                println("PRIVATE_KEY::${PRIVATE_KEY()}")

            }
        }
    }

    val bindingId: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "bindingId")
                binding_id_ = {a.key.Param as String}
        }
    }
    fun getKey(key: String): PublicKey? {
        try {

            val X509publicKey = X509EncodedKeySpec(getBytesFromBase64(key))
            val kf = KeyFactory.getInstance("RSA")
            return kf.generatePublic(X509publicKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    // Encryption

    fun encrypt_____(data: String): ByteArray? {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(1, this.getPublicKey(PUBLIC_KEY()))
        return cipher.doFinal(data.toByteArray())
    }


    private fun getPublicKey(base64PublicKey: String): PublicKey? {
        var publicKey: PublicKey? = null
        try {
            val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.toByteArray()))
            val keyFactory = KeyFactory.getInstance("RSA")
            publicKey = keyFactory.generatePublic(keySpec)
            return publicKey
        } catch (var5: NoSuchAlgorithmException) {
            var5.printStackTrace()
        } catch (var6: InvalidKeySpecException) {
            var6.printStackTrace()
        }
        return publicKey
    }

    fun seToken(TimeStamp: String, UUID: String, PAN: String, MdOrder: String): String? {
        var Template_SeTOKEN: simpleString = {
            "$TimeStamp/$UUID/$PAN///$MdOrder".trimIndent()
        }
        SETOKEN = {Template_SeTOKEN()}
        println("Template_SeTOKEN::${Template_SeTOKEN()}")
        val ENC =  Base64.getEncoder().encodeToString(encrypt_____(Template_SeTOKEN()))
        return  ENC
        //encrypt2(Template_SeTOKEN(), PUBLIC_KEY())///getKey(PUBLIC_KEY()))
    }



    val timestamp: simpleString = {

        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().time)
        println("TIMESTAMP::${timeStamp.toString()}")
        timeStamp.toString().replace("_","T")+"+04:00"
//        val ret = "2021-12-27T08:50:06+03:00"
//        println("TIMESTAMP::$ret")
//        ret

    }

    val getstatus: RoleHandler = {
        mapper.forEach{a ->
            if (a.key.Name == "getstatus"){
                val Lst = a.key.Param as MutableList<KeyValue>
                var orderId: simpleString   = {""}
                var orderNumber    : simpleString   = {""}
                Lst.forEach { A ->
                    run {
                        when (A.Key) {
                            "orderId"        -> orderId      = { A.Value.toString() }
                            "orderNumber"    -> orderNumber  = { A.Value.toString() }
                        }
                    }
                }

                var TEMPLATE_4_STATUS: simpleString = {
                   """
                    ${JUST_HEADER()}
                    ${headersecurity()}
                          <soapenv:Body>
                             <p2p:getP2PStatus>
                                <arg0 language="?">
                                   <orderId>${orderId()}</orderId>
                                   <orderNumber>${orderNumber()}</orderNumber>
                                </arg0>
                             </p2p:getP2PStatus>
                          </soapenv:Body>
                       </soapenv:Envelope>
                   """.trimIndent()
                }
                println("TEMPLATE 2STATUS::"+TEMPLATE_4_STATUS())
                val R = send(TEMPLATE_4_STATUS())
                println(R)

            }

        }
    }

    val perfomP2P: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "perfomP2P") {
                println("\n\n\n\n\n\nperfomP2P!!!!!!!!!!1")
                val Lst = a.key.Param as MutableList<KeyValue>
                var orderId: simpleString   = {""}
                var PAN    : simpleString   = {""}
                Lst.forEach { A ->
                    run {
                        when (A.Key) {
                            "orderId"        -> orderId      = { A.Value.toString() }
                            "PAN"            -> PAN          = { A.Value.toString() }
                        }
                    }
                }
                println("BINDING ID::${binding_id_()}")
                var TEMPLATE_P2P_PERFORM: simpleString = {/////}


                    """
                    ${JUST_HEADER()}
                    ${headersecurity()}
                    <soapenv:Body>
			        <p2p:performP2PByBinding> 
				    <arg0 language="ru"> 
					<orderId>${orderId()}</orderId> 
					<fromCard>
						<bindingId>${binding_id_()}</bindingId>
					</fromCard> 
					<toCard>
						<seToken>${seToken(timestamp(), binding_id_(), PAN(), orderId() )}</seToken> 
					</toCard> 
				    </arg0> 
			        </p2p:performP2PByBinding>
		            </soapenv:Body>
		            </soapenv:Envelope>                  
                    """.trimIndent()
                }
                println("TEMPLATE P2P::"+TEMPLATE_P2P_PERFORM())
                val R = send(TEMPLATE_P2P_PERFORM())
                println(R)
                changeStatusInDB(R, orderId())
            }
        }
    }

    fun checkpsaymentExists(PsaId: String): Boolean{
        val Arr = ArrayList<Any>()
        Arr.add(PsaId)
        val res: ResultSet = PSADSLProcessor.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`payments` WHERE `psa_id` = ?;", Arr)
        return res.next()

    }

    fun changeStatusInDB(Responce: String, OrderId: String): String{
        var param = ArrayList<Any?>()
        param.add(OrderId)
        when (Responce.error_code()){
            "0"     -> {
                println("changing status to $IN_PROGRESS_ATOM")
                param.add(IN_PROGRESS_ATOM)
            }
            else    -> {
                println("changing status to $REJECTED_ATOM")
                param.add(REJECTED_ATOM)
            }
        }
        var prepared = PSADSLProcessor.psearch.psaconnector.executor!!.conn.prepareStatement(
            "UPDATE `psa`.`payments` SET `status`=?  WHERE `document_id`=? "
        );
        prepared.setString(1, param.get(1).toString())
        prepared.setString(2, param.get(0).toString())

        println("prepared SQL=>  $prepared")
        if (prepared != null)
            prepared.execute()
       return Responce
    }

    fun createPayment(psaId: Int){
        val psa = PSADSLProcessor.psearch.getWViaPSAId(psaId.toString());
        if (!psa.next()) {
            println("PSA with ID $psaId not exists!")
            return
        }
        val prepared = PSADSLProcessor.psearch.psaconnector.executor!!.conn.prepareStatement(
                """INSERT INTO `psa`.`payments` (
`id`,`psa_id`,`amount`,`status`,`send_confirm`, `paymentprovider`)
                                VALUES
(NULL,   $psaId,  ${PSADSLProcessor.getSummfromPSaID(psaId.toString())},   
                        '$NEW_ATOM',    0,             'sber');                                
                """  );
        print("prepared to create Payment::$prepared")
        prepared.execute()
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
        else  {
            createPayment(PsaID)
            return constructDSL4registerP2p(PsaID)
        }

        val res2: ResultSet = PSADSLProcessor.psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`psa` WHERE `id` = ?;", param)
        if (res2.next())
            clientId = res2.getInt("passport_id")
        if (HOOKED().equals(TRUE_ATOM))
            if (HOOK_ORDERNUMBER().length>0){
                println("\n\n\n\nin construct DSL; HOOK SEKTION to ${HOOK_ORDERNUMBER()}")
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
        LAST_RESPONCE = {String(Files.readAllBytes(File(TEMP_FILE_TO_RESULT).toPath()))}
        return LAST_RESPONCE()
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
            if (getPaymentStatus(orderNumber).equals(NEW_ATOM))
                return ""
        var prepared = PSADSLProcessor.psearch.psaconnector.executor!!.conn.prepareStatement(
            "UPDATE `psa`.`payments` SET `document_id`='${Responce.order_id()}', `status`='${MAP_STATUS.get(Responce.error_code().toInt())}' WHERE `id`='$orderNumber' "
        );
        println("prepared=> $prepared")
        if (prepared != null)
            prepared.execute()
        return Responce
    }


    override fun appendRole(R: Role){
        when (R?.Name){
            "endpoint"              ->  mapper.put(R, endpoint)
            "login"                 ->  mapper.put(R, login)
            "pass"                  ->  mapper.put(R, pass)
            "registerp2p"           ->  mapper.put(R, registerp2p)
            "enabled"               ->  mapper.put(R, enable)
            "binding_id"            ->  mapper.put(R, binding_id)
            "HOOK"                  ->  mapper.put(R, HOOK)
            "REJECT_NEW"            ->  mapper.put(R, REJECT_NEW)
            "perfomP2P"             ->  mapper.put(R, perfomP2P)
            "bindingId"             ->  mapper.put(R, bindingId)
            "KEY"                   ->  mapper.put(R, KEY)
            "getstatus"             ->  mapper.put(R, getstatus)



        }
    }

}