package DSLGuided.requestsx

import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import DSLGuided.requestsx.SMS.SMSDSLProcessor
import DSLGuided.requestsx.WProcessor.WProcessor
import Message.abstractions.BinaryMessage
import se.roland.abstractions.RolesStorage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*


class DSL {
    var dslfile: String? = null

    constructor() {
        prepareMap()
        prepareDSLProcessors()
    }

    constructor(dslfile_: String?) {
        if (File(dslfile_).exists()) rs =
            BinaryMessage.restored(BinaryMessage.readBytes(dslfile_)) as RolesStorage else {
            rs = RolesStorage()
            BinaryMessage.write(BinaryMessage.savedToBLOB(rs), dslfile_)
        }
        dslfile = dslfile_
        prepareDSLProcessors()
        prepareMap()
    }

    var RequestsDSLProcessor: RequestsDSLProcessor? = null
    var PSADSLProcessor: PSADSLProcessor? = null
    var SMSDSLProcessor: SMSDSLProcessor? = null
    var PSASearchProcessor: PSASearchProcessor? = null
    var PSAConnector: PSAConnector? = null
    var dslProcessors: HashMap<String, DSLProcessor>? = null
    var urltoDSLProc: HashMap<String, String>? = null
    var WProcessor: WProcessor? = null
    var rs: RolesStorage? = null
    fun prepareDSLProcessors() {
        RequestsDSLProcessor = RequestsDSLProcessor()
        SMSDSLProcessor = SMSDSLProcessor()
        PSADSLProcessor = PSADSLProcessor()
        PSAConnector = PSAConnector()
        PSASearchProcessor = PSASearchProcessor()
        WProcessor = WProcessor()
        dslProcessors = HashMap()
        dslProcessors!!["requests"] = RequestsDSLProcessor!!
        dslProcessors!!["sms"] = SMSDSLProcessor!!
        dslProcessors!!["psa"] = PSADSLProcessor!!
        dslProcessors!!["psaconnector"] = PSAConnector!!
        dslProcessors!!["psasearch"] = PSASearchProcessor!!
        dslProcessors!!["wprocessor"] = WProcessor!!
    }

    fun prepareMap() {
        urltoDSLProc = HashMap()
        urltoDSLProc!!["/login"] = "requests"
    }

    @Throws(IOException::class)
    fun getDSLforObject(nameObject: String, User: String): String {
        val f = File("rules" + File.separator + User + ".rules")
        if (!f.exists()) return ""
        val fr = FileReader(f.path)
        val br = BufferedReader(fr)
        var line: String
        while (br.readLine().also { line = it } != null) {
            val str = "'$nameObject'"
            if (line.indexOf(str) > -1) {
                br.close()
                fr.close()
                return line
            }
        }
        br.close()
        fr.close()
        return ""
    }
}
