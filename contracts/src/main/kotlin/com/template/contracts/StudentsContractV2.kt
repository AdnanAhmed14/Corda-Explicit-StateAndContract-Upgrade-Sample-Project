package com.template.contracts

import com.template.states.StudentsStateV1
import com.template.states.StudentsStateV2
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import org.jetbrains.annotations.NotNull

class StudentsContractV2: UpgradedContract<StudentsStateV1,StudentsStateV2> {
    override val legacyContract: ContractClassName
        get() = StudentsContractV1.ID

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        requireThat<Any?> {
            val out = tx.outputsOfType<StudentsStateV1>().single()
            "All of the participants must be signers." using (command.signers.containsAll(out.participants.map { it.owningKey }))

        }
    }

    override fun upgrade(@NotNull state: StudentsStateV1): StudentsStateV2 {
        return StudentsStateV2(state.fName,state.lName,state.fatherName,state.partyA,state.partyB,null)
    }
    interface Commands : CommandData {
        class Create : Commands
    }
}