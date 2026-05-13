package com.cargoexpress.app.core.common


object Constants {
    const val PRODUCTION_BASE_URL = "https://cargoexpress-backend-production.up.railway.app/api/v1/"
    const val LOCAL_IP = "10.0.2.2"
    const val LOCAL_PORT = "5194"

    var USE_LOCAL_API = true

    val BASE_URL: String
        get() = if (USE_LOCAL_API) {
            "http://$LOCAL_IP:$LOCAL_PORT/api/v1/"
        } else {
            PRODUCTION_BASE_URL
        }

    var TOKEN: String = ""
    var USER_ID: Int = 0
    var USER_NAME: String = ""
    var ENTREPRENEUR_ID: Int = 0
    var USER_ROLE: String = ""
    var TRIP_ID: Int = 0
    var CLIENT_ID: Int = 0
}
