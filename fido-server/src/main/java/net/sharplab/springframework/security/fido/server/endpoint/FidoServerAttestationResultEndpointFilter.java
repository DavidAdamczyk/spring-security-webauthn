/*
 *    Copyright 2002-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.sharplab.springframework.security.fido.server.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.registry.Registry;
import com.webauthn4j.response.attestation.AttestationObject;
import com.webauthn4j.response.client.CollectedClientData;
import net.sharplab.springframework.security.fido.server.validator.ServerPublicKeyCredentialValidator;
import net.sharplab.springframework.security.webauthn.WebAuthnRegistrationRequestValidator;
import net.sharplab.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import net.sharplab.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import net.sharplab.springframework.security.webauthn.util.ExceptionUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FidoServerAttestationResultEndpointFilter extends ServerEndpointFilterBase {

    /**
     * Default name of path suffix which will validate this filter.
     */
    public static final String FILTER_URL = "/webauthn/attestation/result";

    private WebAuthnUserDetailsService webAuthnUserDetailsService;
    private AttestationObjectConverter attestationObjectConverter;
    private CollectedClientDataConverter collectedClientDataConverter;
    private WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator;
    private ServerPublicKeyCredentialValidator<ServerAuthenticatorAttestationResponse> serverPublicKeyCredentialValidator;

    private UsernameNotFoundHandler usernameNotFoundHandler = new DefaultUsernameNotFoundHandler();

    public FidoServerAttestationResultEndpointFilter(
            Registry registry,
            WebAuthnUserDetailsService webAuthnUserDetailsService,
            WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator) {
        super(FILTER_URL, registry);
        this.webAuthnUserDetailsService = webAuthnUserDetailsService;
        this.attestationObjectConverter = new AttestationObjectConverter(registry);
        this.collectedClientDataConverter = new CollectedClientDataConverter(registry);
        this.webAuthnRegistrationRequestValidator = webAuthnRegistrationRequestValidator;
        this.serverPublicKeyCredentialValidator = new ServerPublicKeyCredentialValidator<>();
    }

    @Override
    protected ServerResponse processRequest(HttpServletRequest request) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        ServerPublicKeyCredential<ServerAuthenticatorAttestationResponse> credential;
        try {
            credential = registry.getJsonMapper().readValue(request.getInputStream(),
                    new TypeReference<ServerPublicKeyCredential<ServerAuthenticatorAttestationResponse>>() {
                    });
        } catch (RuntimeException e) {
            throw ExceptionUtil.wrapWithAuthenticationException(e);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        serverPublicKeyCredentialValidator.validate(credential);
        ServerAuthenticatorAttestationResponse response = credential.getResponse();
        AttestationObject attestationObject = attestationObjectConverter.convert(response.getAttestationObject());
        CollectedClientData collectedClientData = collectedClientDataConverter.convert(response.getClientDataJSON());
        webAuthnRegistrationRequestValidator.validate(
                request,
                response.getClientDataJSON(),
                response.getAttestationObject(),
                credential.getClientExtensionResults());

        WebAuthnAuthenticator webAuthnAuthenticator =
                new WebAuthnAuthenticator(
                        "Authenticator",
                        attestationObject.getAuthenticatorData().getAttestedCredentialData(),
                        attestationObject.getAttestationStatement(),
                        attestationObject.getAuthenticatorData().getSignCount());
        String loginUsername = serverEndpointFilterUtil.decodeUsername(collectedClientData.getChallenge());
        try {
            webAuthnUserDetailsService.loadUserByUsername(loginUsername);
        } catch (UsernameNotFoundException e) {
            usernameNotFoundHandler.onUsernameNotFound(loginUsername);
        }
        webAuthnUserDetailsService.addAuthenticator(loginUsername, webAuthnAuthenticator);
        return new AttestationResultSuccessResponse();
    }

    public UsernameNotFoundHandler getUsernameNotFoundHandler() {
        return usernameNotFoundHandler;
    }

    public void setUsernameNotFoundHandler(UsernameNotFoundHandler usernameNotFoundHandler) {
        this.usernameNotFoundHandler = usernameNotFoundHandler;
    }

    private class DefaultUsernameNotFoundHandler implements UsernameNotFoundHandler {
        @Override
        public void onUsernameNotFound(String loginUsername) {
            throw new UsernameNotFoundException("Username not found");
        }
    }


}