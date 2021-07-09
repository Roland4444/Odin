package DSLGuided.requestsx.HelperDBUpdate

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.WProcessor.DBConnector
import abstractions.Role
import se.roland.JSON.ParcedJSON
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


class HelperDBUpdate:  DSLProcessor() {
    companion object{
        fun fullupdate(Helper: HelperDBUpdate, DSL: String, JSON1: ParcedJSON, JSON2:ParcedJSON){
            Helper.render(DSL)
            Helper.fullupdate(JSON1, JSON2)
        }

        fun fullupdate(Helper: HelperDBUpdate,JSON1: ParcedJSON, JSON2:ParcedJSON){
            Helper.fullupdate(JSON1, JSON2)
        }
    }
    lateinit var dbconnector: DBConnector
    var hockDeleting_: Boolean = false
    var ProductionMode_ : Boolean = false

    override fun render(DSL: String): Any {
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }

    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }
    fun appendRole(R: Role){
        when (R?.Name){
            "hockDeleting" -> mapper.put(R, hockDeleting)
            "ProductionMode" -> mapper.put(R, ProductionMode)
            "enabled" -> mapper.put(R, enable)
       }
    }
    
    val hockDeleting: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "hockDeleting") {
                hockDeleting_ = a.key.Param.toString().toBoolean()
            }
        }
    }

    val ProductionMode: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "ProductionMode") {
                ProductionMode_ = a.key.Param.toString().toBoolean()
            }
        }
    }



    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    @Throws(SQLException::class)
    fun getMetalID(metal: String?): String? {
        val pst: PreparedStatement = dbconnector.executor!!.getConn().prepareStatement("SELECT * FROM metal where name=?")
        pst.setString(1, metal)
        val metals = pst.executeQuery()
        return if (metals.next()) metals.getObject("id").toString() else "-1"
    }

    @Throws(SQLException::class)
    fun getId(json: ParcedJSON): Int {
        println("INSIDE gitID")
        val arr: ArrayList<Any?> = ArrayList<Any?>()
        arr.add(json.Waybill_number)
        arr.add(json.Date)
        arr.add(json.Time)
        val r: ResultSet = dbconnector.executor!!.executePreparedSelect("SELECT * FROM weighings WHERE waybill = ? AND  date =? and time = ?",  arr    )
        println("EXECUTE SELECT")
        return if (r.next()) r.getObject("id") as Int else -1
    }

    @Throws(SQLException::class)
    fun getCount(json: ParcedJSON): Int {
        println("INSIDE gitID")
        val arr: ArrayList<Any?> = ArrayList()
        arr.add(json.Waybill_number)
        val r: ResultSet = dbconnector.executor!!.executePreparedSelect("SELECT * FROM weighings WHERE waybill = ?", arr)
        println("EXECUTE SELECT.... COUNTING ITEMS")
        var counter = 0
        while (r.next()) counter++
        return counter
    }


    @Throws(SQLException::class)
    fun updateComment(json: ParcedJSON) {
        if (!ProductionMode_)
            return
        val id = getId(json)
        val stmt: PreparedStatement = dbconnector.executor!!.getConn().prepareStatement("UPDATE weighings set comment = ?  WHERE id = ?")
        stmt.setString(1, json.Comment)
        stmt.setLong(2, id.toLong())
        stmt.executeUpdate()
    }


    @Throws(SQLException::class)
    fun productiondelete(json: ParcedJSON?, initial: ParcedJSON) {
        val stmt: PreparedStatement = dbconnector.executor!!.getConn()
            .prepareStatement("DELETE from weighing_items  WHERE weighing_id=? AND trash = ? AND clogging=? AND tare =? AND brutto =? AND metal_id=?") // metal_id =
        println("DELETING ITEM USING SQL::>>")
        println(
            "DELETE from weighing_items  WHERE weighing_id=<" + getId(json!!) + ">  AND trash =<" + initial.Trash + ">  AND clogging= <" + initial.Clogging + "> AND tare =<" + initial.Tara + "> AND brutto =" + initial.Brutto + " AND metal_id=<" + getMetalID(
                initial.Metall
            ).toString() + ">"
        )
        stmt.setInt(1, getId(json))
        System.out.println("initial trash:" + initial.Trash)
        stmt.setBigDecimal(2, BigDecimal(initial.Trash))
        System.out.println("initial clogging:" + initial.Clogging)
        stmt.setBigDecimal(3, BigDecimal(initial.Clogging))
        System.out.println("initial tare:" + initial.Tara)
        stmt.setBigDecimal(4, BigDecimal(initial.Tara))
        System.out.println("initial brutto:" + initial.Brutto)
        stmt.setBigDecimal(5, BigDecimal(initial.Brutto))
        System.out.println("initial metal_id:" + initial.Metall)
        stmt.setString(6, getMetalID(initial.Metall).toString())
        if (hockDeleting_) {
            println("HOOK ON DELETE!!!")
            return
        }
        stmt.executeUpdate()
        if (getCount(initial) < 2) {
            val stmt2: PreparedStatement = dbconnector.executor!!.getConn().prepareStatement("DELETE from weighings WHERE id = ?")
            stmt2.setLong(1, getId(initial).toLong())
            stmt2.executeUpdate()
        }
    }

    @Throws(SQLException::class)
    fun updateweighing_items(json: ParcedJSON, initial: ParcedJSON) {
        if (!ProductionMode_)
            return
        val id = getId(json)
        val stmt: PreparedStatement = dbconnector.executor!!.getConn()
            .prepareStatement("update weighing_items set trash = ?, clogging=?, tare =?, brutto =?, metal_id=?  WHERE weighing_id=? AND trash = ? AND clogging=? AND tare =? AND brutto =? AND metal_id=?") // metal_id =
        System.out.println("TRASH:" + json.Trash)
        stmt.setBigDecimal(1, BigDecimal(json.Trash))
        System.out.println("clogging:" + json.Clogging)
        stmt.setBigDecimal(2, BigDecimal(json.Clogging))
        System.out.println("Tara:" + json.Tara)
        stmt.setBigDecimal(3, BigDecimal(json.Tara))
        System.out.println("Brutto:" + json.Brutto)
        stmt.setBigDecimal(4, BigDecimal(json.Brutto))
        println("Metall:" + getMetalID(json.Metall))
        stmt.setString(5, getMetalID(json.Metall).toString())
        println("id:$id")
        stmt.setInt(6, id)
        System.out.println("initial trash:" + initial.Trash)
        stmt.setBigDecimal(7, BigDecimal(initial.Trash))
        System.out.println("initial clogging:" + initial.Clogging)
        stmt.setBigDecimal(8, BigDecimal(initial.Clogging))
        System.out.println("initial tare:" + initial.Tara)
        stmt.setBigDecimal(9, BigDecimal(initial.Tara))
        System.out.println("initial brutto:" + initial.Brutto)
        stmt.setBigDecimal(10, BigDecimal(initial.Brutto))
        System.out.println("initial metal_id:" + initial.Metall)
        stmt.setString(11, getMetalID(initial.Metall).toString())
        stmt.executeUpdate()
        updateComment(json)
    }

    @Throws(SQLException::class)
    fun fullupdate(json: ParcedJSON, initial: ParcedJSON) {
        if (!ProductionMode_)
            return
        if (json.Brutto.equals("0.00")) {
            productiondelete(json, initial)
            return
        }
        updateComment(json)
        updateweighing_items(json, initial)
    }



}