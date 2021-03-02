package abstractions

import java.io.Serializable

data class Role(val Name: String): Serializable{
    val Param: MutableList<String> = mutableListOf()

    override fun toString():String{
        return Name
    }

}