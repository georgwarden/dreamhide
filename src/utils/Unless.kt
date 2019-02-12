package net.rocketparty.utils

inline fun unless(condition: Boolean, block: () -> Unit, orElse: () -> Unit = {}) {
    return if (!condition)
        block()
    else
        orElse()
}

inline fun <R> unless(condition: Boolean, block: () -> R, orElse: () -> R): R {
    return if (!condition)
        block()
    else
        orElse()
}