package DSLGuided.requestsx.WProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
import se.roland.util.HTTPClient
import se.roland.util.HTTPForm
import java.io.File
import java.io.FileOutputStream
import java.net.http.HttpClient
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

    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
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
        HTTPClient.sendPOST(HTTPForm.MapParams(Params) as HashMap<String, String>, addresstoresend_)
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