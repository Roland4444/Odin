package DSLGuided.requestsx.SMS
import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.sendSMS
import abstractions.Role
import java.lang.StringBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
class SMSDSLProcessor : DSLProcessor() {
    var enabled: Boolean = false
    var login_: String =""
    var pass_: String =""
    var sendto_ = mutableListOf<String>()
    fun emptyfunc()={
        println("NOTHING TO DO")
    }

    val renderfunc:sendSMS=
         {
            if (!enabled)
                 ""
            val sb: StringBuilder=StringBuilder()
            sendto_.forEach {a->
                val req = """https://smsc.ru/sys/send.php?login=$login&psw=$pass&phones=$a&mes=${it.replace(" ", "%20")}"""
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(req))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build()
                val client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
                if (response.statusCode() == 200)
                    sb.append("$it=>${String(response.body())}")
                else
                    sb.append("$it=>shit happens")
            }
            sb.toString()

    }

    override fun render(DSL: String) :Any{
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        if (enabled)        return renderfunc
        return  emptyfunc()

    }

    val login: RoleHandler = {
        mapper.forEach {
            a->
                if (a.key.Name=="login")
                    login_= a.key.Param as String
        }
    }


    val pass: RoleHandler ={
        mapper.forEach {
                a->
            if (a.key.Name=="pass")
                pass_= a.key.Param as String
        }
    }

    val sendto: RoleHandler = {
        mapper.forEach {
                a->
            if (a.key.Name=="sendto"){
                sendto_= a.key.Param as MutableList<String>
            }
        }
    }



    val enable: RoleHandler = {
        mapper.forEach {
                a->
            if (a.key.Name=="enable")
                enabled= a.key.Param as Boolean
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
        print("Adding role ${R.Name}\n")
        when (R?.Name){
            "login" -> mapper.put(R, login)
            "pass" -> mapper.put(R, pass)
            "sendto" -> mapper.put(R, sendto)
            "enabled" -> mapper.put(R, enable)
        }
    }
}