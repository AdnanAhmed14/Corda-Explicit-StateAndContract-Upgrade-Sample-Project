package com.template.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object StudentsSchema2

object StudentsSchemaV2 : MappedSchema(
        StudentsSchema2::class.java,
        1,
        listOf(PersistentStudentV2::class.java)){
    @Entity
    @Table(name = "StudentsTable")
    class PersistentStudentV2(
            @field:Column(name = "fName") val fName: String,
            @field:Column(name = "lName") val lName: String,
            @field:Column(name = "fatherName") val fatherName: String,
            @field:Column(name = "partyA") val partyA: String,
            @field:Column(name = "partyB") val partyB: String,
            @field:Column(name = "address") val address: String
    ): PersistentState() {
        constructor(): this(
                "",
                "",
                "",
                "",
                "",
                ""
        )
    }
}