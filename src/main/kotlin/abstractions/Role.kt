package abstractions

import java.io.Serializable

data class Role(val Name: String): Serializable{
    val Param: Param = Param()
    override fun toString():String{
        return Name
    }

}