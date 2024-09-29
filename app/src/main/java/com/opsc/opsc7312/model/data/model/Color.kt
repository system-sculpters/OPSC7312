package com.opsc.opsc7312.model.data.model

// This is a data class representing a Color entity.

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class Color(
    var Id: String,   // Unique ID for the color (could be a hex code or database ID)
    var name: String  // Name of the color (e.g., "Red", "Green")
)