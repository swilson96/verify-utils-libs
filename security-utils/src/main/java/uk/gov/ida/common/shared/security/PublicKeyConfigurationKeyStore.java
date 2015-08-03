package uk.gov.ida.common.shared.security;

import javax.inject.Inject;
import org.apache.commons.codec.binary.StringUtils;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PublicSigningKeyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Throwables.propagate;

public class PublicKeyConfigurationKeyStore implements InternalPublicKeyStore {

    private final PublicKeyFactory publicKeyFactory;
    private final List<PublicKeyConfiguration> publicKeyConfiguration;
    private final PublicKeyInputStreamFactory publicKeyInputStreamFactory;

    @Inject
    public PublicKeyConfigurationKeyStore(
            PublicKeyFactory publicKeyFactory,
            @PublicSigningKeyConfiguration List<PublicKeyConfiguration> publicKeyConfiguration,
            PublicKeyInputStreamFactory publicKeyInputStreamFactory) {

        this.publicKeyFactory = publicKeyFactory;
        this.publicKeyConfiguration = publicKeyConfiguration;
        this.publicKeyInputStreamFactory = publicKeyInputStreamFactory;
    }

    @Override
    public List<PublicKey> getVerifyingKeysForEntity() {
        List<PublicKey> verifyingKeys = new ArrayList<>();
        for (PublicKeyConfiguration keyConfiguration : publicKeyConfiguration) {
            verifyingKeys.add(getPublicKey(keyConfiguration.getKeyUri()));
        }
        return verifyingKeys;
    }

    private PublicKey getPublicKey(String publicKeyUri) {
        try (InputStream inputStream = publicKeyInputStreamFactory.createInputStream(publicKeyUri)) {
            String keyAsString = StringUtils.newStringUtf8(FileUtils.readStream(inputStream));
            return publicKeyFactory.createPublicKey(keyAsString);
        } catch (IOException e) {
            throw propagate(e);
        }
    }
}
