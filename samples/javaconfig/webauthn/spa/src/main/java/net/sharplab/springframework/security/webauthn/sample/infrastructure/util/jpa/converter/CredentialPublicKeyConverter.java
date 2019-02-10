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

package net.sharplab.springframework.security.webauthn.sample.infrastructure.util.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.registry.Registry;
import com.webauthn4j.response.attestation.authenticator.CredentialPublicKey;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.UncheckedIOException;

@Converter
public class CredentialPublicKeyConverter implements AttributeConverter<CredentialPublicKey, String> {

    private ObjectMapper jsonMapper = new Registry().getJsonMapper();

    @Override
    public String convertToDatabaseColumn(CredentialPublicKey attribute) {
        try {
            return jsonMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public CredentialPublicKey convertToEntityAttribute(String dbData) {
        try {
            return jsonMapper.readValue(dbData, CredentialPublicKey.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
