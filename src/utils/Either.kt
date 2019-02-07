package net.rocketparty.utils

sealed class Either<out L, out R> {

    fun <T> fold(ifLeft: (L) -> T, ifRight: (R) -> T): T {
        return when(this) {
            is Left -> ifLeft(value)
            is Right -> ifRight(value)
        }
    }

    fun <N> mapLeft(map: (L) -> N): Either<N, R> {
        return fold({ Left(map(it)) }, { Right(it) })
    }

    fun <N> mapRight(map: (R) -> N): Either<L, N> {
        return fold({ Left(it) }, { Right(map(it)) })
    }

}

class Left<L>(
    val value: L
) : Either<L, Nothing>()

class Right<R>(
    val value: R
) : Either<Nothing, R>()

fun <R> retrieve(from: () -> R): Either<Throwable, R> {
    return try {
        Right(from())
    } catch (e: Throwable) {
        Left(e)
    }
}

fun <L, A> A?.wrap(ifNull: () -> L): Either<L, A> {
    return this?.let { Right(this) } ?: Left(ifNull())
}