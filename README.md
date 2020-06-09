<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# CorDapp Upgrades

This sample project will shows you how to upgrade corDapps using explicit approaches.
Signature Constraint (Implicit-Upgrades) introduced in Corda 4.4 is however the recommended approach to
perform upgrades in Corda, since it doesn't requires the heavyweight process of creating upgrade transactions 
for every state on the ledger of all parties.

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html.

# Contract and Flow Version
This sample project has two version of contracts and State which will used to 
demonstrate explicit upgrades in Corda.
This project is use to create a student data base in block-chain where 2 parties are there, one is School and other is
students.First state contain only fName,lName,fatherName, PartyA and partyB, and the contracts contain only 
verification regarding that.

*Version 2 of Contract and State*
The secound version on State contain all the parameter addition to address which we need to add in the upgraded project 
and the contract verify the required verifications

#Explicit Upgrade Steps
**Step1:** 
Clean deploy the nodes and the run the nodes.

``./gradlew clean deployNodes
  ./build/nodes/runnodes``
  
**Step2:**
Register A student from the partyA shell by using this command 

``start AddNewStudentFlow addStudentRequest: {fName: "Jon", lName: "Snow", fatherName: "Rhaegar", partyA: "O=PartyA,L=London,C=GB", partyB: "O=PartyB,L=New York,C=US"}``
 
**Step3:**
 
  Run vaultQuery in each party's shell to check the states issued.
 ``run vaultQuery contractStateType: com.template.states.StudentsStateV1``
 
**Step4:**
Perform explicit upgrade to the contracts defined in StudentsContractV2. It can be done by running the 
client ExplicitContractAndStateUpgrade using below command

``./gradlew runUpgradeClient``

The client uses the `ContractUpgradeFlow` to upgrade the contracts and states to a new version.

**Step5:**

Run vaultQuery in each party's shell to check the upgraded states issued. Notice that the old states would have been consumed.

 ``run vaultQuery contractStateType: com.template.states.StudentsStateV2``
 
 That's all now you have a new state with upgraded data in the corda and for flow execution also.
  
