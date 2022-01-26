package DSLGuided.requestsx.PSA

//import DSL.abstractions.KeyValue
import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import se.roland.util.HTTPForm.collectParams
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.ResultSet
import java.time.Duration
import java.time.LocalDate
import java.util.*


class PSASearchProcessor  : DSLProcessor() {
    companion object {
        @Throws(Exception::class)
        fun search(input: String, PSASearch: PSASearchProcessor): String {
            PSASearch.r(input)
            return PSASearch.createJSONResponce(PSASearch.getPSA())
        }
        fun search(input: String, PSASearch: PSASearchProcessor, depsRestricted: String): String {
            val departname = PSASearch.getdepNameExecutor(depsRestricted);
            //input = input.replace()
            var input1 = PSASearch.parser.removeRolefromStringDSL(input, "department")
            var input = PSASearch.parser.removeRolefromStringDSL(input1, "limit")
            val appendDep = "},::department{'"+departname+"',''},::limit{200}."
            val finishDSL = input.replace("}.", appendDep)
            print("final string dsl =>$finishDSL")
            PSASearch.r(finishDSL)
            return PSASearch.createJSONResponce(PSASearch.getPSA())
        }
    }

    lateinit var searchFrom: String
    lateinit var searchTo: String
    lateinit var numberPsa: String
    lateinit var client__: String
    lateinit var platenumber_: String
    lateinit var typepayment_: String
    lateinit var passcheckurl_: String
    lateinit var section_: String

    lateinit var limit_: Integer
    var departments = mutableListOf<String>()
    lateinit var psaconnector: PSAConnector
    var if_present = false
    var initialString: StringBuilder = StringBuilder()
    var first = true
    fun reset() {
        initialString.setLength(0)
        searchFrom = ""
        searchTo = ""
        numberPsa = ""
        client__ = ""
        platenumber_ = ""
        typepayment_ = ""
        departments.clear()
        if_present = false
        first = true
    }

    override fun r(DSL: String): Any {
        reset()
        loadRoles(parseRoles(DSL))
        println("\n\n\nCALLING Handlers!\n\n\n")
        mapper.forEach { it.value.invoke(it.key) }
        println("EFFECTIVE STRIUNG \n${initialString.toString()}")
        if (initialString.toString().equals("SELECT * FROM psa  LIMIT 200")) {
            println(":::INITIAL REQUEST:::")
            initialString.clear()
            initialString.append("SELECT * FROM psa  ORDER BY id DESC LIMIT 200")
        }

        return "OK"
    }



    fun params() {
        if (!if_present) {
            initialString.append(" where  ")
            if_present = true
        }
        if (first) {
            first = false
            return
        }
        initialString.append(" AND  ")
    }

    fun getPSA(): ResultSet? {
        val stmt = psaconnector.executor!!.conn.createStatement()
        val res = stmt?.executeQuery(initialString.toString())
        return res
    }

    fun getdepIdExecutor(input: String): String {
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`department` WHERE `name` = ?;", param)
        if (res.next())
            return res.getString("id")
        return ""
    }

    fun getdepNameExecutor(input: String): String {
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`department` WHERE `id` = ?;", param)
        if (res.next()) {
            return res.getString("name")
        };
        return ""
    }

    fun getmetalName(metal_id: String): String {
        var param = ArrayList<Any?>()
        param.add(metal_id)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`metal` WHERE `id` = ?;", param)
        if (res.next())
            return res.getString("title");
        return ""
    }

    fun loadMetals(id: String): String {
        var param = ArrayList<Any?>()
        param.add(id)
        var result = mutableListOf<String>()
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`weighing` WHERE `psa_id` = ?;", param)
        while (res.next()) {
            var metalname = getmetalName(res.getString("metal_id"))
            result.add(metalname)
        }
        var restr = result.toString()
        var restr1 = restr.replace("[", "")
        var restr2 = restr1.replace("]", "")
        return restr2
    }

    fun appendRow(row: ResultSet): JSONObject {
        var jsonobj = JSONObject()

        jsonobj.put("id", row.getString("id"))
        jsonobj.put("datetime", row.getString("date"))
        jsonobj.put("department", getdepNameExecutor(row.getString("department_id")))
        jsonobj.put("psanumber", row.getString("number"))
        jsonobj.put("client", row.getString("client"))
        jsonobj.put("platenumber", row.getString("plate_number"))
        jsonobj.put("metals", loadMetals(row.getString("id")))
        jsonobj.put("uuid", row.getString("uuid"))
        return jsonobj

    }

    fun createJSONResponce(input: ResultSet?): String {
        var result = JSONArray()

        while (input?.next() == true) {
            result.add(appendRow(input))
        }
        return result.toString()
    }

    fun users(input: List<String>): String {
        val builder = StringBuilder()
        if (input.size == 0)
            return ""
        builder.append("(")
        for (i in 0..input.size - 1) {
            if (input.get(i) == "") continue
            println("CURRENT::"+input.get(i))
            val userid = input.get(i)
            builder.append("(user_id=$userid)")
            if ((i == input.size - 1))
                break
            builder.append("   or ")
        }
        builder.append(")")
        return builder.toString()
    }

    fun departments(input: List<String>): String {
        val builder = StringBuilder()
        if (input.size == 0)
            return ""
        builder.append("(")
        for (i in 0..input.size - 1) {
            if (input.get(i) == "") continue
            val departmentId = getdepIdExecutor(input.get(i))
            builder.append("(department_id=$departmentId)")
            if ((i == input.size - 1) or (input.get(i + 1) == ""))
                break
            builder.append("   or ")
        }
        builder.append(")")
        return builder.toString()
    }

