package DSLGuided.requestsx

import junit.framework.TestCase

class DSLTest : TestCase() {

    fun GetRequestsDSLProcessor() {
        val DSL2 = DSL()
     //   assertNotNull(DSL.getDSLProc(DSL.RequestsDSLProcessor_ATOM))
        assertEquals(DSL2.RequestsDSLProcessor, DSL2.getDSLProc(DSL.RequestsDSLProcessor_ATOM))    }


    fun testc(){
        assertEquals(1,1)
    }
}