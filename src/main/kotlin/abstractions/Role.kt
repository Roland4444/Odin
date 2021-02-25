package abstractions

data class Role(val Name: String){
    val Param: MutableList<String> = mutableListOf()

    override fun toString():String{
        return Name
    }

}