package abstractions

import java.io.Serializable

class Param: Serializable {
    var paramsActivated = false
    var map: HashMap<String, Any> = HashMap()
    var sequence: List<Any> = mutableListOf()
}