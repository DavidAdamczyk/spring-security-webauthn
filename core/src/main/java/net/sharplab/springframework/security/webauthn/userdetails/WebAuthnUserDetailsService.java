/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sharplab.springframework.security.webauthn.userdetails;

import net.sharplab.springframework.security.webauthn.authenticator.WebAuthnAuthenticator;
import net.sharplab.springframework.security.webauthn.exception.CredentialIdNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface WebAuthnUserDetailsService extends UserDetailsService {

    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    WebAuthnUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Locates a user based on a credentialId.
     *
     * @param credentialId credentialId
     * @return fully populated {@link WebAuthnUserDetails} instance (never <code>null</code>),
     * which must returns the authenticator in getAuthenticators result.
     * @throws CredentialIdNotFoundException if the authenticator could not be found
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    WebAuthnUserDetails loadUserByCredentialId(byte[] credentialId) throws CredentialIdNotFoundException;

    void addAuthenticator(String username, WebAuthnAuthenticator authenticator);

    void removeAuthenticator(String username, WebAuthnAuthenticator authenticator);

    void removeAuthenticator(String username, byte[] credentialId);
}
