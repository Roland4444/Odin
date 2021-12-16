package DSLGuided.requestsx.Sber

import junit.framework.TestCase

class SberDSLProcessorTest : TestCase() {

    fun testRender() {
        val EndP = "https://123"
        val Sber = SberDSLProcessor()
        assertEquals(Sber.DEFAULT_URL, Sber.endpoint_())
        Sber.render("'sber'=>::endpoint{$EndP}.")
        assertEquals(EndP, Sber.endpoint_())
    }
}