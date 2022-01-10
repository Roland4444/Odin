package DSLGuided.requestsx.Supervisor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.Sber.simpleString
import abstractions.Role
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.Handler
import se.roland.abstractions.Call
import se.roland.util.GLOBAL.LOG
import se.roland.util.Memory
import se.roland.util.Watcher
import java.io.File

class DSLSupervisor: DSLProcessor() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>){
            val FILELOG_ = "text.log"
            val ALERTLOG_ = "alertlog.log"
            val dsl = """
            'SW'=>::filelog{'$FILELOG_'},
            ::delay{2},::enabled{true},::threshold{4000},::alertfile{$ALERTLOG_},::port{12555}.
            """
            val SW = DSLSupervisor()
            SW.r(dsl)
        }
    }
    fun getMemfromString(input: String): Int{
        if (input.indexOf("FREE MEM::")<0)
            return 0
        return input.substring(input.indexOf("FREE MEM::")+"FREE MEM::".length).toInt()
    }

    var FILELOG: simpleString = {"initialsupervisor.log"}
    var DELAY                 = {100}
    var THRESHOLD             = {500}
    var ALERT_FILE            = {"THRESHOLD.log"}
    var PORT                  = {12345}
    lateinit var WorkThread : Watcher
    val t = Memory()

    fun getmemoryUsage(): String{
        val SB = StringBuilder()
        SB.append("<!DOCTYPE html>")
        SB.append("<head>\n    <meta charset=\"UTF-8\">\n</head>")
        File(FILELOG()).forEachLine {
            if (getMemfromString(it)< THRESHOLD())
                SB.append("<h5 style=\"color: red\">$it</h5>")
            else
                SB.append("<h5 style=\"color: green\">$it</h5>")
        }
        return SB.toString()
    }
    val muHandler: Handler = object:Handler{
        override fun handle(p0: Context) {
            p0.html(getmemoryUsage())        }
    }

    val mvHandler: Handler = object:Handler{
        override fun handle(p0: Context) {
            p0.html(getmemoryWarnings())        }
    }


    fun getmemoryWarnings(): String{
        val SB = StringBuilder()
        SB.append("<!DOCTYPE html>")
        SB.append("<head>\n    <meta charset=\"UTF-8\">\n</head>")
        File(ALERT_FILE()).forEachLine { SB.append("<h5 style=\"color: red\">$it</h5>") }
        return SB.toString()
    }
    override fun r(DSL: String): Any {
        println("SUPERVISOR setting up.......")
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        LOG( "SUPERVISOR setting up.......", FILELOG())
        WorkThread = Watcher(DELAY())
        val app = Javalin.create().start(PORT())
        app.get("/" , muHandler)
        app.get("/mu" , muHandler)
        app.get("/mw", mvHandler)
        WorkThread.callback = object : Call {
            override fun doIt() {
                    var FREE_MEM = t.getfreeMem() / 1024
                    LOG( ("FREE MEM::$FREE_MEM\n").toString(), FILELOG())
                    println("FREE MEM:" + FREE_MEM)
                    if (FREE_MEM<= THRESHOLD())
                        LOG( "MEMORY IN CRITICAL LEVEL!!!", ALERT_FILE())
            } }
        if (enabled.equals(TRUE_ATOM))
            WorkThread.start()
        return "OK"
    }

    override fun appendRole(R: Role) {
        when (R?.Name) {
            "filelog" -> mapper.put(R, filelog)
            "enabled" -> mapper.put(R, enable)
            "delay" -> mapper.put(R, delay)
            "threshold" -> mapper.put(R, threshold)
            "alertfile" -> mapper.put(R, alertfile)
            "port" -> mapper.put(R, port)
        }

    }

    val threshold: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "threshold")
                THRESHOLD = {a.key.Param as Int}
        }
    }

    val alertfile: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "alertfile")
                ALERT_FILE = {a.key.Param as String}
        }
    }

    val port: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "port")
                PORT = {a.key.Param as Int}
        }
    }

    val filelog: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "filelog")
                FILELOG = {a.key.Param as String}
        }
    }
    val delay: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "delay")
                DELAY = {a.key.Param as Int}
        }
    }
}