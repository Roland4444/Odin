package DSLGuided.requestsx.SMS
import DSLGuided.requestsx.*
import abstractions.Role
import java.lang.StringBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
////////////Пример DSL для SMSDSLProcessor'a
///////////'sms'=>::login{'login'}, ::pass{'pass'},::sendto{'8958875755','89565888866'},::enabled{'false'}.  в sendto должно быть минимум два отправителя
class SMSDSLProcessor : DSLProcessor() {
    companion object {
        fun sendSMS(msg: String, DSL: String, SMSProc: SMSDSLProcessor): String{
            val f: StringHandler = SMSProc.r(DSL) as StringHandler
            return  f(msg)
        }
    }
    var login_: String =""
    var pass_: String =""
    var sendto_ = mutableListOf<String>()
    val renderfunc:StringHandler=
         {
            val sb: StringBuilder = StringBuilder()
            if (enabled=="true") {
                sendto_.forEach { a ->
                    if (a.length>3)
                    {
                        sb.append(":::SENDING to $a:::")
                        val req = """https://smsc.ru/sys/send.php?login=$login_&psw=$pass_&phones=$a&mes=${
                        it.replace(
                                " ",
                                "%20"
                            )
                        }"""
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

                }
                }
             sb.toString()
         }

    val add: DumbHandler={it+2}
    val str: StringHandler={ (it); }
    override fun r(DSL: String) :Any{
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return renderfunc
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


    override fun appendRole(R: Role){
        when (R?.Name){
            "login" -> mapper.put(R, login)
            "pass" -> mapper.put(R, pass)
            "sendto" -> mapper.put(R, sendto)
            "enabled" -> mapper.put(R, enable)
        }
    }
}