package com.basalbody.app.model.common

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FullDirectionResponse(
    @field:SerializedName("full_response")
    val fullResponse: Directions? = null,
) : Serializable

/**
 * geocoded_waypoints : [{"geocoder_status":"OK","place_id":"ChIJ-Y25iEuDXjkRCdnta-6NYxs","types":["street_address"]},{"geocoder_status":"OK","place_id":"ChIJLYLNtlObXjkRqyAEZlW_ujA","types":["route"]}]
 * routes : [{"bounds":{"northeast":{"lat":23.0751647,"lng":72.5251301},"southwest":{"lat":23.0462178,"lng":72.5154725}},"copyrights":"Map data ©2018 Google","legs":[{"distance":{"text":"3.7 km","value":3733},"duration":{"text":"9 mins","value":514},"end_address":"Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India","end_location":{"lat":23.0488473,"lng":72.5166279},"start_address":"312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India","start_location":{"lat":23.0751455,"lng":72.5251301},"steps":[{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head <b>south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn <b>right<\/b> toward <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn <b>left<\/b> onto <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn <b>right<\/b> onto <b>Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn <b>right<\/b> at <b>Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}],"traffic_speed_entry":[],"via_waypoint":[]}],"overview_polyline":{"points":"uzykCaatyLANbCf@fB\\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@"},"summary":"Sarkhej - Gandhinagar Hwy","warnings":[],"waypoint_order":[]}]
 * status : OK
 * error_message : Invalid request. Missing the 'destination' parameter.
 */
data class Directions(
    @field:SerializedName("status")
    var status: String? = null,
    @field:SerializedName("error_message")
    var errorMessage: String? = null,
    @field:SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoints>? = null,
    @field:SerializedName("routes")
    var routes: List<Routes>? = null
) : Serializable

/**
 * geocoder_status : OK
 * place_id : ChIJ-Y25iEuDXjkRCdnta-6NYxs
 * types : ["street_address"]
 */
data class GeocodedWaypoints(
    @field:SerializedName("geocoder_status")
    var geocoderStatus: String? = null,
    @field:SerializedName("place_id")
    var placeId: String? = null,
    @field:SerializedName("types")
    var types: List<String>? = null
)

/**
 * bounds : {"northeast":{"lat":23.0751647,"lng":72.5251301},"southwest":{"lat":23.0462178,"lng":72.5154725}}
 * copyrights : Map data ©2018 Google
 * legs : [{"distance":{"text":"3.7 km","value":3733},"duration":{"text":"9 mins","value":514},"end_address":"Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India","end_location":{"lat":23.0488473,"lng":72.5166279},"start_address":"312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India","start_location":{"lat":23.0751455,"lng":72.5251301},"steps":[{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head <b>south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn <b>right<\/b> toward <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn <b>left<\/b> onto <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn <b>right<\/b> onto <b>Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn <b>right<\/b> at <b>Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}],"traffic_speed_entry":[],"via_waypoint":[]}]
 * overview_polyline : {"points":"uzykCaatyLANbCf@fB\\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@"}
 * summary : Sarkhej - Gandhinagar Hwy
 * warnings : []
 * waypoint_order : []
 */
data class Routes(
    @field:SerializedName("bounds")
    var bounds: Bounds? = null,
    @field:SerializedName("copyrights")
    var copyrights: String? = null,
    @field:SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolyline? = null,
    @field:SerializedName("summary")
    var summary: String? = null,
    @field:SerializedName("legs")
    var legs: List<Legs>? = null,
    @field:SerializedName("warnings")
    var warnings: List<String>? = null,
    @field:SerializedName("waypoint_order")
    var waypointOrder: List<String>? = null
)

/**
 * northeast : {"lat":23.0751647,"lng":72.5251301}
 * southwest : {"lat":23.0462178,"lng":72.5154725}
 */
data class Bounds(
    @field:SerializedName("northeast")
    var northeast: Northeast? = null,
    @field:SerializedName("southwest")
    var southwest: Southwest? = null
)

/**
 * lat : 23.0751647
 * lng : 72.5251301
 */
