package abstractions

import com.avs.ParseDSL
import java.io.Serializable

data class Role(val Name: String, val params: String, val parser: ParseDSL): Serializable{
    var Param = parser.Atom(params)
    override fun toString():String{
        return Name
    }

}