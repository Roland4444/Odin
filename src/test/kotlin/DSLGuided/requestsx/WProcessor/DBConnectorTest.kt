package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import kotlin.test.assertNotEquals

class DBConnectorTest : TestCase() {

    fun testRender() {
        val dsl = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val Connector = DBConnector()
        Connector.render(dsl)
        assertEquals("avs", Connector.login)
        assertEquals("123", Connector.pass)
        assertNotEquals(null, Connector.executor?.conn)
    }
}