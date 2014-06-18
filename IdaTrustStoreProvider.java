package uk.gov.ida.shared.rest.truststore;


import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.gov.ida.shared.configuration.ClientTrustStoreConfiguration;
import uk.gov.ida.truststore.IdaTrustStore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class IdaTrustStoreProvider implements Provider<IdaTrustStore> {

    private final ClientTrustStoreConfiguration configuration;

    @Inject
    public IdaTrustStoreProvider(ClientTrustStoreConfiguration configuration) {

        this.configuration = configuration;
    }

    @Override
    public IdaTrustStore get() {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] password = configuration.getPassword().toCharArray();

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(configuration.getStoreUri());
                ks.load(inputStream, password);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw Throwables.propagate(e);
        }
        return new IdaTrustStore(ks);
    }
}
