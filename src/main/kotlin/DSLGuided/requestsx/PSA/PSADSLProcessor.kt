package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import fr.roland.DB.Executor
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.util.Department
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.*
import java.time.Duration
import java.time.LocalDate
import java.util.*

typealias psaDraft = (Brutto: String, Sor: String, Metal: String, DepId:String, PlateNumber: String, UUID: String, Type: String) -> Unit
typealias completePSA = (Tara: String, Sor: String, UUID: String) -> Unit

////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'psa2'=>::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}
class PSADSLProcessor  : DSLProcessor() {
    companion object {
        fun createdraftPSA(params: HashMap<String, String>, DSL: String, PSAProc: PSADSLProcessor): Unit{
            PSAProc.render(DSL)
            val f: psaDraft = PSAProc.createdraft
            val Brutto = params.get("Brutto")
            val Sor = params.get("Sor")
            val Metal = params.get("Metal")
            val DepId = params.get("DepId")
            val PlateNumber = params.get("PlateNumber")
            val UUID = params.get("UUID")
            val Type = params.get("Type")
            f(Brutto as String, Sor as String , Metal as String , DepId as String , PlateNumber as String , UUID as String, Type as String)
        }


        fun completePSA(params: HashMap<String, String>, DSL: String, PSAProc: PSADSLProcessor): Unit{
            PSAProc.render(DSL)
            val m = PSAProc.completePSA
            val Sor = params.get("Sor")
            val Tara = params.get("Tara")
            val UUID = params.get("UUID")
            m(Tara as String, Sor as String,  UUID as String)

        }
    }

    val deps__: Department = Department()

