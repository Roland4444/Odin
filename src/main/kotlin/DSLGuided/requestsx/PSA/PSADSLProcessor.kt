package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.util.Department
import java.sql.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

typealias psaDraftSection = (Brutto: String, Sor: String, Metal: String, DepId:String, PlateNumber: String, UUID: String, Type: String, Section: String) -> Unit
typealias psaDraft = (Brutto: String, Sor: String, Metal: String, DepId:String, PlateNumber: String, UUID: String, Type: String) -> Unit
typealias completePSA = (Tara: String, Sor: String, UUID: String) -> Unit

////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'psa2'=>::psaIDtoSEhooK{'true','3':'1'},::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}
/////psaId => metal in PSA   , table metal, db PSA

class PSADSLProcessor  : DSLProcessor() {
    companion object {
        fun processColorPSA(inputJSON: String, uuid: String,DSL: String,PSAProc: PSADSLProcessor ){
            PSAProc.render(DSL)
            PSAProc.processfarg(uuid, inputJSON)
        }

        fun createdraftPSA(params: HashMap<String, String>, DSL: String, PSAProc: PSADSLProcessor): Unit{
            println("into create draft psa")
            PSAProc.render(DSL)
            val f: psaDraftSection = PSAProc.createdraftsection
            val Brutto = params.get("Brutto")
            val Sor = params.get("Sor")
            val Metal = params.get("Metal")
            val DepId = params.get("DepId")
            val PlateNumber = params.get("PlateNumber")
            val UUID = params.get("UUID")
            val Type = params.get("Type")
            val Section = params.get("Section")
            f(Brutto as String, Sor as String , Metal as String , DepId as String , PlateNumber as String , UUID as String, Type as String, Section as String)
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
    val jsparser = JSONParser()
    var comment: String = ""
    val NONE = "NONE"


    val deps__: Department = Department()

    val DepsMap = mapOf(6 to 1, 16 to 1, 10 to 2, 9 to 25)

    val psaSql =                         """
INSERT INTO `psa`(
`id`,`number`,`passport_id`,`date`,`plate_number`,`client`,`department_id`,`description`,             `type`,     `created_at`,    `diamond`,`payment_date`, `comment`,`check_printed`,`deferred`,`filename`,`uuid`) 
VALUES (
NULL,    ?         , ?,           ?,       ?,    'Не выбран', ?, 'Лом и отходы черных металлов', 'black',CURRENT_TIMESTAMP,      '0', CURRENT_TIMESTAMP,    ?,           '0',        '0',        NULL,    ?);"""
                                            //////Необходимо выбрать

    var login: String=""
    var pass: String=""
    var urldb: String =""
  /////  var urlPsanumberUrl: String =""   DEPRECATED!
 ////   var keyparam_: String =""  DEPRECATED!
    var dumb: String = ""
    var json_ = ""
    var HOOKUUID = ""
    var HOOKSECTION = ""
    var HOOKED = FALSE_ATOM

    var PSAID = ""
    var SECTION = ""
    var PSAIDHOOK = FALSE_ATOM

    var external_searchdsl =""
    lateinit var psearch: PSASearchProcessor
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        clearhooked()
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
    //    executor = Executor(urldb, login, pass)
    /////    urlPsanumberUrl += "?"+keyparam_+"="
        if (enabled == "true") {
        ///        dbConnection = DriverManager.getConnection(urldb, login, pass)
        //    dbConnection= executor.conn
        }

        return "OK"
    }

    val BLACK_ATOM = "black"
    val COLOR_ATOM = "color"
    val descriptionMap = mapOf(BLACK_ATOM to "Лом и отходы черных металлов", COLOR_ATOM to "Лом и отходы цветных металлов")



    fun getPSANumberviaDSL(DepsId: String): String{
        println("in DSL psa getnumber")
        val name = psearch.getdepNameExecutor(DepsId)
        val year = Calendar.getInstance()[Calendar.YEAR]
        val date: String = LocalDate.now().toString()
        println("date => $date")
        val buildSearchDSL = "'search'=>::sql{'SELECT * FROM psa '},::department{'${name}',''},::datarange{'${year}-01-01':'${java.sql.Date.valueOf(date)}'}."
        println("PREPARED DSL=> $buildSearchDSL")
        psearch.render(buildSearchDSL)
        val res = psearch.getPSA()
        var numberpsa = 0;
        while (res?.next() == true) {
            numberpsa = res.getInt("number")
        }
        numberpsa++
        return numberpsa.toString()
    }

    fun getPSANumberviaDSL(DepsId: String, Section: String): String{
        println("in DSL psa getnumber")
        println("DEP_ID::$DepsId, SECTION::$Section")
        val name = psearch.getdepNameExecutor(DepsId)
        val year = Calendar.getInstance()[Calendar.YEAR]
        val date: String = LocalDate.now().toString()
        println("date => $date")
        val buildSearchDSL = "'search'=>::sql{'SELECT * FROM psa '},::section{'${Section}'},::department{'${name}',''},::datarange{'${year}-01-01':'${java.sql.Date.valueOf(date)}'}."
        println("PREPARED DSL for search=> $buildSearchDSL")
        psearch.render(buildSearchDSL)
        external_searchdsl = buildSearchDSL
        val res = psearch.getPSA()
        var numberpsa = 0;
        while (res?.next() == true) {
            numberpsa = res.getInt("number")
            println("number PSA at currentRow::$numberpsa")
        }
        numberpsa++
        println("PSA NUMBER==>$numberpsa")
        return numberpsa.toString()
    }
    //
    fun updateDescriptionToBlack(UUID: String){
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """UPDATE `psa`  SET   `description`=?, `type`=? WHERE `uuid` = ?;""")
        prepared?.setString(1, descriptionMap.get(BLACK_ATOM))////LocalDate getDate
        prepared?.setString(2, BLACK_ATOM)
        prepared?.setString(3, UUID)
        println("UPDATING SECTION SET TYPE=$BLACK_ATOM WHERE PSA UUID=$UUID")
        prepared?.execute()
    }

