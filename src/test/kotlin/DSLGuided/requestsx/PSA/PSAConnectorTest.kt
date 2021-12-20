package DSLGuided.requestsx.PSA

import junit.framework.TestCase

class PSAConnectorTest : TestCase() {

    fun testRender() {
        val dsl =        "'psaconnector'=>::psalogin{root},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'},::timedbreconnect{1}."
        val psaConnector = PSAConnector()
        psaConnector.r(dsl)
        assertEquals("Pf,dtybt010203", psaConnector.pass)

    }


    fun testRecharge() {
        val dsl =        "'psaconnector'=>::psalogin{root},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'},::timedbreconnect{1}."
        val psaConnector = PSAConnector()
        psaConnector.r(dsl)
        assertEquals(1, psaConnector.delay)
////        Thread.sleep(10)
    }
}