package eda.teamred.entity

class CommonCustomerFactory : CustomerFactory {
    override fun create(firstName: String, lastName: String, address: String): Customer {
        return CommonCustomer(firstName, lastName, address)
    }
}