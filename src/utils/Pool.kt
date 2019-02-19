package net.rocketparty.utils

import java.util.LinkedList

class Pool<T>(
    val supplier: () -> T,
    val reset: (T) -> Unit = {}
) {

    private val internalStorage = LinkedList<T>()

    fun obtain() = internalStorage.poll() ?: supplier()

    fun free(instance: T) {
        reset(instance)
        internalStorage.add(instance)
    }

}