package DSLGuided.requestsx.Supervisor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.Sber.simpleString
import abstractions.Role
import jdk.jfr.Threshold
import se.roland.abstractions.Call
import se.roland.util.GLOBAL.LOG
import se.roland.util.Memory
import se.roland.util.Watcher
import java.io.FileOutputStream

class DSLSupervisor: DSLProcessor() {
    var FILELOG: simpleString = {"initialsupervisor.log"}
    var DELAY                 = {100}
    var THRESHOLD             = {500}
    var ALERT_FILE            = {"THRESHOLD.log"}
    lateinit var WorkThread : Watcher
    val t = Memory()
    override fun r(DSL: String): Any {
        println("SUPERVISOR setting up.......")
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        LOG( "SUPERVISOR setting up.......", FILELOG())
        WorkThread = Watcher(DELAY())
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
        when (R?.Name){
            "filelog" -> mapper.put(R, filelog)
            "enabled" -> mapper.put(R, enable)
            "delay" -> mapper.put(R, delay)
            "threshold" -> mapper.put(R, threshold)
            "alertfile" -> mapper.put(R, alertfile)

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