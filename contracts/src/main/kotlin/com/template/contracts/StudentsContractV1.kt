package com.template.contracts

import com.template.states.StudentsStateV1
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


class StudentsContractV1: Contract {
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        requireThat<Any?> {
            val out = tx.outputsOfType<StudentsStateV1>().single()
            "All of the participants must be signers." using (command.signers.containsAll(out.participants.map { it.owningKey }))

        }
    }
    interface Commands : CommandData {
        class Create : Commands
    }
    companion object {
        @JvmStatic
        val ID = "com.template.contracts.StudentsContractV1"
    }
}