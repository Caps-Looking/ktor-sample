package com.example.service

import io.ktor.application.*

interface CustomerService {

    suspend fun getCustomers(call: ApplicationCall)
    suspend fun getCustomerById(call: ApplicationCall)
    suspend fun createCustomer(call: ApplicationCall)
    suspend fun updateCustomer(call: ApplicationCall)
    suspend fun deleteCustomer(call: ApplicationCall)

}