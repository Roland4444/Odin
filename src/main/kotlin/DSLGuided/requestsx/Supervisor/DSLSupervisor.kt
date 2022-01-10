package DSLGuided.requestsx.Supervisor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.Sber.simpleString
import abstractions.Role
import se.roland.abstractions.Call
import se.roland.util.GLOBAL.LOG
import se.roland.util.Memory
import se.roland.util.Watcher
import java.io.FileOutputStream

class DSLSupervisor: DSLProcessor() {
    var FILELOG: simpleString = {""}
    var DELAY                 = {1000}
    lateinit var WorkThread : Watcher
    val t = Memory()
    override fun r(DSL: String): Any {
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        WorkThread = Watcher(DELAY())
        WorkThread.callback = object : Call {
            override fun doIt() {
                    LOG( ("FREE MEM::${t.getfreeMem() / 1024}\n").toString(), FILELOG())
                    println("FREE MEM:" + t.getfreeMem() / 1024)
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