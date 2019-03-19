package net.rocketparty.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import net.rocketparty.entity.Id
import net.rocketparty.event.Broadcast
import net.rocketparty.repository.EventsRepository

class EventInteractor(
    private val eventsRepository: EventsRepository,
    private val scope: CoroutineScope
) {

    suspend fun subscribe(userId: Id): ReceiveChannel<Broadcast> {
        val channel = Channel<Broadcast>()

        scope.launch {
            for (event in eventsRepository.adminBroadcast())
                channel.send(event)
        }

        return channel
    }

    suspend fun adminBroadcast(message: Broadcast) {
        eventsRepository.doAdminBroadcast(message)
    }

}