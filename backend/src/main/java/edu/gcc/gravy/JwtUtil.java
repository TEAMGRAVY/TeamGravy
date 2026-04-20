package edu.gcc.gravy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    private static final String SUPABASE_JWT_SECRET = "YOUR_JWT_SECRET";

    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SUPABASE_JWT_SECRET))
                .build()
                .verify(token);
    }
}
