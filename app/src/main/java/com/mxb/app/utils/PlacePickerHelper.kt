package com.mxb.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mxb.app.model.common.AddressData

object PlacePickerHelper {

    private val allowedCountries = listOf("SA", "OM", "YE", "AE", "IN")

    fun createPlacePickerIntent(context: Context): Intent {
        val fields = listOf(
            Field.ID,
            Field.NAME,
            Field.DISPLAY_NAME,
            Field.LOCATION,
            Field.FORMATTED_ADDRESS,
            Field.ADDRESS_COMPONENTS
        )

        return Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountries(allowedCountries)
            .build(context)
    }

    fun registerLauncher(
        activityResultRegistryOwner: ActivityResultRegistryOwner,
        lifecycleOwner: LifecycleOwner,
        onPlaceSelected: (AddressData?) -> Unit,
        onError: (Status?) -> Unit = {}
    ): ActivityResultLauncher<Intent> {
        return activityResultRegistryOwner.activityResultRegistry.register(
            "place_picker_${System.currentTimeMillis()}",
            lifecycleOwner,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        onPlaceSelected(place.toAddressData())
                    }
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(result.data!!)
                    onError(status)
                }
            }
        }
    }

    private fun Place?.toAddressData(): AddressData? {
        if (this == null) return null

        return AddressData().apply {
            latitude = location?.latitude?.toString()
            longitude = location?.longitude?.toString()

            addressComponents?.asList()?.let { components ->
                street = components.firstOrNull { "route" in it.types }?.name
                building = components.firstOrNull { "street_number" in it.types }?.name

                city = components.firstOrNull { "locality" in it.types }?.name
                    ?: components.firstOrNull { "sublocality" in it.types }?.name
                            ?: components.firstOrNull { "administrative_area_level_2" in it.types }?.name

                state = components.firstOrNull { "administrative_area_level_1" in it.types }?.name
                country = components.firstOrNull { "country" in it.types }?.name
                postalCode = components.firstOrNull { "postal_code" in it.types }?.name
            }
        }
    }
}
