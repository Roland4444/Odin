package DSLGuided.requestsx.WProcessor

import junit.framework.TestCase
import java.sql.Connection
import java.sql.DriverManager.getConnection
import kotlin.test.assertNotEquals


class DBConnectorTest : TestCase() {

    fun testRender() {
        val dsl = "'dbconnector'=>::dblogin{'avs'},::dbpass{'123'},::db{jdbc:mysql://db2.avs.com.ru/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}."
        val Connector = DBConnector()
        Connector.r(dsl)
        assertEquals("avs", Connector.login)
        assertEquals("123", Connector.pass)
        assertNotEquals(null, Connector.executor?.conn)
    }

    fun testExecutor(){
        val myConn: Connection = getConnection("JDBC:mysql://db2.avs.com.ru/avs", "avs", "123")
        assertNotNull(myConn)

    }
}