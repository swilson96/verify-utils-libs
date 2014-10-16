package uk.gov.ida.truststore;


import com.google.common.base.Throwables;
import com.google.inject.Provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class EmptyKeyStoreProvider implements Provider<KeyStore> {

    @Override
    public KeyStore get() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }
}
