package com.template

import com.template.contracts.StudentsContractV1
import com.template.contracts.StudentsContractV2
import com.template.states.StudentsStateV1
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.flows.ContractUpgradeFlow
import net.corda.core.utilities.NetworkHostAndPort
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger


fun main(args: Array<String>) {
    ExplicitContractAndStateUpgrade().main(args)
}

private class ExplicitContractAndStateUpgrade {
    companion object {
        val logger: Logger = loggerFor<ExplicitContractAndStateUpgrade>()
    }

    fun main(args: Array<String>) {
        require(args.size == 2) { "Usage: UpgradeStateClient <PartyA RPC address> <PartyB RPC address>" }

        // Create a connection to PartyA and PartyB.
        val (partyAProxy, partyBProxy) = args.map { arg ->
            val nodeAddress = NetworkHostAndPort.parse(arg)
            val client = CordaRPCClient(nodeAddress)
            client.start("user1", "test").proxy
        }

        listOf( partyAProxy, partyBProxy).forEach { proxy ->
            // Extract all the unconsumed State instances from the vault.
            val stateAndRefs = proxy.vaultQuery(StudentsStateV1::class.java).states

            // Run the upgrade flow for each one.
            stateAndRefs
                    .filter { stateAndRef ->
                        stateAndRef.state.contract.equals(StudentsContractV1.ID)
                    }.forEach { stateAndRef ->
                        println("--------Calling Authorize--------")
                        println("Authorize:$stateAndRef")
                        proxy.startFlowDynamic(
                                ContractUpgradeFlow.Authorise::class.java,
                                stateAndRef,
                                StudentsContractV2::class.java)
                    }
        }
        println("Authorizing state and contract upgrade, waiting 10 seconds")
        Thread.sleep(10000)

        partyAProxy.vaultQuery(StudentsStateV1::class.java).states
                .filter { stateAndRef ->
                    stateAndRef.state.contract.equals(StudentsContractV1.ID)
                }.forEach{stateAndRef ->
                    println("--------Calling Upgrade--------")
                    println("Upgrade:$stateAndRef")
                    partyAProxy.startFlowDynamic(
                        ContractUpgradeFlow.Initiate::class.java,
                        stateAndRef,
                        StudentsContractV2::class.java)
                }
    }
}