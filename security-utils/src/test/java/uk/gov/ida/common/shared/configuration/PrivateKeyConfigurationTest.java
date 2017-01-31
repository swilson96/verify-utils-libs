package uk.gov.ida.common.shared.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;

public class PrivateKeyConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_loadPrivateKeyFromJSON() throws Exception {
        String path = getClass().getClassLoader().getResource("private_key.pk8").getPath();
        PrivateKeyConfiguration privateKeyConfiguration = objectMapper.readValue("{\"keyFile\": \"" + path + "\"}", PrivateKeyConfiguration.class);

        assertThat(privateKeyConfiguration.getPrivateKey().getAlgorithm()).isEqualTo("RSA");
    }

    @Test(expected = FileNotFoundException.class)
    public void should_ThrowFooExceptionWhenFileDoesNotExist() throws Exception {
        objectMapper.readValue("{\"keyFile\": \"/foo/bar\"}", PrivateKeyConfiguration.class);
    }

    @Test
    public void should_ThrowFooExceptionWhenFileDoesNotContainAPrivateKey() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectCause(any(InvalidKeySpecException.class));

        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"keyFile\": \"" + path + "\"}", PrivateKeyConfiguration.class);
    }

    @Test(expected = PrivateKeyDeserializer.PrivateKeyPathNotSpecifiedException.class)
    public void should_throwAnExceptionWhenIncorrectKeySpecified() throws Exception {
        String path = getClass().getClassLoader().getResource("empty_file").getPath();
        objectMapper.readValue("{\"privateKeyFoo\": \"" + path + "\"}", PrivateKeyConfiguration.class);
    }
}
