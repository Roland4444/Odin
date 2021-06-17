package DSLGuided.requestsx.WProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
import fr.roland.DB.Executor
import se.roland.abstractions.Call
import se.roland.util.Watcher

/////"'dbconnector'=>::dblogin{root},::dbpass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'},::timedbreconnect{1}."

class DBConnector : DSLProcessor() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val dsl =        "'dbconnector'=>::dblogin{root},::dbpass{'123'},::db{jdbc:mysql://192.126.0.121/psa},::enabled{'false'},::timedbreconnect{1}."
            val psaConnector = PSAConnector()
            psaConnector.render(dsl)
        }
    }
    var login: String=""
    var pass: String=""
    var urldb: String =""
    var delay = 3600
    var executor: Executor? = null
    lateinit var watcher: Watcher
    ///var ExecutorFree: Boolean = true
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        recharge()
        val watcher = Watcher(delay)
        watcher.callback = object : Call {
            override fun doIt() {
                println("Reconnecting to db")
                recharge()
            }
        }
        watcher.start()
        println("Starting watcher to reconnect DB!!!")
        return "OK"
    }
    val db: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "db")
                urldb = a.key.Param as String
        }
    }
    val dblogin: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "dblogin")
                login = a.key.Param as String
            //   processPSASection(a.key.Param as MutableList<Any>)
        }
    }
    val dbpass: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "dbpass")
                pass = a.key.Param as String
            //  processPSASection(a.key.Param as MutableList<Any>)
        }
    }
    val timedbreconnect: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "timedbreconnect")
                delay = a.key.Param as Int
            //  processPSASection(a.key.Param as MutableList<Any>)
        }
    }

    fun recharge(){
        if (enabled == "true")
            executor = Executor(urldb, login, pass)
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }

    fun appendRole(R: Role){
        when (R?.Name){
            "db" -> mapper.put(R, db)
            "enabled" -> mapper.put(R, enable)
            "dbpass" -> mapper.put(R, dbpass)
            "dblogin" -> mapper.put(R, dblogin)
            "timedbreconnect" -> mapper.put(R, timedbreconnect)
        }
    }
}