package com.template.states

import com.template.contracts.StudentsContractV2
import com.template.schema.StudentsSchemaV2
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import org.jetbrains.annotations.Nullable
import java.io.Serializable

@CordaSerializable
@BelongsToContract(StudentsContractV2::class)
data class StudentsStateV2(val fName: String,
                           val lName: String,
                           val fatherName: String,
                           val partyA: Party,
                           val partyB: Party,
                           @Nullable val address: String?
): QueryableState, Serializable, ContractState {
    override val participants get() = listOf(partyA,partyB)
    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is StudentsSchemaV2 -> StudentsSchemaV2.PersistentStudentV2(
                    this.fName,
                    this.lName,
                    this.fatherName,
                    this.partyA.toString(),
                    this.partyB.toString(),
                    this.address.toString()
            )
            else -> throw IllegalArgumentException("Unrecognised schema")
        }
    }
    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(StudentsSchemaV2)
}