    fun updateSection(Section :String, UUID: String){

        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """UPDATE `psa` SET  `section` = ? WHERE `uuid` = ?;""")

        prepared?.setString(1, Section)
        prepared?.setString(2, UUID)
        println("UPDATING SECTION SET SECTION=$Section WHERE PSA UUID=$UUID")
        prepared?.execute()
    }


    var completePSA: completePSA = {Tare: String, Sor: String, UUID: String ->run {
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(

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
NULL,   ?,         ?,           2,             ?,         ?, 'Не выбран',      ?,              ?,       ?,CURRENT_TIMESTAMP, '0', CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?);"""

                                             ////Необходимо выбрать

    fun checkpsaexist(uuid: String): Boolean{
        var param = ArrayList<Any>()
        param.add(uuid)
        var prepared = psearch.psaconnector.executor!!.executePreparedSelect(" SELECT * from `psa` where  `uuid` = ?;", param)
        return (prepared.next())
    }

    fun clearweignings(uuid : String){
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(      "DELETE FROM `weighing` WHERE `uuid`=?;");
        prepared?.setString(1, uuid);
        prepared?.executeUpdate();
    }

    fun splitpsa(uuid : String){
        var param = ArrayList<Any>()
        param.add(uuid)
        println("uuid = $uuid")
        val datainvagning = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `weighing` WHERE `uuid`=?;", param)
        val datainvagning_ = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `weighing` WHERE `uuid`=?;", param)

        updateDatainvagning(datainvagning, uuid)
        val datapsa = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa` WHERE `uuid`=?;", param)
        createPSA(datapsa, "${uuid}_")
        createinvagning(datainvagning_, "${uuid}_")
    }

    fun createinvagning(datainvagning: ResultSet, uuid: String) {
        val prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """
INSERT INTO `weighing` (
`id`,`brutto`,`tare`,`sor`,`price`,`psa_id`,`metal_id`,`client_brutto`,`client_tare`,`client_sor`,`client_price`,`inspection`, `uuid`)
                                VALUES
(NULL,   ?,      ?,    ?,     ?,      ?,        ?,          ?,               ?,           ?,            ?,            ?,         ?);
                                
                """  );
        val inspect =  Random().nextFloat()/4
        if (!datainvagning.next()){
            println("empty datainvagning")
            return
        }
        val brutto =  datainvagning.getFloat("brutto")/2 + datainvagning.getFloat("tare") / 2
        prepared?.setFloat(1,brutto ) //Brutto)
        prepared?.setFloat(2, datainvagning.getFloat("tare"))////json.get("tare").toString().toFloat())
        prepared?.setFloat(3,  datainvagning.getFloat("sor"))///  json.get("clogging").toString().toFloat())
        prepared?.setFloat(4, datainvagning.getFloat("price"))
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, datainvagning.getInt("metal_id"))
        prepared?.setFloat(7, brutto)//json.get("brutto").toString().toFloat() )
        prepared?.setFloat(8, datainvagning.getFloat("client_tare"))///json.get("tare").toString().toFloat())
        prepared?.setFloat(9, datainvagning.getFloat("client_sor"))////json.get("clogging").toString().toFloat())
        prepared?.setFloat(10, datainvagning.getFloat("client_price"))
        prepared?.setString(11, (Math.round(inspect * 100.0) / 100.0).toString())
        prepared?.setString(12, uuid)
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
    }

    fun createPSA(data: ResultSet, uuid: String) {
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(               ////color/black////`created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`,
            """
INSERT INTO `psa` (
`id`,`number`,   `date`, `plate_number`, `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`) 
VALUES (
NULL,   ?,          ?,       ?,              ?,           ?,             ?,           ?,        ?   ,       '0',         ?,        'fromScales',   '0',          '0',       NULL,       ?);"""
        );                     ////Необходимо выбрать
        if (!data.next()) {
            println("empty data set in createPSA")
            return;
        }
        val date: String = LocalDate.now().toString()
        println("date => $date")
        prepared?.setString(1, getPSANumberviaDSL(data.getString("department_id")))//getPSANumber(depsId.toString()))
        /// getPassportId()?.let { prepared?.setInt(2, it) }
        prepared?.setDate(2, java.sql.Date.valueOf(date));
        prepared?.setString(3, data.getString("plate_number"));

        prepared?.setString(4, data.getString("client"));

        prepared?.setInt(5, data.getString("department_id").toInt())
        prepared?.setString(6, descriptionMap.get(data.getString("type")))////LocalDate getDate
        prepared?.setString(7, data.getString("type"))
        prepared?.setString(8, data.getString("created_at"))
        prepared?.setString(9, data.getString("payment_date"))
        prepared?.setString(10, uuid)
        println("prepared @createPSA=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
    }

    fun updateDatainvagning(data: ResultSet, uuid: String) {
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """UPDATE `weighing` SET  `brutto` = ?, `client_brutto` = ? WHERE `uuid` = ?;"""
        )
        if (!data.next())
            return;
        val brutto =  data.getFloat("brutto")/2 + data.getFloat("tare") / 2
        println("Brutto => $brutto")
        prepared?.setFloat(1, brutto)
        prepared?.setFloat(2, brutto)
        prepared?.setString(3, uuid)

        println("prepared @updateDatainvagning=> $prepared")
        prepared?.execute()
    }

    fun processfarg(uuid_: String, inputJSON: String){
        var uuid = uuid_
        if (HOOKED.equals(TRUE_ATOM))
            if (HOOKUUID.length > 0)
                uuid = HOOKUUID
        println("inputJSON=> $inputJSON, uuid $uuid")
        clearweignings(uuid)
        val js = jsparser.parse(inputJSON) as JSONObject
        comment = js.get("comment") as String
        println("comment:: $comment")
        val inputdepID = Integer.parseInt(js.get("departmentId").toString() )
        val f = deps__.DepsMap.get(inputdepID)
        val realdepID = deps__.DepsMap.get(inputdepID)
        val sum = extractSummary(inputJSON)
        println("SUMM: $sum")
        val vagning = convertToListJSON(sum)
        var section = NONE
        if (js.get("section")!= null)
            section = js.get("section") as String
        if (HOOKED.equals(TRUE_ATOM))
            if (HOOKSECTION.length > 0)
                section = HOOKSECTION
        println("VAGNING: ${vagning}")
        println("\n\nSECTION::$section\n\n")
        val checkpsa = checkpsaexist(uuid)
        if ((realdepID != null) &&  !checkpsa) {
            println("creating draft @$realdepID")
            createdraftfarg(realdepID, uuid, section)
        }
        vagning.forEach { invagning ->
            if (realdepID != null) {
                println("process vagning  @JSON::${invagning.toString()}")
                processinvagning__(invagning as JSONObject, uuid)///processinvagning(invagning as JSONObject, uuid)
            }
        }


    }

    fun createdraftfarg(depsId: Int, guuid: String, section: String) {
        println("\n\n\n\n@@@@\n\n\n\n\nINTO FARG Draft!")

        var initial = """
INSERT INTO `psa` (
`id`,`number`,   `date`,  `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`, `section`) 
VALUES (
NULL,   ?,       ?,  'Не выбран ($comment)',   ?,           ?,       ?,   CURRENT_TIMESTAMP, '0', CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',       NULL,      ?,     ?);"""
                         ////Необходимо выбрать
        var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(initial);
        val date: String = LocalDate.now().toString()
        println("date => $date")
        prepared?.setString(1, getPSANumberviaDSL(depsId.toString(), section))//getPSANumber(DepId))
        /// getPassportId()?.let { prepared?.setInt(2, it) }
        ///  prepared?.setInt(2, 2)
        prepared?.setDate(2, java.sql.Date.valueOf(date));
        prepared?.setInt(3, depsId.toString().toInt())
        prepared?.setString(4, descriptionMap.get("color")!!)
        prepared?.setString(5, "color")////LocalDate getDate
        prepared?.setString(6, guuid)
        prepared?.setString(7, section)

        println("UUID= $guuid")
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
    }

    fun processinvagning__(json: JSONObject, uuid: String){
        val prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """
INSERT INTO `weighing` (
`id`,`brutto`,`tare`,`sor`,`price`,`psa_id`,`metal_id`,`client_brutto`,`client_tare`,`client_sor`,`client_price`,`inspection`, `uuid`)
                                VALUES
(NULL,   ?,      ?,    ?,     ?,      ?,        ?,          ?,               ?,           ?,            ?,            ?,         ?);
                                
                """  );
        ///   "cost":4736.16,"median":52,"weight":91.08,"psaid":12

        val inspect =  Random().nextFloat()/4
        prepared?.setFloat(1, json.get("weight").toString().toFloat() ) //Brutto)
        prepared?.setFloat(2, 0.0f)////json.get("tare").toString().toFloat())
        prepared?.setFloat(3,  0.0f)///  json.get("clogging").toString().toFloat())
        prepared?.setFloat(4, json.get("median").toString().toFloat()*1000)
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, json.get("psaid").toString().toInt())
        prepared?.setFloat(7, json.get("weight").toString().toFloat())//json.get("brutto").toString().toFloat() )
        prepared?.setFloat(8, 0.0f)///json.get("tare").toString().toFloat())
        prepared?.setFloat(9, 0.0f)////json.get("clogging").toString().toFloat())
        prepared?.setFloat(10, json.get("median").toString().toFloat()*1000)
        prepared?.setString(11, (Math.round(inspect * 100.0) / 100.0).toString())
        prepared?.setString(12, uuid)
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }
        when (PSAIDHOOK){
            TRUE_ATOM->{
                if (json.get("psaid").toString().equals(PSAID)) {
                    println("HOOK SECTION SET TO $SECTION @ PSAID=$PSAID")
                    updateSection(SECTION, uuid)
                    updateDescriptionToBlack(uuid)
                }
            }
        }
        // prepared.setString();
    }


    fun processinvagning(json: JSONObject, uuid: String){
        val prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
            """
INSERT INTO `weighing` (
`id`,`brutto`,`tare`,`sor`,`price`,`psa_id`,`metal_id`,`client_brutto`,`client_tare`,`client_sor`,`client_price`,`inspection`, `uuid`)
                                VALUES
(NULL,   ?,      ?,    ?,     ?,      ?,        ?,          ?,               ?,           ?,            ?,            ?,         ?);
                                
                """  );
     ///   "cost":4736.16,"median":52,"weight":91.08,"psaid":12
        val Bruttoinput: Float = json.get("brutto").toString().toFloat()
        val CloggingInput : Float = json.get("clogging").toString().toFloat()
        val Tare: Float = json.get("tare").toString().toFloat()
        val Trash = json.get("trash").toString().toFloat()
        val sub: Float = Bruttoinput - Tare - Trash
        val percentage: Float = ((CloggingInput / 100.00 * sub).toFloat())
        val Brutto = (100-CloggingInput)/100*sub
        println("Calculated Brutto=> $Brutto")
        val inspect =  Random().nextFloat()/4
        prepared?.setFloat(1, Brutto)/////json.get("brutto").toString().toFloat() ) //Brutto)
        prepared?.setFloat(2, 0.0f)////json.get("tare").toString().toFloat())
        prepared?.setFloat(3,  0.0f)///  json.get("clogging").toString().toFloat())
        prepared?.setFloat(4, (json.get("price").toString().toFloat())*1000)
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, getmetalID(json))
        prepared?.setFloat(7, Brutto)//json.get("brutto").toString().toFloat() )
        prepared?.setFloat(8, 0.0f)///json.get("tare").toString().toFloat())
        prepared?.setFloat(9, 0.0f)////json.get("clogging").toString().toFloat())
        prepared?.setFloat(10, (json.get("price").toString().toFloat())*1000)
        prepared?.setString(11, (Math.round(inspect * 100.0) / 100.0).toString())
        prepared?.setString(12, uuid)
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

    fun extractSummary(input: String): String{
        println("extract in summary $input")
        val jsObj: JSONObject = jsparser.parse(input) as JSONObject
        return jsObj.get("summary").toString()
    }

    fun convertToList(input: String): String{
        println("INTO CONVERT TO LIST")
        val startelem = ":{";
        val finishelem = "},\""
        var builder = StringBuilder()
        builder.append("[")
        var str = input
        var index = str.indexOf(startelem)
        while (index>=0){
            val nextindex = str.indexOf(finishelem)
            if (nextindex >=0) {
                println("nextindex $nextindex")
                println("append ${str.substring(index+1, nextindex+2)}")
                builder.append(str.substring(index + 1, nextindex + 2))
            }
            else
            {
                builder.append(str.substring(index+1))
                break
            }
            str = str.substring(nextindex+2)
            println("STR=> $str")
            index = str.indexOf(startelem)
            println("index $index")
        }
        builder.append("]")
        return builder.toString().replace("}}]", "}]")
    }

    fun convertToListJSON(input: String): JSONArray{
        println("input:: $input")
        return jsparser.parse(convertToList(input)) as JSONArray
    }

    fun getPSAID(uuid: String): Int{
        val prepared = psearch.psaconnector.executor!!.conn.prepareStatement("""SELECT *  FROM `psa` WHERE `uuid`= ?;""")
        prepared?.setString(1, uuid)
        println(prepared)
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
            var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(               ////color/black
                """
INSERT INTO `psa` (
`id`,`number`,`date`, `plate_number`, `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`) 
VALUES (
NULL,   ?,      ?,         ?,'Не выбран ($PlateNumber)',?,              ?,         ?,  CURRENT_TIMESTAMP, '0',   CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?);"""
            );                              /////Необходимо выбрать
            val date: String = LocalDate.now().toString()
            prepared?.setString(1, getPSANumberviaDSL(DepId))//getPSANumber(DepId))
            /// getPassportId()?.let { prepared?.setInt(2, it) }
            ///  prepared?.setInt(2, 2)
            prepared?.setDate(2, java.sql.Date.valueOf(date));
            prepared?.setString(3, PlateNumber)
            prepared?.setString(4, DepId)
            prepared?.setString(5, descriptionMap.get(Type))////LocalDate getDate
            prepared?.setString(6, Type)
            prepared?.setString(7, UUID)
            println("prepared=> $prepared")
            if (prepared != null) {
                prepared.execute()
            }
            prepared = psearch.psaconnector.executor!!.conn.prepareStatement("""SELECT *  FROM `psa` WHERE `date` = ? AND `plate_number` LIKE ? 
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

            prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
                """INSERT INTO `weighing` 
(`id`, `brutto`,  `sor`, `tare`,`price`, `psa_id`, `metal_id`,  `client_price`, `inspection`, `uuid`) 
VALUES 
(NULL,     ?,       ?,    0.0,   0.0,          ?,        ?,             0.0,           ?,            ?);""")

            prepared?.setInt(1,Brutto.toInt())
            prepared?.setFloat(2, Sor.toFloat())
            prepared?.setInt(3, PSAId)
            var inspect =  Random().nextFloat()/4
            prepared?.setInt(4, getMetalId(Metal))
            val m = getMetalId(Metal)
            prepared?.setString(5, (Math.round(inspect * 100.0) / 100.0).toString())
            prepared?.setString(6, UUID)
            println(prepared)
            if (PSAId == 0)
                println("Wrong psaId")
            prepared?.execute()
        }
    }


    var createdraftsection: psaDraftSection= { Brutto, Sor, Metal, DepId, PlateNumber, UUID, Type, Section ->
        run {
            var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(               ////color/black
                """
INSERT INTO `psa` (
`id`,`number`,`date`, `plate_number`, `client`, `department_id`, `description`, `type`, `created_at`, `diamond`, `payment_date`, `comment`, `check_printed`, `deferred`,`filename`, `uuid`, `section`) 
VALUES (
NULL,   ?,      ?,         ?,'Не выбран ($PlateNumber)',?,              ?,         ?,  CURRENT_TIMESTAMP, '0',   CURRENT_TIMESTAMP, 'fromScales',     '0',          '0',    NULL,         ?,     ?);"""
            );                              /////Необходимо выбрать
            val date: String = LocalDate.now().toString()
            prepared?.setString(1, getPSANumberviaDSL(DepId, Section))//getPSANumber(DepId))
           /// getPassportId()?.let { prepared?.setInt(2, it) }
          ///  prepared?.setInt(2, 2)
            prepared?.setDate(2, java.sql.Date.valueOf(date));
            prepared?.setString(3, PlateNumber)
            prepared?.setString(4, DepId)
            prepared?.setString(5, descriptionMap.get(Type))////LocalDate getDate
            prepared?.setString(6, Type)
            prepared?.setString(7, UUID)
            prepared?.setString(8, Section)
            println("prepared=> $prepared")
            println("\n\nSECTION::$Section\n\n")
            if (prepared != null) {
                prepared.execute()
            }
            prepared = psearch.psaconnector.executor!!.conn.prepareStatement("""SELECT *  FROM `psa` WHERE `date` = ? AND `plate_number` LIKE ? 
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

            prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
"""INSERT INTO `weighing` 
(`id`, `brutto`,  `sor`, `tare`,`price`, `psa_id`, `metal_id`,  `client_price`, `inspection`, `uuid`) 
VALUES 
(NULL,     ?,       ?,    0.0,   0.0,          ?,        ?,             0.0,           ?,            ?);""")

            prepared?.setInt(1,Brutto.toInt())
            prepared?.setFloat(2, Sor.toFloat())
            prepared?.setInt(3, PSAId)
            var inspect =  Random().nextFloat()/4
            prepared?.setInt(4, getMetalId(Metal))
            val m = getMetalId(Metal)
            prepared?.setString(5, (Math.round(inspect * 100.0) / 100.0).toString())
            prepared?.setString(6, UUID)
            println(prepared)
            if (PSAId == 0)
                println("Wrong psaId")
            prepared?.execute()
        }
    }

    fun getMetalId(metal: String?): Int {
        var prepared =psearch.psaconnector.executor!!.conn.prepareStatement("select * from `psa`.`metal` where title=?;")
        prepared?.setString(1, metal)
        val rs = prepared?.executeQuery()
        if (rs?.next() == true)
            return rs.getInt("id")
        return -1;
    }

    fun processPSASection(input:MutableList<Any>){
        println("into PSA section::")
        input.forEach{
            val f: KeyValue = it as KeyValue
            when (it.Key){
                "login" -> login = it.Value as String;
                "pass"  -> pass  = it.Value as String;
            }
        }
    }

    fun clearhooked(){
        HOOKSECTION = ""
        HOOKUUID = ""
        HOOKED=FALSE_ATOM
    }

    val HOOK: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "HOOK"){
                clearhooked()
                var Arr = a.key.Param as MutableList<Any>
                Arr.forEach { a ->
                    when (a) {
                        is KeyValue -> {
                            if (a.Key.equals("section")) {
                                HOOKSECTION = a.Value as String
                                println("HOOK SECTION to=>$HOOKSECTION")
                            }
                            if (a.Key.equals("uuid")) {
                                HOOKUUID = a.Value as String
                                println("HOOK UUID to=>$HOOKUUID")
                            }
                            };
                        is String -> HOOKED = a;
                    }
                }
            }
        }
    }

    val stupid: RoleHandler = {
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

    val psa: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psa")
                processPSASection(a.key.Param as MutableList<Any>)
        }
    }

    val psaIDtoSEhooK: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "psaIDtoSEhooK") {
                PSAID=EMPTY_ATOM
                SECTION=EMPTY_ATOM
                PSAIDHOOK=FALSE_ATOM
                var Arr = a.key.Param as MutableList<Any>
                Arr.forEach { a ->
                    when (a) {
                        is KeyValue -> {
                            PSAID = a.Key
                            SECTION = a.Value as String
                        }
                        is String -> {
                            println("A::$a")
                            PSAIDHOOK = a
                        }
                    }
                }
            }
        }
    }
    val json: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "json")
                json_ = (a.key.Param as String)
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
            "json" -> mapper.put(R, json)
            "HOOK" -> mapper.put(R, HOOK)
            "psaIDtoSEhooK" -> mapper.put(R, psaIDtoSEhooK)
        }
    }

}