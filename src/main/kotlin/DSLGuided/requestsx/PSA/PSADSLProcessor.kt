package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import se.roland.util.Checker.checkdigit
import se.roland.util.Department
import se.roland.util.HTTPClient
import se.roland.util.HTTPClient.sendPost
import java.io.IOException
import java.sql.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

typealias psaDraftSection = (Brutto: String, Sor: String, Metal: String, DepId:String, PlateNumber: String, UUID: String, Type: String, Section: String) -> Unit
typealias psaDraft = (Brutto: String, Sor: String, Metal: String, DepId:String, PlateNumber: String, UUID: String, Type: String) -> Unit
typealias completePSA = (Tara: String, Sor: String, UUID: String) -> Unit
typealias completePSAwithPrice = (Tara: String, Sor: String, UUID: String, Price: String, ClientPrice: String) -> Unit

////////////Пример DSL для PSADSLProcessor'a
///////////      login, pass,                                  db PSA                                           URL service (get request)          название параметра для url service получения номера ПСА
//                                                                                                                                                                                  подключаться к БД
///////'psa2'=>::passcheck{true},::passcheckurl{https://passport.avs.com.ru/},::activatePSA{true},::urltoActivate{http://192.168.0.126:15000/psa/psa/gettest},::psaIDtoSEhooK{'true','3':'1'},::psa{'login':user123,'pass':password},::db{jdbc:mysql://192.168.0.121:3306/psa},::getPsaNumberfrom{http://192.168.0.121:8080/psa/psa/num},::keyparam{department_id},::enabled{'true'}
/////psaId => metal in PSA   , table metal, db PSA

class PSADSLProcessor  : DSLProcessor() {
    companion object {
        fun processColorPSA(inputJSON: String, uuid: String, DSL: String, PSAProc: PSADSLProcessor) {
            PSAProc.render(DSL)
            PSAProc.processfarg(uuid, inputJSON)
        }

        fun createdraftPSA(params: HashMap<String, String>, DSL: String, PSAProc: PSADSLProcessor): Unit {
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
            f(
                Brutto as String,
                Sor as String,
                Metal as String,
                DepId as String,
                PlateNumber as String,
                UUID as String,
                Type as String,
                Section as String
            )
        }


        fun completePSA(params: HashMap<String, String>, DSL: String, PSAProc: PSADSLProcessor): Unit {
            PSAProc.render(DSL)
            println("\n\n\nIN COMPLETE PSA!!!")
            val m = PSAProc.completePSA
            val mwp = PSAProc.completePSAwithPrice
            val Sor = params.get("Sor")
            val Tara = params.get("Tara")
            val UUID = params.get("UUID")
            val Price = params.get("Price")
            val ClientPrice = params.get("ClientPrice")
            val Client = params.get("Client")
            println("PRICE::$Price")
            if (Price == null){
                println("\n\n\nCALLING M")
                m(Tara as String, Sor as String, UUID as String)
                return
            }
            println("\n\n\n\nCALLING M with price")
            mwp(Tara as String, Sor as String, UUID as String, Price as String, ClientPrice as String)
            if (Client !=null){
                println("SETTING UP CLIENT ${Client} @ PSA ${UUID}")
                PSAProc.setupUniqueClient(UUID, Client)
            }


        }
    }

    val EFFECT = "является действующим"
    val UNEFFECT = "ЯВЛЯЕТСЯ НЕДЕЙСТВИТЕЛЬНЫМ"

    val jsparser = JSONParser()
    var comment: String = ""
    val NONE               = "NONE"
    val deps__             = Department()
    val DepsMap            = mapOf(6 to 1, 16 to 1, 10 to 2, 9 to 25)
    var login              = EMPTY_ATOM
    var pass               = EMPTY_ATOM
    var urldb              = EMPTY_ATOM

