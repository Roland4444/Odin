package DSLGuided.requestsx.HelperDBUpdate

import DSLGuided.requestsx.WProcessor.DBConnector
import junit.framework.TestCase

class HelperDBUpdateTest : TestCase() {

    fun testParseRoles() {
        val dsl = "'dbhelper'=>::hockDeleting{true},::ProductionMode{true},::enabled{'true'}."
        val initDB = "'dbconnector'=>::dblogin{avs},::dbpass{'123'},::db{jdbc:mysql://192.168.0.173:3306/avs?autoReconnect=true},::enabled{'true'},::timedbreconnect{3600}. ////\n"
        val dbconnector = DBConnector()
        dbconnector.render(initDB)
        val HelperDBUpdate = HelperDBUpdate()
        HelperDBUpdate.dbconnector=dbconnector
        HelperDBUpdate.render(dsl)
        assertEquals(true, HelperDBUpdate.ProductionMode_)
        assertEquals(true, HelperDBUpdate.hockDeleting_)
        val dsl2 = "'dbhelper'=>::hockDeleting{false},::ProductionMode{true},::enabled{'true'}."
        HelperDBUpdate.render(dsl2)
        assertEquals(true, HelperDBUpdate.ProductionMode_)
        assertEquals(false, HelperDBUpdate.hockDeleting_)
    }
}