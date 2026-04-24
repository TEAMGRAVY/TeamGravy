package edu.gcc.gravy;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class AuthMiddleware implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        String authHeader = ctx.header("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).result("Missing token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            var jwt = JwtUtil.verify(token);
            ctx.attribute("userId", jwt.getSubject());
            ctx.attribute("token", token);
        } catch (Exception e) {
            ctx.status(401).result("Invalid token");
        }
    }
}
