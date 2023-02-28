package com.kotak.mb2.admin.administration.config;


import com.kotak.mb2.admin.administration.repository.AdminSessionTokenDetailsRepository;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;


@Service
@Slf4j
public class JWTUtils {

	 @Value("${jwt.encryption.key}")
	 private String key;

	 @Value("${jwt.encryption.algorithm}")
	 private String algorithm;

	@Value("${jwt.expiry.milliseconds}")
	private int jwtExp;

	@Autowired private AdminSessionTokenDetailsRepository sessionTokenDetailsRepository;

	public String validateJwtToken(String bearerToken, String username) {

		try {

			if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
				String token = bearerToken.substring(7, bearerToken.length());

				Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
				String usernameFromToken = claims.get("username", String.class);

				if (username.equalsIgnoreCase(usernameFromToken)) {
					String dbToken = sessionTokenDetailsRepository.fetchJwtTokenByUsername(username);
					if (token.equalsIgnoreCase(dbToken)) {
						return username;
					} else {
						return "";
					}
				} else {
					return "";
				}
			} else {
				return "";
			}
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			log.error("Invalid Credentials {} ",e);
		} catch (ExpiredJwtException ex) {
			log.error("Token Expired {} ", ex);
		}
		return "";
	}

	public String generateToken(String username) {

		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

			byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
			Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

			Claims claims = Jwts.claims();
			claims.put("username", username);

			JwtBuilder builder = Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + jwtExp))
					.signWith(signatureAlgorithm, signingKey);

			return builder.compact();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}



	public static void main(String[] args) {

		System.out.println("main...");
		String token = new JWTUtils().generateToken("venkatk");
		System.out.println("token : " + token);

		String returntoken = new JWTUtils().validateJwtToken("Bearer "+ token, "");
		System.out.println("returntoken : " + returntoken);
	}
}
