package com.generation.progetto_finale.auth.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTGenerator 
{
	private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	@Value("${jwtduration}")
	private long JWT_DURATION;
	
	public String generateToken(Authentication authentication) 
	{
	 	User userPrincipal = (User) authentication.getPrincipal();
    	List<String> roles = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
			.map(role -> "ROLE_" + role) 
            .collect(Collectors.toList());
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + (JWT_DURATION*60*1000));
		
		String token = Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.claim("roles", roles)
				.setIssuedAt( new Date())
				.setExpiration(expireDate)
				.signWith(key,SignatureAlgorithm.HS512)
				.compact();
		System.out.println("New token :");
		System.out.println(token);
		return token;
	}
	
	public String getUsernameFromJWT(String token){
		
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }

	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect",ex.fillInStackTrace());
		}
	}

}
