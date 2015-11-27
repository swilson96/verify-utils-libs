package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import uk.gov.ida.common.shared.security.PrivateKeyFactory;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;

public class PrivateKeyDeserializer extends JsonDeserializer<PrivateKeyConfiguration> {
    @Override
    public PrivateKeyConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        PrivateKeyFactory privateKeyFactory = new PrivateKeyFactory();
        p.setCodec(new ObjectMapper());
        JsonNode node = p.getCodec().readTree(p);
        JsonNode privateKeyNode = node.get("keyUri");
        if (null==privateKeyNode) {
            throw new PrivateKeyPathNotSpecifiedException("keyUri not specified.");
        }
        String keyUri = privateKeyNode.asText();
        PrivateKey privateKey = privateKeyFactory.createPrivateKey(Files.toByteArray(new File(keyUri)));
        return new PrivateKeyConfiguration(privateKey, keyUri);
    }

    class PrivateKeyPathNotSpecifiedException extends JsonProcessingException {
        protected PrivateKeyPathNotSpecifiedException(String msg) {
            super(msg);
        }
    }
}
