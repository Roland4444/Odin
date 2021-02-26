package DSLGuided.requestsx

import abstractions.Role


abstract class DSLProcessor {
    abstract fun render(DSL: String?): String?
    abstract fun parseRoles(DSL: String?): List<Role?>?
}
