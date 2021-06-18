package DSLGuided.requestsx.EcoProcessor

import junit.framework.TestCase

class EcoProcessorTest : TestCase() {

    fun testRender() {
        val dsl = "'eco'=>::generatefor{'quarter':4,'year':2019,'department':6},::enabled{'false'}."
        val EcoProc = EcoProcessor()
        EcoProc.render(dsl)
        assertEquals(4, EcoProc.quarter)
        assertEquals(2019, EcoProc.year)
        assertEquals(6, EcoProc.department)


    }
}