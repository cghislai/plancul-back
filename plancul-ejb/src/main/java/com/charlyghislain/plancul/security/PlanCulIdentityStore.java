package com.charlyghislain.plancul.security;

import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.security.Group;
import com.charlyghislain.plancul.security.exception.JwtValidationException;
import com.charlyghislain.plancul.service.SecurityService;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlanCulIdentityStore implements IdentityStore {

    @EJB
    private SecurityService securityService;
    @Inject
    private JwtService jwtService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            return this.validateUserNamePassword(usernamePasswordCredential);
        } else if (credential instanceof JwtTokenCredential) {
            JwtTokenCredential tokenCredential = (JwtTokenCredential) credential;
            return this.validateJwtToken(tokenCredential);
        } else {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
    }


    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return null;
    }


    private CredentialValidationResult validateUserNamePassword(UsernamePasswordCredential usernamePasswordCredential) {
        String callerName = usernamePasswordCredential.getCaller();
        Password password = usernamePasswordCredential.getPassword();
        return securityService.findCallerByName(callerName)
                .map(caller -> this.validateCallerPassword(caller, password))
                .orElse(CredentialValidationResult.INVALID_RESULT);
    }

    private CredentialValidationResult validateCallerPassword(Caller caller, Password providedPassword) {
        boolean passwordValid = securityService.isValidCallerPassword(caller, providedPassword.getValue());
        if (passwordValid) {
            return createValidCallerResult(caller);
        } else {
            return CredentialValidationResult.INVALID_RESULT;
        }
    }

    private CredentialValidationResult createValidCallerResult(Caller caller) {
        Set<String> callerGroups = securityService.findCallerGroups(caller)
                .stream()
                .map(Group::name)
                .collect(Collectors.toSet());
        String callerName = caller.getName();
        CredentialValidationResult validationResult = new CredentialValidationResult(callerName, callerGroups);
        return validationResult;
    }


    private CredentialValidationResult validateJwtToken(JwtTokenCredential tokenCredential) {
        try {
            JwtClaims jwtClaims = jwtService.validateToken(tokenCredential.getToken());
            String callerName = jwtClaims.getSubject();
            return securityService.findCallerByName(callerName)
                    .map(this::createValidCallerResult)
                    .orElse(CredentialValidationResult.INVALID_RESULT);
        } catch (JwtValidationException | MalformedClaimException e) {
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
}
