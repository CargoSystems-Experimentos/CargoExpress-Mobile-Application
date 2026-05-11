package com.cargoexpress.app.core.data.repository

import org.json.JSONObject
import retrofit2.Response

object ApiErrorParser {
    fun parse(response: Response<*>): String {
        val raw = response.errorBody()?.string()?.trim().orEmpty()
        if (raw.isBlank()) return "Error: ${response.code()}"

        return try {
            val json = JSONObject(raw)

            val message = json.optString("message")
            if (message.isNotBlank()) return message

            val errors = json.optJSONObject("errors")
            if (errors != null) {
                val keys = errors.keys()
                val chunks = mutableListOf<String>()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val arr = errors.optJSONArray(key)
                    if (arr != null && arr.length() > 0) {
                        chunks += arr.getString(0)
                    }
                }
                if (chunks.isNotEmpty()) return chunks.joinToString(" | ")
            }

            "Error: ${response.code()}"
        } catch (_: Exception) {
            "Error: ${response.code()} - $raw"
        }
    }
}
