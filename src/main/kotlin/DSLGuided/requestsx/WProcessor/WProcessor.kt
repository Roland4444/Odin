package DSLGuided.requestsx.WProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.Role
import util.Saver.Companion.write
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

/////"'wprocessor'=>::pathtoimgs{/home/romanx/IMG},::enabled{'false'}."
class WProcessor : DSLProcessor()  {

    companion object {
        fun saveImages(DSL: String, WProc: WProcessor, Arr1: ByteArray, Arr2: ByteArray,Department: String, Date: String, WaybillID: String){
            WProc.render(DSL)
            WProc.saveImages(Arr1, Arr2, Department, Date, WaybillID)
        }
    }
    lateinit var pathtoimgs_ : String
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
            "enabled" -> mapper.put(R, enable)

        }
    }
}