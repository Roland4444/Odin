package DSLGuided.requestsx
import junit.framework.TestCase
import org.junit.Test

class requestsDSLProcessorTest : TestCase() {
    @Test
    fun testGetDumbHandler() {
        val req = RequestsDSLProcessor()
        req.outtemplate = "12xxx"
        assertEquals("12xxx", req.outtemplate)
        assertEquals(4, req.add(2))
        assertEquals(7, req.add2(2,5))
    }

    @Test
    fun testApplyrules() {
        val input: String = """'requests' => ::read{}, ::write{}, ::create{}, ::super{}."""
        val req = RequestsDSLProcessor()
        req.r(input)
        //assertNotEquals(null, req.mapper)
    }
}