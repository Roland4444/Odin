package DSLGuided.requestsx
import DSLGuided.requestsx.HelperDBUpdate.HelperDBUpdate
import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.PSA.PSADSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import DSLGuided.requestsx.SMS.SMSDSLProcessor
import DSLGuided.requestsx.Sber.SberDSLProcessor
import DSLGuided.requestsx.WProcessor.DBConnector
import DSLGuided.requestsx.WProcessor.WProcessor
import abstractions.KeyValue
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
class DSL {
    companion object{
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
    constructor() {
        prepareMap()
        prepareDSLProcessors()
        initDSL()
    }
    var RequestsDSLProcessor: RequestsDSLProcessor? = null
    var HelperDBUpdate: HelperDBUpdate? = null
    var PSADSLProcessor: PSADSLProcessor? = null
    var SMSDSLProcessor: SMSDSLProcessor? = null
    var PSASearchProcessor: PSASearchProcessor? = null
    var PSAConnector: PSAConnector? = null
    var dslProcessors = mutableListOf<KeyValue>()
    var urltoDSLProc: HashMap<String, String>? = null
    var WProcessor: WProcessor? = null
    var DBConnector: DBConnector? = null
    var SberDSLProcessor: SberDSLProcessor? = null
    val HelperDBUpdate_ATOM         = "helperdbupdate"
    val RequestsDSLProcessor_ATOM   = "requests"
    val SMSDSLProcessor_ATOM        = "sms"
    val PSADSLProcessor_ATOM        = "psa"
    val PSAConnector_ATOM           = "psaconnector"
    val PSASearchProcessor_ATOM     = "psasearch"
    val WProcessor_ATOM             = "wprocessor"
    val DBConnector_ATOM            = "dbconnector"
    val SberDSLProcessor_ATOM       = "sber"
    val SERVER_ATOM                 = "server"
    fun prepareDSLProcessors() {
        HelperDBUpdate = HelperDBUpdate()
        RequestsDSLProcessor = RequestsDSLProcessor()
        SMSDSLProcessor = SMSDSLProcessor()
        PSADSLProcessor = PSADSLProcessor()
        PSAConnector = PSAConnector()
        PSASearchProcessor = PSASearchProcessor()
        WProcessor = WProcessor()
        DBConnector = DBConnector()
        SberDSLProcessor = SberDSLProcessor()
        dslProcessors.add(KeyValue(HelperDBUpdate_ATOM, HelperDBUpdate!!))
        dslProcessors.add(KeyValue(RequestsDSLProcessor_ATOM, RequestsDSLProcessor!!))
        dslProcessors.add(KeyValue(SMSDSLProcessor_ATOM, SMSDSLProcessor!!))
        dslProcessors.add(KeyValue(PSADSLProcessor_ATOM, PSADSLProcessor!!))
        dslProcessors.add(KeyValue(PSAConnector_ATOM, PSAConnector!!))
        dslProcessors.add(KeyValue(PSASearchProcessor_ATOM, PSASearchProcessor!!))
        dslProcessors.add(KeyValue(WProcessor_ATOM, WProcessor!!))
        dslProcessors.add(KeyValue(DBConnector_ATOM, DBConnector!!))
        dslProcessors.add(KeyValue(SberDSLProcessor_ATOM, SberDSLProcessor!!))
    }

    fun getDSLProc(NameProc: String): DSLProcessor{
        val A  = dslProcessors.stream().filter { A -> A.Key.equals(NameProc)}.toArray()
        return (A[0] as KeyValue).Value as DSLProcessor
    }



    fun initDSL() {

        getDSLProc(PSAConnector_ATOM)?.r(getDSLforObject(PSAConnector_ATOM, SERVER_ATOM))
        val psearch = getDSLProc(PSAConnector_ATOM) as PSASearchProcessor
        val pconnector = getDSLProc(PSASearchProcessor_ATOM) as PSAConnector
        psearch.psaconnector= pconnector
        PSADSLProcessor!!.psearch  = PSASearchProcessor!!
        getDSLProc(DBConnector_ATOM)?.r(getDSLforObject(DBConnector_ATOM, SERVER_ATOM))
        WProcessor!!.dbconnector=DBConnector!!
        HelperDBUpdate!!.dbconnector=DBConnector!!
        SberDSLProcessor!!.PSADSLProcessor = PSADSLProcessor!!
        getDSLProc(SberDSLProcessor_ATOM).r(getDSLforObject(SberDSLProcessor_ATOM, SERVER_ATOM))

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