    val limit: RoleHandler= {
        mapper.forEach { a ->
            if (a.key.Name == "limit") {
                limit_ = a.key.Param as Integer
                val appendix = " LIMIT ${limit_}"
                initialString.append(appendix)
            }
        }

    }

    val section: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "section") {
                section_ = a.key.Param as String
                params()
                val appendix = "( section='$section_')"
                initialString.append(appendix)
            }
        }
    }

    val department: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "department") {
                departments = a.key.Param as MutableList<String>
                params()
                val appendix = departments(departments)
                initialString.append(appendix)
            }
        }
    }

    val users: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "users") {
                departments = a.key.Param as MutableList<String>
                params()
                val appendix = users(departments)
                initialString.append(appendix)
            }
        }
    }
    val numberpsa: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "numberpsa") {
                numberPsa = a.key.Param as String
                params()
                val appendix = "( number='$numberPsa')"
                initialString.append(appendix)
            }
        }
    }
    val sql: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "sql") {
                initialString.append(a.key.Param as String)
            }
        }
    }

    val passcheckurl: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "passcheckurl") {
                passcheckurl_= (a.key.Param as String)
            }
        }
    }

    val datarange: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "datarange") {
                val keyvalue: KeyValue = a.key.Param as KeyValue
                searchFrom = keyvalue.Key
                searchTo = keyvalue.Value as String
                params()
                val appendix = "( `psa`.`date` between '${searchFrom}' and '${searchTo}')"
                initialString.append(appendix)
            }
        }
    }


    val date: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "date") {
                val keyvalue: KeyValue = a.key.Param as KeyValue
                searchFrom = keyvalue.Key
                searchTo = keyvalue.Value as String
                params()
                val appendix = "( `date` between '${searchFrom}' and '${searchTo}')"
                initialString.append(appendix)
            }
        }
    }

    val client: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "client") {
                client__ = a.key.Param as String
                params()
                val appendix =
                    " (client LIKE '%${client__}%')"               /// val appendix = " (client = '${client__}')"
                initialString.append(appendix)

            }
        }
    }
    val typepayment: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "typepayment") {
                typepayment_ = a.key.Param as String
                params()
            }
        }
    }
    val platenumber: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "platenumber") {
                platenumber_ = a.key.Param as String
                params()
                val appendix = " (plate_number= '${platenumber_}')"
                initialString.append(appendix)
            }
        }
    }





    fun getPSAIdViaUUID(UUID: String): Int {
        var param = ArrayList<Any?>()
        param.add(UUID)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`psa` WHERE `uuid` = ?;", param)
        if (res.next())
            return res.getInt("id")
        else
            return -1;
    }

    fun getPSAViaUUID(UUID: String): ResultSet? {
        var param = ArrayList<Any?>()
        param.add(UUID)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`psa` WHERE `uuid` = ?;", param)
        if (res.next())
            return res
        return null
    }

    fun getPSANumberviaDSL(DepsId: String, Section: String): String{
        println("in DSL psa getnumber")
        println("DEP_ID::$DepsId, SECTION::$Section")
        val name = getdepNameExecutor(DepsId)
        val year = Calendar.getInstance()[Calendar.YEAR]
        val date: String = LocalDate.now().toString()
        println("date => $date")
        val buildSearchDSL = "'search'=>::sql{'SELECT * FROM psa '},::section{'${Section}'},::department{'${name}',''},::datarange{'${year}-01-01':'${java.sql.Date.valueOf(date)}'}."
        println("PREPARED DSL for search=> $buildSearchDSL")
        r(buildSearchDSL)
        val res = getPSA()
        var numberpsa = 0;
        while (res?.next() == true) {
            val number = res.getInt("number")
            if ((number !=0) && (numberpsa<= number))
                numberpsa = number
            ////println("${counter++} number PSA at currentRow::${res.getInt("number")}")
        }
        numberpsa++
        println("PSA NUMBER==>$numberpsa")
        return numberpsa.toString()
    }


    fun getWViaPSAId(PsaId: String): ResultSet {
        var param = ArrayList<Any?>()
        param.add(PsaId)
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`weighing` WHERE `psa_id` = ?;", param)
        return res
    }

    fun getMetalInfo(): ResultSet{
        var param = ArrayList<Any?>()
        val res: ResultSet =
            psaconnector.executor!!.executePreparedSelect("SELECT * FROM `psa`.`metal`", param)
        return res
    }

    

    override fun appendRole(R: Role) {
        print("Adding role ${R.Name}\n")
        when (R?.Name) {
            "numberpsa" -> mapper.put(R, numberpsa)
            "datarange" -> mapper.put(R, datarange)
            "client" -> mapper.put(R, client)
            "typepayment" -> mapper.put(R, typepayment)
            "platenumber" -> mapper.put(R, platenumber)
            "department" -> mapper.put(R, department)
            "sql" -> mapper.put(R, sql)
            "passcheckurl" -> mapper.put(R, passcheckurl)
            "limit" -> mapper.put(R, limit)
            "section" -> mapper.put(R, section)
            "enabled" -> mapper.put(R, enable)
            "users" -> mapper.put(R, users)
            "date" -> mapper.put(R, date)

        }
    }

    @Throws(IOException::class)
    fun postRequest(series: String, number: String, url: String): String {
        val bodyPublisher = HttpRequest.BodyPublishers.ofString(collectParams(series, number))
        println("params ${collectParams(series, number)}")
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(2))
            .POST(bodyPublisher)
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