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
            ::delay{0},::enabled{true},::threshold{30000},::alertfile{$ALERTLOG_},::port{12555}.
            """
        val SW = DSLSupervisor()
        SW.r(dsl)
        assertEquals(0, SW.DELAY())
        assertEquals(FILELOG_, SW.FILELOG())
        assertEquals(30000, SW.THRESHOLD())
        assertEquals(ALERTLOG_, SW.ALERT_FILE())
        assertEquals(12555, SW.PORT())
        Thread.sleep(500)
        assertTrue(File(FILELOG_).exists())
        assertTrue(File(ALERTLOG_).exists())
    }

    fun testgetMem(){
        val StrMem = "2022-01-10T15:07:50.652814295::FREE MEM::3931"
        val SW = DSLSupervisor()
        assertEquals(3931, SW.getMemfromString(StrMem))
    }
}