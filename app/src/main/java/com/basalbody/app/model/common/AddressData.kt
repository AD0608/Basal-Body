package com.basalbody.app.model.common

data class AddressData(
    var street: String? = null,
    var building: String? = "",
    var city: String? = null,
    var state: String? = null,
    var postalCode: String? = null,
    var country: String? = null,
    var latitude: String? = null,
    var longitude: String? = null
)