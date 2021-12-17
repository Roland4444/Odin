package DSLGuided.requestsx.Sber

import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import junit.framework.TestCase

class SberDSLProcessorTest : TestCase() {
    val initDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val psaconnector = PSAConnector()
    fun testRender() {
        val EndP = "https://123"
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.render("'sber'=>::endpoint{$EndP}.")
        assertEquals(EndP, Sber.endpoint_())
    }
    fun testLoginPass() {
        val EndP = "https://123"
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.render("'sber'=>::endpoint{$EndP},::login{hello},::pass{pass}.")
        assertEquals({"hello"}(), Sber.login_())
        assertEquals({"pass"}(),  Sber.pass_())
    }

    fun testHS(){
        val EndP = "https://123"
        val Etalon_Header = """
        <soapenv:Header>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" env:mustUnderstand="1" soapenv:mustUnderstand="1">
        <wsse:UsernameToken wsu:Id="UsernameToken-UUID">
        <wsse:Username>hello</wsse:Username>
        <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">pass</wsse:Password>
        </wsse:UsernameToken>        
        """.trimIndent()
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.render("'sber'=>::endpoint{https://123},::login{hello},::pass{pass}.")
        assertEquals(Etalon_Header, Sber.headersecurity())
    }

    fun testgeneratefrompsaId(){
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
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
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(145780))
        println(Sber.constructDSL4registerP2p(145780))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::HOOK{true,'ordernumber':'7878787'}."
        Sber.render(DSL4SberInitial)
        val StrRequest = Sber.constructDSL4registerP2p(145780)
        println("STRING TO REQUEST::$StrRequest")
        Sber.render(StrRequest)
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
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS}."
        Sber.render(DSL4SberInitial)
        val StrRequest = Sber.constructDSL4registerP2p(psaid)
        println("STRING TO REQUEST::$StrRequest")
        Sber.render(StrRequest)
    }

    fun testRejectNew(){
        val psaid = 148233
        val test = "https://3dsec.sberbank.ru/payment/webservices/p2p?wsdl"
        var psa  = PSADSLProcessor()
        val psaconnstr = "'psaconnector'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val psastr = "'psa'=>::notupdate{true},::default1{true},::log{'true':'psadsl.log'},::number_at_2_w{true},::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::HOOK{'false','section':'244'},::enabled{'true'}.:-:HOOK{'true','section':'2','uuid':'55555'}\n"
        psaconnector.render(psaconnstr)
        val PSASearchProcessor = PSASearchProcessor()
        PSASearchProcessor.psaconnector= psaconnector
        psa.psearch=PSASearchProcessor
        psa.render(psastr)
        val Sber = SberDSLProcessor()
        Sber.PSADSLProcessor=psa
        assertNotNull(Sber.constructDSL4registerP2p(psaid))
        println(Sber.constructDSL4registerP2p(psaid))
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{true}."
        Sber.render(DSL4SberInitial)
        assertEquals(Sber.TRUE_ATOM, Sber.REJECT_NEW_())
        val PaymentID = 60224
        val StrRequest = "'sber'=>::registerp2p{'amount':100,'currency':643,'orderNumber':$PaymentID,'clientId':31279}."
        println("STRING TO REQUEST::$StrRequest")
        Sber.render(StrRequest)
        Sber.setPaymentStatus(PaymentID, Sber.NEW_ATOM)
        val STATUS = Sber.getPaymentStatus(PaymentID)
        assertEquals(Sber.NEW_ATOM, STATUS)
        val DSL4SberInitial2 = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS},::REJECT_NEW{false}."
        Sber.render(DSL4SberInitial2)
        Sber.render(StrRequest)
        val STATUS2 = Sber.getPaymentStatus(PaymentID)
        assertEquals(Sber.REJECTED_ATOM, STATUS2)
    }



}