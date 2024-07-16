package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;

public class ServerCrypto {
    public static final String cryptoSecret = "CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=";
    private static final Database database = Database.getInstance();
    public static String createTokenForLoginUser(String username){
        Algorithm algorithm = Algorithm.HMAC256(cryptoSecret); //das sollte man nicht machen (!)
        String token = JWT.create()
                .withIssuer("ConvoHub Server")
                .withSubject(username)
                .sign(algorithm);
        return token;
    }

    public static Cookie createSessionCookie(String token){
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true); // Cookie ist nur über HTTP erreichbar
        cookie.setSecure(false); // Erfordert HTTPS für das Cookie
        cookie.setPath("/"); // Setze den Pfad des Cookies
        cookie.setMaxAge(2000);
        cookie.setAttribute("SameSite", "None");
        cookie.setDomain("localhost");
        return cookie;
    }

    public static Cookie createLogoutCookie() {
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true); // Cookie ist nur über HTTP erreichbar
        cookie.setSecure(false); // Erfordert HTTPS für das Cookie
        cookie.setPath("/"); // Setze den Pfad des Cookie;
        cookie.setAttribute("SameSite", "None");
        cookie.setDomain("localhost");
        return cookie;
    }

    public static boolean checkIfUserIsLegit(String token){
        printDebug("checkIfUserIsLegit", token);
        String userId  = getUsernameFromToken(token);
        if(database.getUserId(getUsernameFromToken(token))  != -1){
            printDebug("checkIfUserIsLegit", "true");
            return true;
        }
        printDebug("checkIfUserIsLegit", "false");
        return false;
    }

    public static void printDebug(String option, String message){
        System.out.println(">> [REST-Service] ["+option+"]: "+message);
    }

    public static String getUsernameFromToken(String token){
        printDebug("getUserIDFromToken", token);
        DecodedJWT decodedJWT;
        Algorithm algorithm = Algorithm.HMAC256(cryptoSecret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("ConvoHub Server")
                .build();
        decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

}
