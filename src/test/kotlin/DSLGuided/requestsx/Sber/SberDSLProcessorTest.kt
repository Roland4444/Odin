package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import junit.framework.TestCase
import se.roland.abstractions.timeBasedUUID.generateInt


class SberDSLProcessorTest : TestCase() {
    val PREPARED = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:p2p="http://engine.paymentgate.ru/webservices/p2p" xmlns:env="env">
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
					<orderId>8563aa00-6b32-7168-8264-91ac2823c181</orderId> 
					<fromCard>
						<bindingId>6cc2cc38-3677-7330-9b6b-54b62823c181</bindingId>
					</fromCard> 
					<toCard>
						<seToken>AkHYc4kCTl84YNymdx+sx1L0k5ue0rOXCyX2SFl4M4hmdXreEJV8Veqb/DiLAchvgX+LzXfh9OknHoI+vgsaqnZRmvW9+oqJsKE/jXspJ37Z5p3mahqGvhNFxpQV6Z0gLfe9f/enicbHYV3zVaKLqrz75j/vBnLIebenRo1k7XpL4gnCHaHa0l7mVeaMQhOic4sRXhLpUPRZ3Tn3EX2rdpZhs1Pi43SryaNF8Jx3WBUu3X/0vxEv6eg9MgCRvfhc9fMiTY1k6iM3gV4Skt6gHBsunRONSbHP9JkV1XUnz/PElWEN75vTikn81OgNMXI5NvY86e4HHgWdV+yQKS145Q==</seToken> 
					</toCard> 
				</arg0> 
			</p2p:performP2PByBinding>
		</soapenv:Body>
	</soapenv:Envelope>
    """
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testRender() {
        val EndP = "https://123"
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.r("'sber'=>::endpoint{$EndP}.")
        assertEquals(EndP, Sber.endpoint_())
    }
    fun testLoginPass() {
        val EndP = "https://123"
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.r("'sber'=>::endpoint{$EndP},::login{hello},::pass{pass}.")
        assertEquals({"hello"}(), Sber.login_())
        assertEquals({"pass"}(),  Sber.pass_())
    }

    fun testHS(){
        val EndP = "https://123"
        val Etalon_Header =
            """
        <soapenv:Header>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" env:mustUnderstand="1" soapenv:mustUnderstand="1">
        <wsse:UsernameToken wsu:Id="UsernameToken-UUID">
        <wsse:Username>hello</wsse:Username>
        <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">pass</wsse:Password>
        </wsse:UsernameToken></wsse:Security>
</soapenv:Header>   
""".trimIndent()
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.r("'sber'=>::endpoint{https://123},::login{hello},::pass{pass}.")
        assertEquals(Etalon_Header, Sber.headersecurity())
    }

    fun testgeneratefrompsaId(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(145780))
        println(Sber.constructDSL4registerP2p(145780))
    }

    fun testprocesspsaId(){
        val psaid = 145780
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(145780))
        println(Sber.constructDSL4registerP2p(145780))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::HOOK{true,'ordernumber':'${generateInt()}'}."
        Sber.r(DSL4SberInitial)
        val StrRequest = Sber.constructDSL4registerP2p(145780)
        println("STRING TO REQUEST::$StrRequest")
        Sber.r(StrRequest)
        println(Sber.order_id_(Sber.LAST_RESPONCE()))
    }

    fun testparcereposnec(){
        val Sber = SberDSLProcessor()
        val Resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:registerP2PResponse xmlns:ns1=\"http://engine.paymentgate.ru/webservices/p2p\"><return xmlns:ns2=\"http://engine.paymentgate.ru/webservices/p2p\" orderNumber=\"60224\" errorCode=\"0\" errorMessage=\"Успешно\"><orderId>1a00a8df-c705-72de-8cba-ed2b2823c181</orderId><formUrl>https://3dsec.sberbank.ru/payment/merchants/p2p_aft_oct/payment_ru.html?mdOrder=1a00a8df-c705-72de-8cba-ed2b2823c181</formUrl></return></ns1:registerP2PResponse></soap:Body></soap:Envelope><?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:registerP2PResponse xmlns:ns1=\"http://engine.paymentgate.ru/webservices/p2p\"><return xmlns:ns2=\"http://engine.paymentgate.ru/webservices/p2p\" orderNumber=\"60224\" errorCode=\"0\" errorMessage=\"Успешно\"><orderId>1a00a8df-c705-72de-8cba-ed2b2823c181</orderId><formUrl>https://3dsec.sberbank.ru/payment/merchants/p2p_aft_oct/payment_ru.html?mdOrder=1a00a8df-c705-72de-8cba-ed2b2823c181</formUrl></return></ns1:registerP2PResponse></soap:Body></soap:Envelope>\n"
        assertEquals(Sber.error_code_(Resp), "0")
        assertEquals(Sber.order_id_(Resp), "1a00a8df-c705-72de-8cba-ed2b2823c181")
    }

    fun testprocesspsaIdwithsaveinDB(){
        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS}."
        Sber.r(DSL4SberInitial)
        val StrRequest = Sber.constructDSL4registerP2p(psaid)
        println("STRING TO REQUEST::$StrRequest")
        Sber.r(StrRequest)
    }

    fun testRejectNew(){
        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true}."
        Sber.r(DSL4SberInitial)
        assertEquals(Sber.TRUE_ATOM, Sber.REJECT_NEW_())
        val PaymentID = 60224
        val StrRequest = "'sber'=>::registerp2p{'amount':100,'currency':643,'orderNumber':$PaymentID,'clientId':31279}."
        println("STRING TO REQUEST::$StrRequest")
        Sber.r(StrRequest)
        Sber.setPaymentStatus(PaymentID, Sber.NEW_ATOM)
        val STATUS = Sber.getPaymentStatus(PaymentID)
        assertEquals(Sber.NEW_ATOM, STATUS)
        val DSL4SberInitial2 = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{false}."
        Sber.r(DSL4SberInitial2)
        Sber.r(StrRequest)
        val STATUS2 = Sber.getPaymentStatus(PaymentID)
        assertEquals(Sber.REJECTED_ATOM, STATUS2)
    }

    fun testbindingId(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true},::bindingId{456464634-5464654654-65776657}."
        Sber.r(DSL4SberInitial)
        assertEquals("456464634-5464654654-65776657", Sber.binding_id_())
    }

    fun testKEY(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        val DSL4SberInitial = """'sber'=>::KEY{'public':'pub.key','private':'priv.key'},
::endpoint{$test},
::login{test_AVS-api},
::pass{test_AVS},
::REJECT_NEW{true},
::bindingId{6cc2cc38-3677-7330-9b6b-54b62823c181}.
                """
        Sber.r(DSL4SberInitial)

        val KEY_ETALON =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjH8R0jfvvEJwAHRhJi2Q4fLi1p2z10PaDMIhHbD3fp4OqypWaE7p6n6EHig9qnwC/4U7hCiOCqY6uYtgEoDHfbNA87/X0jV8UI522WjQH7Rgkmgk35r75G5m4cYeF6OvCHmAJ9ltaFsLBdr+pK6vKz/3AzwAc/5a6QcO/vR3PHnhE/qU2FOU3Vd8OYN2qcw4TFvitXY2H6YdTNF4YmlFtj4CqQoPL1u/uI0UpsG3/epWMOk44FBlXoZ7KNmJU29xbuiNEm1SWRJS2URMcUxAdUfhzQ2+Z4F0eSo2/cxwlkNA+gZcXnLbEWIfYYvASKpdXBIzgncMBro424z/KUr3QIDAQAB"
        assertEquals("6cc2cc38-3677-7330-9b6b-54b62823c181", Sber.binding_id_())
        assertEquals(KEY_ETALON, Sber.PUBLIC_KEY())
    }

    fun testTimestamp(){
        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa = PSADSLProcessor()
        val psaconnstr =
            "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr =
            "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        assertNotNull(Sber.timestamp())
        println(Sber.timestamp())
    }


    fun testPerformP2p() {

        val psaid = 29128
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa = PSADSLProcessor()
        val psaconnstr =
            "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr =
            "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor = psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))

        val DSL4SberInitial =
          "'sber'=>::KEY{'public':'pub.key','private':'priv.key'},::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true},::bindingId{6cc2cc38-3677-7330-9b6b-54b62823c181},::HOOK{true,'ordernumber':'${generateInt()}'}."
        Sber.r(DSL4SberInitial)
        assertEquals("6cc2cc38-3677-7330-9b6b-54b62823c181", Sber.binding_id_())
        val StrRequest = Sber.constructDSL4registerP2p(29128)
        println("STRING TO REQUEST::$StrRequest")
        Sber.r(StrRequest)
        val orderId = Sber.order_id_(Sber.LAST_RESPONCE())
        println("LAST RESPONCE:: ${Sber.LAST_RESPONCE()}")
        assertEquals("Успешно", Sber.error_message_(Sber.LAST_RESPONCE()))
        val dslTopay = "'sber'=>::perfomP2P{'orderId':${orderId}, 'PAN':'4111111111111111'}."
        println("PAY!!!!")
        Sber.r(dslTopay)
        assertNotNull(Sber.LAST_RESPONCE)
        assertEquals("0", Sber.Extraktor.extractAttribute(Sber.LAST_RESPONCE().toByteArray(), "errorCode"))

        println("RESPONCE::::${Sber.LAST_RESPONCE()}")
    }



    fun testPerformDirect() {

        val psaid = 148233
        val test = ""
        var psa = PSADSLProcessor()
        val psaconnstr =
            "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr =
            "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor = psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))

        val DSL4SberInitial =
            "'sber'=>::KEY{'public':'pub.key','private':'priv.key'},::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true},::bindingId{6cc2cc38-3677-7330-9b6b-54b62823c181},::HOOK{true,'ordernumber':'${generateInt()}'}."
        Sber.r(DSL4SberInitial)
        Sber.send(Sber.DIRECT())

        println("RESPONCE::::${Sber.LAST_RESPONCE()}")
    }

    fun testse(){
        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa = PSADSLProcessor()
        val psaconnstr =
            "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr =
            "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor = psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))


        val DSL4SberInitial2 =
            "'sber'=>::KEY{'public':'pub.key','private':'priv.key'},::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true},::bindingId{6cc2cc38-3677-7330-9b6b-54b62823c181},::HOOK{true,'ordernumber':'${generateInt()}'}."
        Sber.r(DSL4SberInitial2)
        val DataToEncrypt = "2021-12-20T18:30:03+03:00/520cc423-61a1-11ec-8ded-93c4c95e7fc7/4111111111111111///b4b69146-9a0f-745f-bd29-461a2823c181"
        val PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjH8R0jfvvEJwAHRhJi2Q4fLi1p2z10PaDMIhHbD3fp4OqypWaE7p6n6EHig9qnwC/4U7hCiOCqY6uYtgEoDHfbNA87/X0jV8UI522WjQH7Rgkmgk35r75G5m4cYeF6OvCHmAJ9ltaFsLBdr+pK6vKz/3AzwAc/5a6QcO/vR3PHnhE/qU2FOU3Vd8OYN2qcw4TFvitXY2H6YdTNF4YmlFtj4CqQoPL1u/uI0UpsG3/epWMOk44FBlXoZ7KNmJU29xbuiNEm1SWRJS2URMcUxAdUfhzQ2+Z4F0eSo2/cxwlkNA+gZcXnLbEWIfYYvASKpdXBIzgncMBro424z/KUr3QIDAQAB"
        val EtalonEncrypted = "WEQiLt94eJ0iUvNHLb+71bfODcOVi9hxThBGwnoZcSdBlss02gwhBmcL76SEmqSwSlB8HKeP7kcZAFv4cwJr1SdbSdqgMJvQ+sTDwHecnJXWn11rtzXLb9F8XyAuAnfPkIAg9+gt4aFifqBEwh4+J9BHzRjGKIRzG49aVi3wVv0gqBOfdDq177lN6Z8u5ioRVAMf3S9GP7BHZPw7ps+qq9K8BfhaVEoB6B25vGnhLE5hH3XqytV/7u0L9ga6A58yqfzZxOBj6cjm9UZggne/ItoxSgNGKXOw6Xn0qXU9sFMIgowWr+0xXPfWjr6Z5oEszPxd5o0GZnC8xDsHJRQIAQ=="
        val Timestamp = "2021-12-20T18:30:03+03:00"
        val UUID = "520cc423-61a1-11ec-8ded-93c4c95e7fc7"
        val PAN = "4111111111111111"
        val orderID = "b4b69146-9a0f-745f-bd29-461a2823c181"
        Sber.seToken(Timestamp, UUID, PAN,orderID)
        assertEquals(DataToEncrypt, Sber.SETOKEN())
////        assertEquals(EtalonEncrypted, Sber.seToken(Timestamp, UUID, PAN,orderID))
    }

    fun PerformP2p2() {

        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa = PSADSLProcessor()
        val psaconnstr =
            "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr =
            "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.r(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector = psaconnector
        psa.psearch = PSASearchProcessor
        psa.r(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor = psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))

        val DSL4SberInitial =
            "'sber'=>::KEY{'public':'pub.key','private':'priv.key'},::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true},::bindingId{6cc2cc38-3677-7330-9b6b-54b62823c181},::HOOK{true,'ordernumber':'${generateInt()}'}."
        Sber.r(DSL4SberInitial)
        Sber.send(String(Saver.Saver.readBytes("goxml.xml")))

        println("RESPONCE::::${Sber.LAST_RESPONCE()}")
    }



}