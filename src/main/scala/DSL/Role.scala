package DSL

import com.avs.ParseDSL
import java.io.Serializable

case class Role(val Name: String, val params: String, val parser: ParseDSL) extends Serializable{
   

}