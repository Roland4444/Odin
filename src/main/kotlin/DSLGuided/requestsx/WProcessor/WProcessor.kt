package DSLGuided.requestsx.WProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
import se.roland.util.HTTPClient
import java.io.File
import java.io.FileOutputStream
import java.net.http.HttpClient
import java.sql.ResultSet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


/////"'wprocessor'=>::pathtoimgs{/home/romanx/IMG},::addresstoresend{db2.avs.com.ru/storage/purchase/import},::enabled{'false'}."
class WProcessor : DSLProcessor()  {

    companion object {
        fun saveImages(DSL: String, WProc: WProcessor, Arr1: ByteArray, Arr2: ByteArray,Department: String, Date: String, WaybillID: String){
            WProc.render(DSL)
            WProc.saveImages(Arr1, Arr2, Department, Date, WaybillID)
        }

        fun resend(DSL: String, WProc: WProcessor, Params: HashMap<String, String>){
            WProc.render(DSL)
            WProc.resenddata(Params)
        }


    }
    lateinit var pathtoimgs_ : String
    lateinit var addresstoresend_: String
    var client: HttpClient = HttpClient.newHttpClient()
    val i_ ="_"
    val appendix = ".jpg"
    lateinit var dbconnector: DBConnector
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }

    fun getW(DepId: String): ResultSet? {
        var param = ArrayList<Any?>()
        param.add(DepId)
        val cal   = Calendar.getInstance();
        val D_now = cal.getTime();
        cal.add(Calendar.DATE, -3);
        val D_3 = cal.getTime();
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val D_3Date =  dateFormat.format(D_3)
        val D_nowDate =  dateFormat.format(D_now)
        println(D_nowDate)
        println(D_3Date)
        println("PREPARED::\n"+"SELECT * FROM `transfer` WHERE (`actual_weight`= '0.00') AND (`dest_department_id`=${DepId}) AND (`date` between '${D_3Date}' and '${D_nowDate}')")

        val res: ResultSet? =dbconnector.executor?.executePreparedSelect("SELECT * FROM `transfer` WHERE (`actual_weight`= '0.00') AND (`dest_department_id`=?) AND (`date` between '${D_3Date}' and '${D_nowDate}')", param)
        while (res?.next() == true){
            println(res.getInt("id"))
        }
        return res
    }


    fun saveImages(Arr1: ByteArray, Arr2: ByteArray, Department: String, Date: String, WaybillID: String): Unit{
        val targetDir = pathtoimgs_+File.separator+Date
        println("TARGET DIR::$targetDir")
        if (!File(targetDir).exists())
            File(targetDir).mkdirs()
        val filename = targetDir+File.separator+Department+i_+WaybillID+i_
        val filename1 = filename+1
        val filename2 = filename+2
        val fos1 = FileOutputStream(filename1+appendix)
        fos1.write(Arr1)
        val fos2 = FileOutputStream(filename2+appendix)
        fos2.write(Arr2)
    }

    fun resenddata(Params: HashMap<String, String>){
        println("\n\n\n\nRESENDING!!!!\n\n\n\n\n")
        HTTPClient.sendPOST(Params, addresstoresend_)
    }

    val pathtoimgs: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "pathtoimgs") {
                pathtoimgs_ = a.key.Param as String
                if (!File(pathtoimgs_).exists()){
                    File(pathtoimgs_).mkdirs()
                }
            }
        }
    }

    val addresstoresend: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "addresstoresend") {
                addresstoresend_ = a.key.Param as String

            }
        }
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    fun loadRoles(D: List<Role>): Unit {
        mapper.clear()
        D.forEach { appendRole(it) }
    }



    fun appendRole(R: Role) {
        print("Adding role ${R.Name}\n")
        when (R?.Name) {
            "pathtoimgs" -> mapper.put(R, pathtoimgs)
            "addresstoresend" -> mapper.put(R, addresstoresend)
            "enabled" -> mapper.put(R, enable)
        }
    }
}