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

package net.sharplab.springframework.security.webauthn.sample.infrastructure.config;

import net.sharplab.springframework.security.webauthn.sample.domain.repository.AuthenticatorEntityRepository;
import net.sharplab.springframework.security.webauthn.sample.domain.repository.AuthorityEntityRepository;
import net.sharplab.springframework.security.webauthn.sample.domain.repository.GroupEntityRepository;
import net.sharplab.springframework.security.webauthn.sample.domain.repository.UserEntityRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaMockConfig {

    @MockBean
    public UserEntityRepository userEntityRepository;

    @MockBean
    public GroupEntityRepository groupEntityRepository;

    @MockBean
    public AuthorityEntityRepository authorityEntityRepository;

    @MockBean
    public AuthenticatorEntityRepository authenticatorEntityRepository;

}
