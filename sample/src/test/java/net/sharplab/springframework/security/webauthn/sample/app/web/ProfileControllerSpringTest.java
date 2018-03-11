package net.sharplab.springframework.security.webauthn.sample.app.web;

import net.sharplab.springframework.security.webauthn.sample.app.config.AppConfig;
import net.sharplab.springframework.security.webauthn.sample.app.config.TestSecurityConfig;
import net.sharplab.springframework.security.webauthn.sample.app.test.WithMockUser;
import net.sharplab.springframework.security.webauthn.sample.domain.model.User;
import net.sharplab.springframework.security.webauthn.sample.domain.service.ProfileService;
import net.sharplab.springframework.security.webauthn.sample.infrastructure.config.InfrastructureMockConfig;
import net.sharplab.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for UserController
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProfileController.class)
@Import(value = {TestSecurityConfig.class, AppConfig.class, InfrastructureMockConfig.class})
public class ProfileControllerSpringTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    ProfileService profileService;

    @MockBean(name = "userDetailsService")
    WebAuthnUserDetailsService userDetailsService;

    @Test
    @WithMockUser(id=1, firstName = "John", lastName = "Doe", emailAddress = "john.doe@example.com", authorities = {"ROLE_USER"}, authenticators = {})
    public void read_test1() throws Exception{
        int userId = 1;

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("john.doe@example.com");
        user.setAuthenticators(Collections.emptyList());
        user.setPasswordAuthenticationAllowed(true);

        when(profileService.findOne(userId)).thenReturn(user);

        ProfileUpdateForm profileUpdateForm = new ProfileUpdateForm();
        profileUpdateForm.setFirstName("John");
        profileUpdateForm.setLastName("Doe");
        profileUpdateForm.setEmailAddress("john.doe@example.com");
        profileUpdateForm.setAuthenticators(Collections.emptyList());
        profileUpdateForm.setPasswordAuthenticationAllowed(true);

        //When
        mvc
                .perform(get("/profile/"))
                //Then
                .andExpect(status().isOk())
                .andExpect(model().attribute("profileForm", samePropertyValuesAs(profileUpdateForm)));
    }





}
