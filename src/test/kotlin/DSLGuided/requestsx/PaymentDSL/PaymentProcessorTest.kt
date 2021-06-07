package DSLGuided.requestsx.PaymentDSL

import junit.framework.TestCase

class PaymentProcessorTest : TestCase() {

    fun testRender() {
        val dsl = "'paymentprocessor'=>::rooturl{https://xn--80aqu.xn--g1axi.xn--p1ai/},::key{KJDsdlkfjlds1321},::enabled{'true'},::basiclicenceid{29f662a8-72ad-496a-a456-acde0673549f},::apikey{2bfdb2fa-6d00-43af-aaaa-25a10aa3290c}."
        val PaymentProcessor = PaymentProcessor()
        PaymentProcessor.render(dsl)
        assertEquals("https://xn--80aqu.xn--g1axi.xn--p1ai/", PaymentProcessor.rooturl_)
        assertEquals("KJDsdlkfjlds1321", PaymentProcessor.key_)
        assertEquals("29f662a8-72ad-496a-a456-acde0673549f", PaymentProcessor.BasicLicenceId)
        assertEquals("2bfdb2fa-6d00-43af-aaaa-25a10aa3290c", PaymentProcessor.ApiKey)



    }
}