package net.rocketparty.repository

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import net.rocketparty.event.Broadcast

class EventsRepository {

    private val adminChannel = BroadcastChannel<Broadcast>(2)

    fun adminBroadcast(): ReceiveChannel<Broadcast> {
        return adminChannel.openSubscription()
    }

    suspend fun doAdminBroadcast(message: Broadcast) {
        adminChannel.send(message)
    }

}