package com.example.routes

import com.example.service.CustomerService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.registerCustomerRoutes(customerService: CustomerService) {
    routing {
        route("/customer") {

            get { customerService.getCustomers(call) }
            get("{id}") { customerService.getCustomerById(call) }

            authenticate("auth-jwt") {
                post { customerService.createCustomer(call) }
                put { customerService.updateCustomer(call) }
                delete("{id}") { customerService.deleteCustomer(call) }
            }

        }
    }
}
