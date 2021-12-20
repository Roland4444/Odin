package DSLGuided.requestsx.PaymentDSL

import junit.framework.TestCase
import se.roland.util.HTTPClient

class PaymentProcessorTest : TestCase() {

    fun testRender() {
        val dsl = "'paymentprocessor'=>::rooturl{https://xn--80aqu.xn--g1axi.xn--p1ai},::key{KJDsdlkfjlds1321},::enabled{'true'},::basiclicenceid{29f662a8-72ad-496a-a456-acde0673549f},::apikey{2bfdb2fa-6d00-43af-aaaa-25a10aa3290c}."
        val stringpayId = "a7132f96-657e-7339-81ab-b24b02131266"

        val PaymentProcessor = PaymentProcessor()
        val echourl = "http://192.168.0.126:4567/echo2";
        PaymentProcessor.r(dsl)
        assertEquals("https://xn--80aqu.xn--g1axi.xn--p1ai", PaymentProcessor.rooturl_)
        assertEquals("KJDsdlkfjlds1321", PaymentProcessor.key_)
        assertEquals("29f662a8-72ad-496a-a456-acde0673549f", PaymentProcessor.BasicLicenceId)
        assertEquals("2bfdb2fa-6d00-43af-aaaa-25a10aa3290c", PaymentProcessor.ApiKey)
        val effective_url = PaymentProcessor.rooturl_+PaymentProcessor.lisstattus
        print(effective_url)
        val UserLogin = "79033486557"
        val UserPass  = "HD1CODqkoLdJi"
        val autopstr = "${PaymentProcessor.BasicLicenceId}:${PaymentProcessor.ApiKey}:$UserLogin:$UserPass"
        println("\n\nAUTORIZATION STR:: $autopstr")
        val resp = HTTPClient.sendPostwithAutorisation(effective_url, autopstr)
        println("RESPONCE:: $resp")
    }

    fun testcheckpay() {
        val dsl = "'paymentprocessor'=>::rooturl{https://xn--80aqu.xn--g1axi.xn--p1ai},::key{KJDsdlkfjlds1321},::enabled{'true'},::basiclicenceid{29f662a8-72ad-496a-a456-acde0673549f},::apikey{2bfdb2fa-6d00-43af-aaaa-25a10aa3290c}."
        val stringpayId = "759027"
        val PaymentProcessor = PaymentProcessor()
        val echourl = "http://192.168.0.126:4567/echo2";
        PaymentProcessor.r(dsl)
        assertEquals("https://xn--80aqu.xn--g1axi.xn--p1ai", PaymentProcessor.rooturl_)
        assertEquals("KJDsdlkfjlds1321", PaymentProcessor.key_)
        assertEquals("29f662a8-72ad-496a-a456-acde0673549f", PaymentProcessor.BasicLicenceId)
        assertEquals("2bfdb2fa-6d00-43af-aaaa-25a10aa3290c", PaymentProcessor.ApiKey)
        val effective_url = PaymentProcessor.rooturl_+PaymentProcessor.checkpay
        print(effective_url)
        val UserLogin = "79881760729"
        val UserPass  = "TPiPiY14uoy"
        val autopstr = "${PaymentProcessor.BasicLicenceId}:${PaymentProcessor.ApiKey}:$UserLogin:$UserPass"
        println("\n\nAUTORIZATION STR:: $autopstr")
        val resp = HTTPClient.checkpayment(effective_url, stringpayId, autopstr)
        println("RESPONCE:: $resp")

    }
}