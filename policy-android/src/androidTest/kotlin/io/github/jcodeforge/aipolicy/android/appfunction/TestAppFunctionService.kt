package io.github.jcodeforge.aipolicy.android.appfunction

import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import io.github.jcodeforge.aipolicy.capability.AiCapability

@AppFunctionServiceEntryPoint(
    serviceName = "TestAppFunctionService",
    appFunctionXmlFileName = "test_app_function_service"
)
abstract class TestAppFunctionServiceBase  : AppFunctionService() {

    @AppFunction
    @AiCapability(
        name = "test.appfunction.customer.read",
        description = "Read customer information"
    )
    fun getCustomerName(): String {
        return "Test Customer"
    }
}