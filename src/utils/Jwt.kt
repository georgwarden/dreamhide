package net.rocketparty.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.rocketparty.entity.Id
import net.rocketparty.entity.Token

object Claims {

    const val UserId = "user_id"

}

fun generateJwt(forUserId: Id): Token {
    return JWT.create()
        .withClaim(Claims.UserId, forUserId)
        .sign(Algorithm.HMAC256("old"))
}