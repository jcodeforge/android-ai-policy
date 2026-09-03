public class Quickstart {

    @AiCapability(
            name = "customer.delete",
            description = "Delete a customer",
            userInitiatedRequired = true,
            allowedCallerTypes = {CallerType.SELF},
            requiredPermissions = {"android.permission.WRITE_CONTACTS"}
    )
    public void deleteCustomer() {

        AndroidPolicy policy = AndroidPolicy.forSelfCalls(context);

        PolicyResult result = policy.evaluate(
                "customer.delete",
                true
        );

        if (result.isAllowed()) {
            // Perform the actual operation
            System.out.println("Customer deleted");
        }
    }
}