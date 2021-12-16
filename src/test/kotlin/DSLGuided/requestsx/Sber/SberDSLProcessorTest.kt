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
        val DSL4SberInitial = "'sber'=>::endpoint{$test},::login{test_AVS-api},::pass{test_AVS}."
        Sber.render(DSL4SberInitial)
        val StrRequest = Sber.constructDSL4registerP2p(145780)
        println("STRING TO REQUEST::$StrRequest")
        Sber.render(StrRequest)

    }

}