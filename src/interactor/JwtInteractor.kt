package net.rocketparty.interactor

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import net.rocketparty.entity.Id
import net.rocketparty.entity.Token
import net.rocketparty.repository.JwtRepository
import net.rocketparty.utils.Claims

class JwtInteractor(
    private val repository: JwtRepository
) : JwtRepository by repository {

    fun validate(payload: Payload): Boolean {
        return payload.audience.contains(getAudience())
    }

    fun generateToken(user: Id): Token {
        return JWT.create()
            .withAudience(getAudience())
            .withIssuer(getIssuer())
            .withClaim(Claims.UserId, user)
            .sign(Algorithm.HMAC256(getSecret()))
    }

    fun validateAdmin(payload: Payload): Boolean {
        return payload.audience.contains(getAdminAudience())
    }

    fun generateAdminToken(): Token {
        return JWT.create()
            .withAudience(getAdminAudience())
            .withIssuer(getIssuer())
            .sign(Algorithm.HMAC256(getSecret()))
    }

}