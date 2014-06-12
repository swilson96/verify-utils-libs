package uk.gov.ida.common.shared.security;

import com.google.inject.Inject;
import org.apache.commons.codec.binary.StringUtils;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PublicSigningKeyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.List;

import static com.google.common.base.Throwables.propagate;
import static java.util.Arrays.asList;

public class PublicKeyConfigurationKeyStore implements InternalPublicKeyStore {

    private final PublicKeyFactory publicKeyFactory;
    private final PublicKeyConfiguration publicKeyConfiguration;
    private final PublicKeyInputStreamFactory publicKeyInputStreamFactory;

    @Inject
    public PublicKeyConfigurationKeyStore(
            PublicKeyFactory publicKeyFactory,
            @PublicSigningKeyConfiguration PublicKeyConfiguration publicKeyConfiguration,
            PublicKeyInputStreamFactory publicKeyInputStreamFactory) {

        this.publicKeyFactory = publicKeyFactory;
        this.publicKeyConfiguration = publicKeyConfiguration;
        this.publicKeyInputStreamFactory = publicKeyInputStreamFactory;
    }

    @Override
    public List<PublicKey> getVerifyingKeysForEntity() {
        String publicKeyUri = publicKeyConfiguration.getKeyUri();
        return asList(getPublicKey(publicKeyUri));
    }

    private PublicKey getPublicKey(String publicKeyUri) {
        try (InputStream inputStream = publicKeyInputStreamFactory.createInputStream(publicKeyUri)) {
            String keyAsString = StringUtils.newStringUtf8(FileUtils.readStream(inputStream));
            return publicKeyFactory.createPublicKey(keyAsString);
        } catch (IOException | CertificateException e) {
            throw propagate(e);
        }
    }
}