    val DepsMap = mapOf(6 to 1, 16 to 1, 10 to 2, 9 to 25)

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
    lateinit var executor: Executor
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        executor = Executor(urldb, login, pass)
        urlPsanumberUrl += "?"+keyparam_+"="
        if (enabled == "true") {
            dbConnection = DriverManager.getConnection(urldb, login, pass)
        }
        return "OK"
    }
    val descriptionMap = mapOf("black" to "Лом и отходы черных металлов", "color" to "Лом и отходы цветных металлов")
    fun getPSANumber(DepsId : String): String{
        println("\n\n!!!!!!!!INTO getPSANumber\n\n")
        return getRequest(urlPsanumberUrl+DepsId)
    };



    var completePSA: completePSA = {Tare: String, Sor: String, UUID: String ->run {
        var prepared = dbConnection?.prepareStatement(

            """UPDATE `weighing` SET  `tare` = ?, `sor` = ?,  `client_tare` = ?, `client_sor` = ? WHERE `uuid` = ?;"""
        )
       // prepared?.setFloat(1, Final)
      //  prepared?.setFloat(4, Final)
        prepared?.setFloat(1, Tare.toFloat())
        prepared?.setFloat(3, Tare.toFloat())
        prepared?.setFloat(2, Sor.toFloat())
        prepared?.setFloat(4, Sor.toFloat())
        prepared?.setString(5, UUID)
        prepared?.execute()
    }

    }
    val initialsqldraft =    """
INSERT INTO `psa` (
`id`,`number`,`passport_id`, `company_id`,  `date`, `plate_number`, `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`) 
VALUES (
NULL,   ?,         ?,           2,             ?,       ?, 'Необходимо выбрать',      ?,              ?,       ?,CURRENT_TIMESTAMP, '0', CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?);"""


    fun processfarg(inputJSON: String){
        val parser = JSONParser()
        val js = parser.parse(inputJSON) as JSONObject
        val inputdepID = Integer.parseInt(js.get("departmentId").toString() )
        println("inputdepID => $inputdepID")
        val f = deps__.DepsMap.get(inputdepID)
        println(f)
        val realdepID = deps__.DepsMap.get(inputdepID)
        println("realdepID => $realdepID")
        val UUID: String = se.roland.abstractions.timeBasedUUID.generate()
        val vagning = js.get("weighings") as JSONArray
        if (realdepID != null) {
            createdraftfarg(realdepID, UUID)
        }
        vagning.forEach { invagning ->
            if (realdepID != null) {
                processinvagning(invagning as JSONObject, UUID)
            } }

    }


    fun createdraftfarg(depsId: Int, guuid: String) {
        println("\n\n\n\n@@@@\n\n\n\n\nINTO FARG Draft!")
        var prepared = dbConnection?.prepareStatement(               ////color/black////`created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`,
            """
INSERT INTO `psa` (
`id`,`number`,   `date`,  `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`) 
VALUES (
NULL,   ?,        ?,  'Необходимо выбрать',   ?,              ?,         ?,CURRENT_TIMESTAMP, '0', CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?);"""
        );
        val date: String = LocalDate.now().toString()
        println("date => $date")
        prepared?.setString(1, getPSANumber(depsId.toString()))
        /// getPassportId()?.let { prepared?.setInt(2, it) }
        prepared?.setDate(2, java.sql.Date.valueOf(date));

        prepared?.setString(3, depsId.toString())
        prepared?.setString(4, descriptionMap.get("color"))////LocalDate getDate
        prepared?.setString(5, "color")
        prepared?.setString(6, guuid)
        println("UUID= $guuid")
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
    }


    fun processinvagning(json: JSONObject, uuid: String){
        val prepared = dbConnection?.prepareStatement(
            """
INSERT INTO `weighing` (
`id`,`brutto`,`tare`,`sor`,`price`,`psa_id`,`metal_id`,`client_tare`,`client_sor`,`client_price`,`inspection`, `uuid`)
                                VALUES
(NULL,   ?,      ?,    ?,     ?,      ?,        ?,          ?,           ?,            ?,            ?,           ?);
                                
                """  );

        val inspect =  Random().nextFloat()/4
        prepared?.setFloat(1, json.get("brutto").toString().toFloat() )
        prepared?.setInt(2, json.get("tare").toString().toInt())
        prepared?.setFloat(3, json.get("clogging").toString().toFloat())
        prepared?.setFloat(4, (json.get("price").toString().toFloat())*1000)
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, getmetalID(json))
        prepared?.setInt(7, json.get("tare").toString().toInt())
        prepared?.setFloat(8, json.get("clogging").toString().toFloat())
        prepared?.setFloat(9, (json.get("price").toString().toFloat())*1000)
        prepared?.setString(10, (Math.round(inspect * 100.0) / 100.0).toString())
        prepared?.setString(11, uuid)

        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }

       // prepared.setString();

    }

    fun getmetalID(json: JSONObject): Int {
        val metal: JSONObject = json.get("metal") as JSONObject
        return metal.get("psaid").toString().toInt()

    }

    fun getPSAID(uuid: String): Int{
        val prepared = dbConnection?.prepareStatement("""SELECT *  FROM `psa` WHERE `uuid`= ?;"""
        )
        prepared?.setString(1, uuid)

        System.out.println(prepared)
        val rs: ResultSet? = prepared?.executeQuery()
        var PSAId = 0
        if (rs != null) {
            if (rs.next()) {
                PSAId = rs.getInt("id")
                println("ID! $PSAId")
            }
        }
        return PSAId
    }


    var createdraft: psaDraft= { Brutto, Sor, Metal, DepId, PlateNumber, UUID, Type ->
        run {
            var prepared = dbConnection?.prepareStatement(               ////color/black
                """
INSERT INTO `psa` (
`id`,`number`, `company_id`,  `date`, `plate_number`, `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`) 
VALUES (
NULL,   ?,          ?,          ?,         ?,'Необходимо выбрать',         ?,              ?,       ?,CURRENT_TIMESTAMP, '0', CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?);"""
            );
            val date: String = LocalDate.now().toString()
            println("date => $date")
            prepared?.setString(1, getPSANumber(DepId))
           /// getPassportId()?.let { prepared?.setInt(2, it) }
            prepared?.setInt(2, 2)
            prepared?.setDate(3, java.sql.Date.valueOf(date));
            prepared?.setString(4, PlateNumber)
            prepared?.setString(5, DepId)
            prepared?.setString(6, descriptionMap.get(Type))////LocalDate getDate
            prepared?.setString(7, Type)
            prepared?.setString(8, UUID)
            println("UUID= $UUID")
            println("prepared=> $prepared")
            if (prepared != null) {
                prepared.execute()
            }
            prepared = dbConnection?.prepareStatement("""SELECT *  FROM `psa` WHERE `date` = ? AND `plate_number` LIKE ? 
                        AND `department_id` = ? AND `comment`='fromScales' AND `uuid`= ?;"""
            )
            prepared?.setDate(1, java.sql.Date.valueOf(date))
            prepared?.setString(2, PlateNumber)
            prepared?.setString(3, DepId)
            prepared?.setString(4, UUID)
            System.out.println(prepared)
            val rs: ResultSet? = prepared?.executeQuery()
            var PSAId = 0
            if (rs != null) {
                if (rs.next())
                    PSAId = rs.getInt(1)
            }

            prepared = dbConnection?.prepareStatement(
"""INSERT INTO `weighing` 
(`id`, `brutto`,  `sor`, `tare`,`price`, `psa_id`, `metal_id`,  `client_price`, `inspection`, `uuid`) 
VALUES 
(NULL,     ?,       ?,    0.0,   0.0,          ?,        ?,             0.0,           ?,            ?);""")

            prepared?.setInt(1,Brutto.toInt())
            prepared?.setFloat(2, Sor.toFloat())
            prepared?.setInt(3, PSAId)
            val rnd = Random()
            var inspect =  Random().nextFloat()/4
            prepared?.setInt(4, getMetalId(Metal))
            val m = getMetalId(Metal)
            println("METALID==>$m")
            prepared?.setString(5, (Math.round(inspect * 100.0) / 100.0).toString())
            prepared?.setString(6, UUID)
            println(prepared)
            if (PSAId == 0)
                println("Wrong psaId")
            prepared?.execute()
        }
    }

    fun getMetalId(metal: String?): Int {
        var prepared =dbConnection?.prepareStatement("select * from `psa`.`metal` where title=?;")
        prepared?.setString(1, metal)
        val rs = prepared?.executeQuery()
        println("\n\n\n\nINTO getmetalid $prepared")
        if (rs?.next() == true)
            return rs.getInt("id")
        return -1;
    }


    var createdraft___:psaDraft=
    {///,`client`                  'Необходимо выбрать',
            Brutto, Sor, Metal, DepId, PlateNumber, UUID, Type -> run{
        val prepared = dbConnection?.prepareStatement(               ////color/black
                        """
INSERT INTO `psa`(
`id`,`number`,`date`,`plate_number`,`department_id`,`description`,  `type`,     `created_at`,    `diamond`,`payment_date` ,`check_printed`,`deferred`,`filename`,`uuid`) 
VALUES (
NULL,    ? ,    ?,       ?,                ?,           ?,            ?,      CURRENT_TIMESTAMP,      '0', CURRENT_TIMESTAMP,      '0',        '0',        NULL,    ?);""");
        val date: String = LocalDate.now().toString()
        println("date => $date")
        prepared?.setString(1, getPSANumber(DepId))
        prepared?.setDate  (2, java.sql.Date.valueOf(date));
        prepared?.setString(3, PlateNumber)
        prepared?.setString(4, DepId)
        prepared?.setString(5, descriptionMap.get(Type))////LocalDate getDate
        prepared?.setString(6, Type)
        prepared?.setString(7, UUID)
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
            "enabled" -> mapper.put(R, enable)
        }
    }

    @Throws(IOException::class)
    fun getRequest(url: String?):String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(20))
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