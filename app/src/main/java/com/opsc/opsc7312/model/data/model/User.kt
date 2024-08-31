package com.opsc.opsc7312.model.data.model

// This is a data class named User, representing a user entity.

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class User(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    var balance: Double = 0.0,
    var password: String = "",
    var token: String = ""
){
    // This secondary constructor is provided to initialize all properties with empty strings.
    constructor(): this("", "", "", 0.0, "", "")
}