data class Northeast(
    @field:SerializedName("lat")
    var lat: Double = 0.toDouble(),
    @field:SerializedName("lng")
    var lng: Double = 0.toDouble()
)

/**
 * lat : 23.0751647
 * lng : 72.5251301
 */
data class Southwest(
    @field:SerializedName("lat")
    var lat: Double = 0.toDouble(),
    @field:SerializedName("lng")
    var lng: Double = 0.toDouble()
)

/**
 * points : uzykCaatyLANbCf@fB\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@
 */
data class OverviewPolyline(
    @field:SerializedName("points")
    var points: String? = null
)

/**
 * distance : {"text":"3.7 km","value":3733}
 * duration : {"text":"9 mins","value":514}
 * end_address : Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India
 * end_location : {"lat":23.0488473,"lng":72.5166279}
 * start_address : 312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India
 * start_location : {"lat":23.0751455,"lng":72.5251301}
 * steps : [{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head <b>south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn <b>right<\/b> toward <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn <b>left<\/b> onto <b>Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn <b>right<\/b> onto <b>Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn <b>right<\/b> at <b>Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}]
 * traffic_speed_entry : []
 * via_waypoint : []
 */
data class Legs(
    @field:SerializedName("distance")
    var distance: Distance? = null,
    @field:SerializedName("duration")
    var duration: Duration? = null,
    @field:SerializedName("end_address")
    var endAddress: String? = null,
    @field:SerializedName("end_location")
    var endLocation: EndLocation? = null,
    @field:SerializedName("start_address")
    var startAddress: String? = null,
    @field:SerializedName("start_location")
    var startLocation: StartLocation? = null,
    @field:SerializedName("steps")
    var steps: List<Steps>? = null,
    @field:SerializedName("traffic_speed_entry")
    var trafficSpeedEntry: List<String>? = null,
    @field:SerializedName("via_waypoint")
    var viaWaypoint: List<String>? = null
)

/**
 * text : 3.7 km
 * value : 3733
 */
data class Distance(
    @field:SerializedName("text")
    var text: String? = null,
    @field:SerializedName("value")
    var value: Int = 0
)

/**
 * text : 9 mins
 * value : 514
 */
data class Duration(
    @field:SerializedName("text")
    var text: String? = null,
    @field:SerializedName("value")
    var value: Int = 0
)

/**
 * lat : 23.0488473
 * lng : 72.5166279
 */
data class EndLocation(
    @field:SerializedName("lat")
    var lat: Double = 0.toDouble(),
    @field:SerializedName("lng")
    var lng: Double = 0.toDouble()
)

/**
 * lat : 23.0751455
 * lng : 72.5251301
 */
data class StartLocation(
    @field:SerializedName("lat")
    var lat: Double = 0.toDouble(),
    @field:SerializedName("lng")
    var lng: Double = 0.toDouble()
)

/**
 * distance : {"text":"0.1 km","value":145}
 * duration : {"text":"1 min","value":42}
 * end_location : {"lat":23.0739826,"lng":72.5247022}
 * html_instructions : Head <b>south</b><div style="font-size:0.9em">Partial restricted usage road</div>
 * polyline : {"points":"uzykCaatyLANbCf@fB\\"}
 * start_location : {"lat":23.0751455,"lng":72.5251301}
 * travel_mode : DRIVING
 * maneuver : turn-right
 */
data class Steps(
    @field:SerializedName("distance")
    var distance: Distance? = null,
    @field:SerializedName("duration")
    var duration: Duration? = null,
    @field:SerializedName("end_location")
    var endLocation: EndLocation? = null,
    @field:SerializedName("html_instructions")
    var htmlInstructions: String? = null,
    @field:SerializedName("polyline")
    var polyline: Polyline? = null,
    @field:SerializedName("start_location")
    var startLocation: StartLocation? = null,
    @field:SerializedName("travel_mode")
    var travelMode: String? = null,
    @field:SerializedName("maneuver")
    var maneuver: String? = null
)

/**
 * points : uzykCaatyLANbCf@fB\
 */
data class Polyline(
    @field:SerializedName("points")
    var points: String? = null
)