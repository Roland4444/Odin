package DSLGuided.requestsx.Supervisor

import junit.framework.TestCase
import java.io.File

class DSLSupervisorTest : TestCase() {

    fun testGetFilelog() {
        val FILELOG_ = "text.log"
        if (File(FILELOG_).exists())
            File(FILELOG_).delete()
        val dsl = """
            'SW'=>::filelog{'$FILELOG_'},
            ::delay{0},::enabled{true}.
            """
        val SW = DSLSupervisor()
        SW.r(dsl)
        assertEquals(0, SW.DELAY())
        assertEquals(FILELOG_, SW.FILELOG())
        Thread.sleep(500)
        assertTrue(File(FILELOG_).exists())
    }
}