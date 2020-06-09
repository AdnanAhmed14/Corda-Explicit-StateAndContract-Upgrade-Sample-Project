package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.StudentsContractV1
import com.template.states.StudentsStateV1
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

class AddNewStudentFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(private val addStudentRequest: StudentsStateV1) : FlowLogic<SignedTransaction>() {
        /**
         * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
         * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
         */
        companion object {
            object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new student.")
            object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
            object GATHERING_SIGS : ProgressTracker.Step("Gathering the counter party's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        @Throws(FlowException::class)
        override fun call(): SignedTransaction {
            val notary = serviceHub.networkMapCache.notaryIdentities[0]
            // Stage 1.
            progressTracker.currentStep = GENERATING_TRANSACTION
            // Generate an unsigned transaction.
            val partyA: Party = addStudentRequest.partyA
            val partyB: Party = addStudentRequest.partyB
            val command = Command(StudentsContractV1.Commands.Create(),
                    listOf(partyA.owningKey,partyB.owningKey) )
            val txBuilder = TransactionBuilder(notary = notary).
                    addOutputState(addStudentRequest, StudentsContractV1.ID).
                    addCommand(command)
            // Stage 2.
            progressTracker.currentStep = VERIFYING_TRANSACTION
            // Verify that the transaction is valid.
            txBuilder.verify(serviceHub)
            // Stage 3.
            progressTracker.currentStep = SIGNING_TRANSACTION
            // Sign the transaction.
            val partySignedTx = serviceHub.signInitialTransaction(txBuilder)
            // Stage 4.
            progressTracker.currentStep = GATHERING_SIGS
            // Send the state to the other parties, and receive it back with their signature.
            val partyBSession = initiateFlow(partyB)
            val fullySignedTx = subFlow(
                    CollectSignaturesFlow(partySignedTx, listOf(partyBSession), CollectSignaturesFlow.tracker()))

            // Stage 5.
            progressTracker.currentStep = FINALISING_TRANSACTION
            logger.info("Gathering the counter party's signature.")
            // Notarise and record the transaction in both parties' vaults.
            return subFlow<SignedTransaction>(FinalityFlow(fullySignedTx, listOf(partyBSession)))
        }
    }
    @InitiatedBy(Initiator::class)
    class Responder(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
        @Suspendable
        @Throws(FlowException::class)
        override fun call(): SignedTransaction {
            class SignTxFlow(otherPartyFlow: FlowSession, progressTracker: ProgressTracker) : SignTransactionFlow(otherPartyFlow, progressTracker) {
                override fun checkTransaction(stx: SignedTransaction) {
                    requireThat<Any?> {

                    }
                }
            }
            val signTxFlow = SignTxFlow(otherPartySession, SignTransactionFlow.tracker())
            val txId = subFlow(signTxFlow).id
            logger.info("End of the flow")
            return subFlow(ReceiveFinalityFlow(otherPartySession, txId))
        }
    }
}