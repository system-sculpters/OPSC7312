package com.opsc.opsc7312.model.data.model

// This is a data class named User, representing a user entity.

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class User(
    var id: String = "",               // Unique ID for the user
    var username: String = "",          // The username for the user's account
    var email: String = "",             // The user's email address
    var balance: Double = 0.0,          // The current balance associated with the user's account
    var password: String = "",          // The user's password (ideally stored securely, hashed)
    var token: String = "",             // Authentication token used for API requests and sessions
    var error: String = ""              // An error message, if applicable, for handling user-related errors
){
    // This secondary constructor is provided to initialize all properties with empty strings.
    constructor(): this("", "", "", 0.0, "", "", "")
}