    var dumb               = EMPTY_ATOM
    var json_              = EMPTY_ATOM
    var HOOKUUID           = EMPTY_ATOM
    var HOOKSECTION        = EMPTY_ATOM
    var HOOKED             = FALSE_ATOM
    var ACTIVATE_PSA       = FALSE_ATOM
    var URL_TO_ACTIVATE    = EMPTY_ATOM
    var PASS_CHECK_URL     = EMPTY_ATOM
    var PASS_CHECK         = FALSE_ATOM

    var PSAID              = EMPTY_ATOM
    var SECTION            = EMPTY_ATOM
    var PSAIDHOOK          = FALSE_ATOM
    val COMPANY_ATOM       = "C"
    val PERSON_ATOM        = "P"
    val BLACK_ATOM         = "black"
    val COLOR_ATOM         = "color"

    var external_searchdsl = EMPTY_ATOM
    lateinit var psearch: PSASearchProcessor
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        clearhooked()
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return OK
    }


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
        var counter = 0
        while (res?.next() == true)
            numberpsa = res.getInt("number")
            ////println("${counter++} number PSA at currentRow::${res.getInt("number")}")
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


    var completePSA: completePSA = { Tare: String, Sor: String, UUID: String ->
        run {
            var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
                """UPDATE `weighing` SET  `tare` = ?, `sor` = ?,  `client_tare` = ?, `client_sor` = ?, `price`=? WHERE `uuid` = ?;"""
            )
            prepared?.setFloat(1, Tare.toFloat())
            prepared?.setFloat(3, Tare.toFloat())
            prepared?.setFloat(2, Sor.toFloat())
            prepared?.setFloat(4, Sor.toFloat())
            prepared?.setString(5, UUID)
            println("prepared @completePSA=> $prepared")

            prepared?.execute()
            activatePSA(UUID)
        }
    }

        var completePSAwithPrice: completePSAwithPrice = { Tare: String, Sor: String, UUID: String, price: String, ClientPrice: String ->
            run {
                var prepared = psearch.psaconnector.executor!!.conn.prepareStatement(
                    """UPDATE `weighing` SET  `tare` = ?, `sor` = ?,  `client_tare` = ?, `client_sor` = ?, `price`=?, `client_price`=? WHERE `uuid` = ?;"""
                )
                println("\n\n\nPRICE=>$price")
                println("\n\n\nClientPrice=>$ClientPrice")
                prepared?.setFloat(1, Tare.toFloat())
                prepared?.setFloat(3, Tare.toFloat())
                prepared?.setFloat(2, Sor.toFloat())
                prepared?.setFloat(4, Sor.toFloat())
                prepared?.setFloat(5, price.toFloat())
                prepared?.setFloat(6, ClientPrice.toFloat())
                prepared?.setString(7, UUID)
                println("prepared @completePSAwithPrice=> $prepared")

                prepared?.execute()
                activatePSA(UUID)
            }

        }

    fun constructURLwithId(id: Int): String{
        return "$URL_TO_ACTIVATE?id=$id"
    }

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
        var isBLACK=false
        if (js.get("section")!= null)
            section = js.get("section") as String
        if (HOOKED.equals(TRUE_ATOM))
            if (HOOKSECTION.length > 0)
                section = HOOKSECTION
        if (PSAIDHOOK.equals(TRUE_ATOM)){
            if (isBlack(vagning, PSAID)) {
                isBLACK = true
                println("\n\n\n\n\n\nHOOK SECTION SET TO $SECTION @ PSAID=$PSAID")
                section=SECTION
            }
        }
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
        if (isBLACK)
            updateDescriptionToBlack(uuid)
        if (js.get("client")!=null){
            val client: String = js.get("client").toString()
            println("FOUND CLIENT::$client")
            setupUniqueClient(uuid, client)
        }
        activatePSA(uuid)
    }

    fun activatePSA(uuid: String){
        if (ACTIVATE_PSA.equals(TRUE_ATOM))
            println("ACTIVATING PSA!!! UUID::$uuid, ${HTTPClient.sendGet(constructURLwithId(psearch.getPSAIdViaUUID(uuid)))}")
    }

    fun isBlack(Arr: JSONArray, PatternBlack: String): Boolean {
        var result = false
        Arr.forEach { a ->
            val B = a as JSONObject
            if (B.get("psaid").toString().equals(PatternBlack))
            result = true
            return result
        }
        return result
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
        prepared?.setFloat(4, json.get("median").toString().toFloat())
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, json.get("psaid").toString().toInt())
        prepared?.setFloat(7, json.get("weight").toString().toFloat())//json.get("brutto").toString().toFloat() )
        prepared?.setFloat(8, 0.0f)///json.get("tare").toString().toFloat())
        prepared?.setFloat(9, 0.0f)////json.get("clogging").toString().toFloat())
        prepared?.setFloat(10, json.get("median").toString().toFloat())
        prepared?.setString(11, (Math.round(inspect * 100.0) / 100.0).toString())
        prepared?.setString(12, uuid)
        println("prepared=> $prepared")
        if (prepared != null) {
            prepared.execute()
        }

        // prepared.setString();
    }

    fun String.prepare(): String{
        return this.replace("   "," ").replace("  "," ")
    }

    fun F_(S: String): String{
        return S.prepare().F()
    }

    fun I_(S: String): String{
        return S.prepare().I()
    }

    fun O_(S: String): String{
        return S.prepare().O()
    }

    fun String.F(): String{
        if (this.indexOf(" ")<0) return EMPTY_ATOM
        return this.substring(0, this.indexOf(" "))
    }

    fun String.I(): String{
        val F = this.F()
        if (F.equals(EMPTY_ATOM))
            return EMPTY_ATOM
        val Str  = this.substring(F.length+1)
        if (Str.indexOf(" ")<0)
            return EMPTY_ATOM
        return Str.substring(0, this.indexOf(" ")-1)
    }

    fun String.O(): String{
        val I = this.I()
        if (I.equals(EMPTY_ATOM))
            return EMPTY_ATOM
        return this.substring(this.indexOf(I)+I.length+1)
    }

    fun checkViaFIO(input: String): LinkedList<Any>{
        val param: java.util.ArrayList<Any> = java.util.ArrayList<Any>()
        param.add("%${F_(input)}%")
        param.add("%${I_(input)}%")
        param.add("%${O_(input)}%")
        var res: ResultSet = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`passport` WHERE `lname` LIKE ? AND `fname` LIKE ? AND `mname` LIKE ?;", param)
        return perfomResultSet(res, PERSON_ATOM)
    }

    fun perfomResultSet(RS: ResultSet, TYPE: String): LinkedList<Any>{
        var R = LinkedList<Any>()
        val Empty = LinkedList<Any>()
        if (RS.next()){
            R.addFirst(TYPE)
            R.add(RS.getInt("id"))
            if (RS.next())
                return  Empty;
            return R;
        }
        return Empty;
    }

    @Throws(SQLException::class)
    private fun updateCompany(UUID: String, name: String, id: Int) {
        val stmt: PreparedStatement = psearch.psaconnector.executor!!.getConn()
            .prepareStatement("UPDATE psa set company_id = ?, client = ?, `vat`='НДС исчисляется налоговым агентом', passport_id=NULL   WHERE uuid = ?")
        stmt.setInt(1, id)
        stmt.setString(2, name)
        stmt.setString(3, UUID)
        println(stmt)
        stmt.executeUpdate()
    }

    fun checkpassport(passportId: Int): Boolean{
        var param = java.util.ArrayList<Any?>()
        param.add(passportId)
        val res: ResultSet =
            psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`passport` WHERE `id` = ?", param)
        if (!res.next())
            return false
        val seria = res.getString("series")
        val number = res.getString("number")
        return checkpass(seria, number)
    }

    fun  checkpass(series: String, number: String) : Boolean{
        System.out.println("series "+series + "number"+ number);
        var answer = sendPost(series, number, PASS_CHECK_URL);
        var status = getStatusText(answer);
        System.out.println("STATUS "+status);
        when (status){
            EFFECT ->  return true;
            UNEFFECT ->  return false;
        }
        return false;
    }

    fun getStatusText(input: String): String? {
        val first = "\"StatusText\":"
        val index = input.indexOf(first)
        return if (index < 1) "" else input.substring(index + first.length + 1, input.length - 3)
    }

    @Throws(SQLException::class)
    private fun updateClient(UUID: String, name: String, idclient: Int) {
        if (PASS_CHECK.equals(TRUE_ATOM)){
            if (!checkpassport(idclient))
                return;
        }
        val stmt: PreparedStatement = psearch.psaconnector.executor!!.getConn()
            .prepareStatement("UPDATE psa set passport_id = ?, client = ?, `vat`='без НДС', company_id=NULL   WHERE uuid = ?")
        stmt.setInt(1, idclient)
        stmt.setString(2, name)
        stmt.setString(3, UUID)
        println(stmt)
        stmt.executeUpdate()
    }

    @Throws(SQLException::class)
    fun getCompanyName(ID: Int): String? {
        var param = java.util.ArrayList<Any?>()
        param.add(ID)
        val res: ResultSet =
            psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`company` WHERE `id` = ?", param)
        if (res.next()) {
            return res.getString("name")
        };
        return ""
    }

    @Throws(SQLException::class, IOException::class)
    fun getClientName(ID: Int): String? {
        var param = java.util.ArrayList<Any?>()
        param.add(ID)
        val res: ResultSet =
            psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`passport` WHERE `id` = ?", param)
        if (res.next())
            return "${res.getString("lname")} ${res.getString("fname")} ${res.getString("mname")}"
        return ""

    }

    fun setupUniqueClient(UUID: String, Client: String){
        val R = getUniqueClient(Client)
        when (R.size){
            0 -> return;
            2 -> {
                val TYPE = R.first
                val ID: Int= R.last.toString().toInt()
                when (TYPE){
                    PERSON_ATOM  -> {updateClient(UUID, getClientName(ID)!!, ID)};
                    COMPANY_ATOM -> {updateCompany(UUID, getCompanyName(ID)!!, ID)};
                }
            }
        }
    }

    fun getUniqueClient(input: String): LinkedList<Any>{
        val param: java.util.ArrayList<Any> = java.util.ArrayList<Any>()
        param.add("%$input%")
        var res: ResultSet = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`company` WHERE `inn` LIKE ?;", param)
        var R = perfomResultSet(res, COMPANY_ATOM);
        if ((R.size)>0)
            return R;
        res = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`company` WHERE `name` LIKE ?;", param)
        R = perfomResultSet(res, COMPANY_ATOM);
        if ((R.size)>0)
            return R;
        res = psearch.psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`passport` WHERE `number` LIKE ?;", param)
        R = perfomResultSet(res, PERSON_ATOM);
        if ((R.size)>0)
            return R;
        res = psearch.psaconnector.executor!!.executePreparedSelect(
            "SELECT * FROM `psa`.`passport` WHERE `series` LIKE ? AND `number` LIKE ?;",
            processPassportField(input, 4, false)
        )
        R = perfomResultSet(res, PERSON_ATOM);
        if ((R.size)>0)
            return R;
        res = psearch.psaconnector.executor!!.executePreparedSelect(
            "SELECT * FROM `psa`.`passport` WHERE `series` LIKE ? AND `number` LIKE ?;",
            processPassportField(input, 2, true)
        )
        R = perfomResultSet(res, PERSON_ATOM);
        if ((R.size)>0)
            return R;
        res = psearch.psaconnector.executor!!.executePreparedSelect(
            "SELECT * FROM `psa`.`passport` WHERE `series` LIKE ? AND `number` LIKE ?;",
            processPassportField(input, 3, true)
        )
        R = perfomResultSet(res, PERSON_ATOM);
        if ((R.size)>0)
            return R;
        R=checkViaFIO(input)
        if ((R.size)>0)
            return R;
        return LinkedList<Any>()
    }

    fun processPassportField(input: String, seriesLength: Int, ignoreDigits: Boolean): java.util.ArrayList<Any>? {
        val res = java.util.ArrayList<Any>()
        val sb_series = StringBuilder()
        val sb_number = StringBuilder()
        var seriescounter = 0
        for (i in 1..input.length) {
            if ((checkdigit(input[i - 1]) || ignoreDigits) && seriescounter < seriesLength) {
                seriescounter++
                sb_series.append(input[i - 1])
            } else sb_number.append(input[i - 1])
        }
        res.add("%$sb_series%")
        res.add("%$sb_number%")
        println("SERIES::>$sb_series")
        println("number::>$sb_number")
        return res
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
        prepared?.setFloat(4, (json.get("price").toString().toFloat()))
        prepared?.setInt(5, getPSAID(uuid))
        prepared?.setInt(6, getmetalID(json))
        prepared?.setFloat(7, Brutto)//json.get("brutto").toString().toFloat() )
        prepared?.setFloat(8, 0.0f)///json.get("tare").toString().toFloat())
        prepared?.setFloat(9, 0.0f)////json.get("clogging").toString().toFloat())
        prepared?.setFloat(10, (json.get("price").toString().toFloat()))
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
            println("CREATE DRAFT PSA WITH SECTION")
            var uuid = UUID
            var section = Section
            if (HOOKED.equals(TRUE_ATOM)){
                if (HOOKUUID.length > 0)
                    uuid = HOOKUUID
                if (HOOKSECTION.length > 0)
                    section = HOOKSECTION
            }

            println("SECTION::$section")
            val date: String = LocalDate.now().toString()
            prepared?.setString(1, getPSANumberviaDSL(DepId, section))//getPSANumber(DepId))
            prepared?.setDate(2, java.sql.Date.valueOf(date));
            prepared?.setString(3, PlateNumber)
            prepared?.setString(4, DepId)
            prepared?.setString(5, descriptionMap.get(Type))////LocalDate getDate
            prepared?.setString(6, Type)
            prepared?.setString(7, uuid)
            prepared?.setString(8, section)
            println("prepared=> $prepared")
            println("\n\nSECTION::$Section\n\n")
            if (prepared != null) {
                prepared.execute()
            }
            prepared = psearch.psaconnector.executor!!.conn.prepareStatement("""SELECT *  FROM `psa` WHERE `date` = ? AND `plate_number` LIKE ? 
                        AND `department_id` = ? AND `comment`='fromScales' AND `uuid`= ?;"""            )
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

    val activatePSA: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "activatePSA")
                ACTIVATE_PSA = a.key.Param as String
        }
    }

    val urltoActivate: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "urltoActivate")
                URL_TO_ACTIVATE = a.key.Param as String
        }
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

    val passcheckurl: RoleHandler= {
        mapper.forEach { a ->
            if (a.key.Name == "passcheckurl")
                PASS_CHECK_URL = (a.key.Param as String)
        }
    }

    val passcheck: RoleHandler= {
        mapper.forEach { a ->
            if (a.key.Name == "passcheck")
                PASS_CHECK = (a.key.Param as String)
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
            "psa"           -> mapper.put(R, psa)
            "db"            -> mapper.put(R, db)
            "enabled"       -> mapper.put(R, enable)
            "json"          -> mapper.put(R, json)
            "HOOK"          -> mapper.put(R, HOOK)
            "psaIDtoSEhooK" -> mapper.put(R, psaIDtoSEhooK)
            "activatePSA"   -> mapper.put(R, activatePSA)
            "urltoActivate" -> mapper.put(R, urltoActivate)
            "passcheckurl"  -> mapper.put(R, passcheckurl)
            "passcheck"     -> mapper.put(R, passcheck)
        }
    }

}