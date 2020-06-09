package com.template.states

import com.template.contracts.StudentsContractV1
import com.template.schema.StudentsSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import java.io.Serializable

@CordaSerializable
@BelongsToContract(StudentsContractV1::class)
data class StudentsStateV1 (val fName: String,
                       val lName: String,
                       val fatherName: String,
                       val partyA: Party,
                       val partyB: Party
): QueryableState, Serializable, ContractState {
    override val participants get() = listOf(partyA,partyB)
    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is StudentsSchemaV1 -> StudentsSchemaV1.PersistentStudent(
                    this.fName,
                    this.lName,
                    this.fatherName,
                    this.partyA.toString(),
                    this.partyB.toString()
            )
            else -> throw IllegalArgumentException("Unrecognised schema")
        }
    }
    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(StudentsSchemaV1)
}