package com.template.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object StudentsSchema1

object StudentsSchemaV1 : MappedSchema(
        StudentsSchema1::class.java,
        1,
        listOf(PersistentStudent::class.java)){
    @Entity
    @Table(name = "StudentsTable")
    class PersistentStudent(
            @field:Column(name = "fName") val fName: String,
            @field:Column(name = "lName") val lName: String,
            @field:Column(name = "fatherName") val fatherName: String,
            @field:Column(name = "partyA") val partyA: String,
            @field:Column(name = "partyB") val partyB: String
    ): PersistentState() {
        constructor(): this(
                "",
                "",
                "",
                "",
                ""
        )
    }
}