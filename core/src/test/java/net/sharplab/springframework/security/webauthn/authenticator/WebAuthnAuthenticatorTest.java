package net.sharplab.springframework.security.webauthn.authenticator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WebAuthnAuthenticatorTest {

    @Test
    public void equals_hashCode_test(){
        WebAuthnAuthenticator instanceA = new WebAuthnAuthenticator("authenticator");
        WebAuthnAuthenticator instanceB = new WebAuthnAuthenticator("authenticator");
        assertThat(instanceA).isEqualTo(instanceB);
        assertThat(instanceA).hasSameHashCodeAs(instanceB);
    }
}
