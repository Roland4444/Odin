package DSLGuided.requestsx.Supervisor

import junit.framework.TestCase
import java.io.File

class DSLSupervisorTest : TestCase() {

    fun testGetFilelog() {
        val FILELOG_ = "text.log"
        val ALERTLOG_ = "alertlog.log"
        if (File(FILELOG_).exists())
            File(FILELOG_).delete()
        if (File(ALERTLOG_).exists())
            File(ALERTLOG_).delete()
        val dsl = """
            'SW'=>::filelog{'$FILELOG_'},
            ::delay{0},::enabled{true},::threshold{30000},::alertfile{$ALERTLOG_}.
            """
        val SW = DSLSupervisor()
        SW.r(dsl)
        assertEquals(0, SW.DELAY())
        assertEquals(FILELOG_, SW.FILELOG())
        assertEquals(30000, SW.THRESHOLD())
        assertEquals(ALERTLOG_, SW.ALERT_FILE())
        Thread.sleep(500)
        assertTrue(File(FILELOG_).exists())
        assertTrue(File(ALERTLOG_).exists())
    }
}