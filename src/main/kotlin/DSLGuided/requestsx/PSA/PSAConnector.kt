package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import fr.roland.DB.Executor
import se.roland.abstractions.Call
import se.roland.util.Watcher
/////"'psaconnector'=>::psalogin{root},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'},::timedbreconnect{1}."
class PSAConnector  : DSLProcessor() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val dsl =        "'psaconnector'=>::psalogin{root},::psapass{'Pf,dtybt010203'},::db{jdbc:mysql://localhost/psa},::enabled{'false'},::timedbreconnect{1}."
            val psaConnector = PSAConnector()
            psaConnector.render(dsl)

////        Thread.sleep(10)
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
      //  executor = Executor(urldb, login, pass)
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
    val psa: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psa")
                processPSASection(a.key.Param as MutableList<Any>)
        }
    }

    val psalogin: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psalogin"){
                login = a.key.Param as String
            }
             //   processPSASection(a.key.Param as MutableList<Any>)
        }
    }

    val psapass: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psapass"){
                pass = a.key.Param as String
            }
              //  processPSASection(a.key.Param as MutableList<Any>)
        }
    }

    val timedbreconnect: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "timedbreconnect"){
                delay = a.key.Param as Int
            }
            //  processPSASection(a.key.Param as MutableList<Any>)
        }
    }



    fun recharge(){
        if (enabled == "true") {
            executor = Executor(urldb, login, pass)
        }
    }


    fun processPSASection(input: MutableList<Any>) {
        input.forEach {
            val f: KeyValue = it as KeyValue
            println("""KEY VALUE ${f.Key}::${f.Value}""")
            when ((it as KeyValue).Key) {
                "login" -> login = it.Value as String;
                "pass" -> pass = it.Value as String;

            }
        }
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
            "psa" -> mapper.put(R, psa)
            "db" -> mapper.put(R, db)
            "enabled" -> mapper.put(R, enable)
            "psapass" -> mapper.put(R, psapass)
            "psalogin" -> mapper.put(R, psalogin)
            "timedbreconnect" -> mapper.put(R, timedbreconnect)
        }
    }

}