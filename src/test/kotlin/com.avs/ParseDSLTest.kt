package com.avs

import abstractions.DSLRole
import abstractions.Role
import org.junit.Test
import kotlin.test.assertEquals

internal class ParseDSLTest {

    @Test
    fun getDSLRulestoObject() {
        val input: String = """'requests' => ::read{}, ::write{}, ::create{}."""
        val readRole: Role = Role("read")
        val writeRole: Role = Role("write")
        val createRole: Role = Role("create")
        var Roles: MutableList<Role> = mutableListOf(readRole, writeRole, createRole)
        var ObjectRules : DSLRole = DSLRole("requests", Roles)
        val parser = ParseDSL();
        assertEquals(ObjectRules, parser.getDSLRulestoObject(input))
    }
}


