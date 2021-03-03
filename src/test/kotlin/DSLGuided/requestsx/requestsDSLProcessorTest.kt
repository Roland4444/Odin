package DSLGuided.requestsx
import junit.framework.TestCase
import java.lang.reflect.Method
import kotlin.test.assertNotEquals
class requestsDSLProcessorTest : TestCase() {
    fun testGetDumbHandler() {
        val req = RequestsDSLProcessor()
        req.outtemplate = "12"
        req.test2(req.defaultParam)
        assertEquals("12xxx", req.outtemplate)
        assertEquals(4, req.add(2))
        assertEquals(7, req.add2(2,5))
        val method: Method = req.javaClass.getDeclaredMethod(
            "dumbsum",
            Int::class.java,
            Int::class.java
        )
        val sum = method.invoke(req, 4, 4)
        assertEquals(8, sum)
    }

    fun testApplyrules() {
        val input: String = """'requests' => ::read{}, ::write{}, ::create{}, ::super{}."""
        val req = RequestsDSLProcessor()
        req.render(input)
        assertNotEquals(null, req.mapper)
    }
}