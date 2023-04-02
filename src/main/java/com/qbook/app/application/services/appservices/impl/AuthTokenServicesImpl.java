package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.MissingTokenException;
import com.qbook.app.application.configuration.exception.NotAuthorisedException;
import com.qbook.app.application.configuration.exception.TokenExpirationException;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.User;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Component
@AllArgsConstructor
public class AuthTokenServicesImpl implements AuthTokenServices {
	private final ClientRepository clientRepository;
	private final EmployeeRepository employeeRepository;
	@Override
	public String extractUserId(String authToken) {
		if(authToken.startsWith("Bearer")){
			authToken = authToken.substring(7);
		}
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")).parseClaimsJws(authToken);

			return claims.getBody().getAudience();
		} catch (Exception e) {
			log.log(Level.WARNING, "Access Denied. No Authorization Token Provided.", e);
			throw new MissingTokenException("No Authorization Token Provided");
		}
	}

	@Override
	public String extractUserIdForReset(String resetToken) {
		if(resetToken.startsWith("Bearer")){
			resetToken = resetToken.substring(7);
		}
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")).parseClaimsJws(resetToken);

			Date expirationDate = claims.getBody().getExpiration();
			DateTime timeInMillis = new DateTime(expirationDate.getTime());

			if(timeInMillis.isBeforeNow()){
				throw new TokenExpirationException("The url used has expired, please try again to reset your password. If you are having trouble please contact our support team.");
			}

			return claims.getBody().getAudience();
		} catch (Exception e) {
			log.log(Level.WARNING, "Access Denied. No Authorization Token Provided.", e);
			throw new MissingTokenException("No Authorization Token Provided");
		}
	}

	@Override
	public String generateAuthToken(String id) {
		DateTime expiration = new DateTime().plusDays(365);
		return Jwts.builder()
				.setSubject("AuthToken")
				.setAudience(id)
				.setIssuedAt(new DateTime().toDate())
				.setExpiration(expiration.toDate())
				.setIssuer("qbook_api")
				.signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="))
				.compact();
	}

	@Override
	public String generateResetToken(String id) {
		DateTime expiration = new DateTime().plusHours(1);
		return Jwts.builder()
				.setSubject("ResetToken")
				.setAudience(id)
				.setIssuedAt(new DateTime().toDate())
				.setExpiration(expiration.toDate())
				.setIssuer("qbook_api")
				.signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="))
				.compact();
	}
//.substring(7)
	@Override
	public User extractUser(String authToken) {
		try {

			Jws<Claims> claims = Jwts.parser().setSigningKey(TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")).parseClaimsJws(authToken);

			Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(claims.getBody().getAudience()));
			Optional<Client> clientOptional = clientRepository.findById(new ObjectId(claims.getBody().getAudience()));
			if (employeeOptional.isPresent()) {
				if(!employeeOptional.get().isActive()) {
					throw new NotAuthorisedException("Your account has been disabled. Please contact the company to find out why.");
				} else {
					return employeeOptional.get();
				}
			} else if(clientOptional.isPresent()) {
				if(!clientOptional.get().isActive()) {
					throw new NotAuthorisedException("Your account has been disabled. Please contact the company to find out why.");
				} else {
					return clientOptional.get();
				}
			} else {
				throw new MissingTokenException("No Authorization Token Provided");
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Access Denied. No Authorization Token Provided.", e);
			throw new MissingTokenException("No Authorization Token Provided");
		}
	}
}
