package net.rocketparty.event

import com.google.gson.annotations.SerializedName

data class Broadcast(
    @SerializedName("priority")
    val priority: Priority,
    @SerializedName("message")
    val message: String
) {

    enum class Priority {
        Info,
        Admin,
        Critical
    }

}
