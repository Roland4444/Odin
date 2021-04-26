package DSLGuided.requestsx.PSA

import junit.framework.TestCase

class PSAConnectorTest : TestCase() {

    fun testRender() {
        val dsl =        "'psaconnector'=>::psalogin{root},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'}."
        val psaConnector = PSAConnector()
        psaConnector.render(dsl)
        assertEquals("Pf,dtybt010203", psaConnector.pass)

    }
}