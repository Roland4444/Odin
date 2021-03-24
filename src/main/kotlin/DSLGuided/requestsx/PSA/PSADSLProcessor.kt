package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.time.Duration
import java.time.LocalDate

typealias psaDraft = (Brutto: Float, DepId:String, PlateNumber: String, GUID: String, Type: String) -> Unit
////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass,                                  db PSA                                                     URL service (get request)
///////'psa2'=>::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id}
class PSADSLProcessor  : DSLProcessor() {
    val psaSql =                         """
INSERT INTO `psa`(
`id`,`number`,`passport_id`,`date`,`plate_number`,`client`,`department_id`,`description`,             `type`,     `created_at`,    `diamond`,`payment_date`, `comment`,`check_printed`,`deferred`,`filename`,`uuid`) 
VALUES (
NULL,    ?         , ?,           ?,       ?,    'Необходимо выбрать', ?, 'Лом и отходы черных металлов', 'black',CURRENT_TIMESTAMP,      '0', CURRENT_TIMESTAMP,    ?,           '0',        '0',        NULL,    ?);"""
    var login: String=""
    var pass: String=""
    var urldb: String =""
    var urlPsanumberUrl: String =""
    var keyparam_: String =""
    var dumb: String = ""
    var dbConnection: Connection? = null
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        urlPsanumberUrl += "?"+keyparam_+"="
        dbConnection = DriverManager.getConnection(urldb, login, pass)
        return "OK"
    }
    val descriptionMap = mapOf("black" to "Лом и отходы черных металлов", "color" to "Лом и отходы цветных металлов")
    fun getPSANumver(DepsId : String): String{
        return getRequest(urlPsanumberUrl+DepsId)
    };

    var createdraft:psaDraft=
    {
        Brutto, DepId, PlateNumber, GUID, Type -> run{
        val prepared = dbConnection?.prepareStatement(               ////color/black
                        """
INSERT INTO `psa`(
`id`,`number`,`date`,`plate_number`,`client`,`department_id`,`description`,  `type`,     `created_at`,    `diamond`,`payment_date` ,`check_printed`,`deferred`,`filename`,`uuid`) 
VALUES (
NULL,    ? ,    ?,       ?,    'Необходимо выбрать', ?,           '?',         ?,      CURRENT_TIMESTAMP,      '0', CURRENT_TIMESTAMP,      '0',        '0',        NULL,    ?);""");
        prepared?.setString(1, getPSANumver(DepId))
        prepared?.setDate  (2, Date.valueOf(LocalDate.now() as String))
        prepared?.setString(3, PlateNumber)
        prepared?.setString(4, DepId)
        prepared?.setString(5, descriptionMap.get(Type))
        prepared?.setString(6, Type)
        prepared?.setString(6, GUID)
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
    }
    }

    fun processPSASection(input:MutableList<Any>){
        println("into PSA section::")
        input.forEach{
            val f: KeyValue = it as KeyValue
            println("""KEY VALUE ${f.Key}::${f.Value}""")
            when ((it as KeyValue).Key){
                "login" -> login = it.Value as String;
                "pass"  -> pass  = it.Value as String;
            }
        }
    }
    val stupid: RoleHandler = {
        println("\n\n\nINTO DUMB\n\n\n")
        mapper.forEach { a ->
            if (a.key.Name == "stupid")
                dumb = a.key.Param as String
        }
    }

    val db: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "db")
                urldb = a.key.Param as String
        }
    }
    val keyparam: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "keyparam")
                keyparam_ = a.key.Param as String
        }
    }

    val psa: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psa")
                processPSASection(a.key.Param as MutableList<Any>)
        }
    }
    val getPsaNumberfrom: RoleHandler = {
        println("\n\n\n\n<<<<<<<>>>>>>>INTO getPsaNumberfrom")
        mapper.forEach { a ->
            if (a.key.Name == "getPsaNumberfrom")
                urlPsanumberUrl = a.key.Param as String
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
            "psa" -> mapper.put(R, psa)
            "getPsaNumberfrom" -> mapper.put(R, getPsaNumberfrom)
            "db" -> mapper.put(R, db)
            "keyparam" -> mapper.put(R, keyparam)
        }
    }

    @Throws(IOException::class)
    fun getRequest(url: String?):String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(2))
            .GET()
            .build()
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(20))
            .build()
        val response: HttpResponse<String>
        return try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body().toString()
        } catch (e: InterruptedException) {
            "-1"
        }
    }
}