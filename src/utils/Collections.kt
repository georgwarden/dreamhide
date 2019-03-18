package net.rocketparty.utils

fun <L> Iterable<Pair<L, *>>.left(): Iterable<L> =
        map { (left, _) -> left }

fun <R> Iterable<Pair<*, R>>.right(): Iterable<R> =
        map { (_, right) -> right }