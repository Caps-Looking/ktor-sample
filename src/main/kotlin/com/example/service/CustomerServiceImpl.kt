package com.example.service

import com.example.models.Customer
import com.mongodb.client.MongoDatabase
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOneById

class CustomerServiceImpl(
    private val database: MongoDatabase
) : CustomerService {

    private val collection = database.getCollection<Customer>()

    override suspend fun getCustomers(call: ApplicationCall) {
        call.respond(collection.find().toList())
    }

    override suspend fun getCustomerById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val customer = collection.findOneById(id) ?: return call.respondText(
            "No customer with id $id",
            status = HttpStatusCode.NotFound
        )

        call.respond(customer)
    }

    override suspend fun createCustomer(call: ApplicationCall) {
        val customer = call.receive<Customer>()
        collection.insertOne(customer)

        call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
    }

    override suspend fun updateCustomer(call: ApplicationCall) {
        val customer = call.receive<Customer>()

        if (collection.updateOneById(customer.id, customer).wasAcknowledged()) {
            call.respondText("Customer updated correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }

    override suspend fun deleteCustomer(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )

        if (collection.deleteOneById(id).wasAcknowledged()) {
            call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }

}