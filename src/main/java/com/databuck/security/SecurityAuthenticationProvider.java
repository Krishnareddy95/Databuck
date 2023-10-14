/*package com.databuck.security;

import org.omg.CORBA.UnknownUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.aadhaar.entity.AppUser;
import com.aadhaar.repo.AppUserRepository;

@Component
public class SecurityAuthenticationProvider implements AuthenticationProvider {

	private final AppUserRepository appUserRepository;

	@Autowired
	public SecurityAuthenticationProvider(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		DemoAuthenticationToken demoAuthentication = (DemoAuthenticationToken) authentication;
		AppUser user = appUserRepository.findOne(demoAuthentication.getUid());

		if (user == null) {
			try {
				throw new UnknownUserException();
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return user;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return DemoAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
*/