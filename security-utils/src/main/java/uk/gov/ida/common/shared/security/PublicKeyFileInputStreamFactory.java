package uk.gov.ida.common.shared.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.google.common.base.Throwables.propagate;

public class PublicKeyFileInputStreamFactory implements PublicKeyInputStreamFactory {

    public InputStream createInputStream(String publicKeyUri) {
        try {
            return new FileInputStream(new File(publicKeyUri));
        } catch (FileNotFoundException e) {
            throw propagate(e);
        }
    }
}
