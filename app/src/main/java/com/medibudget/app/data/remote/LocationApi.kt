package com.medibudget.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LocationApi {

    @Headers("User-Agent: MediBudget-Android/1.0 (healthcare cost estimation)")
    @GET("https://nominatim.openstreetmap.org/reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("zoom") zoom: Int = 16,
        @Query("addressdetails") addressDetails: Int = 1
    ): OsmReverseResponse

    @Headers("User-Agent: MediBudget-Android/1.0 (healthcare hospital search)")
    @GET("https://nominatim.openstreetmap.org/search")
    suspend fun searchLocalHospitals(
        @Query("q") query: String = "hospital",
        @Query("viewbox") viewbox: String, // left,top,right,bottom
        @Query("bounded") bounded: Int = 1,
        @Query("limit") limit: Int = 25,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1
    ): List<OsmSearchResponse>
}

data class OsmAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val suburb: String?,
    val road: String?,
    val state: String?,
    val postcode: String?
)

data class OsmReverseResponse(
    val lat: String,
    val lon: String,
    val display_name: String,
    val address: OsmAddress?
)

data class OsmSearchResponse(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String
)
