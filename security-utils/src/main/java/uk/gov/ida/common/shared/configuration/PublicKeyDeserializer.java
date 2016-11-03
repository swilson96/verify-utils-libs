package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import uk.gov.ida.common.shared.security.X509CertificateFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class PublicKeyDeserializer extends JsonDeserializer<DeserializablePublicKeyConfiguration> {
    @Override
    public DeserializablePublicKeyConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Setting the Codec explicitly is needed when this executes with the YAMLParser
        // for example, when our Dropwizard apps start. The codec doesn't need to be set
        // when the JsonParser implementation is used.
        p.setCodec(new ObjectMapper());
        JsonNode node = p.getCodec().readTree(p);

        JsonNode keyUriNode = node.get("keyUri");
        Preconditions.checkState(keyUriNode != null, "keyUri not specified.");
        String keyUri = keyUriNode.asText();

        JsonNode keyNameNode = node.get("keyName");
        Preconditions.checkState(keyNameNode != null, "keyName not specified.");
        String keyName = keyNameNode.asText();

        X509CertificateFactory certificateFactory = new X509CertificateFactory();
        String cert = new String(Files.readAllBytes(Paths.get(keyUri)));
        Certificate certificate = certificateFactory.createCertificate(cert);

        PublicKey publicKey = certificate.getPublicKey();

        return new DeserializablePublicKeyConfiguration(publicKey, keyUri, keyName, cert);
    }
}
