package DSLGuided.requestsx

import junit.framework.TestCase

class DSLTest : TestCase() {

    fun testGetRequestsDSLProcessor() {
        val DSL = DSL()
        assertNotNull(DSL.getDSLProc(DSL.RequestsDSLProcessor_ATOM))
        assertEquals(DSL.RequestsDSLProcessor, DSL.getDSLProc(DSL.RequestsDSLProcessor_ATOM))    }
}