package net.rocketparty.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.rocketparty.entity.Id
import net.rocketparty.entity.Token

fun generateJwt(forUserId: Id): Token {
    return JWT.create()
        .withClaim("user_id", forUserId)
        .sign(Algorithm.HMAC256("old"))
}