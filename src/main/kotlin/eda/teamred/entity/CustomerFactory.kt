package eda.teamred.entity

interface CustomerFactory {
    fun create(firstName : String, lastName : String, address : String) : Customer
}