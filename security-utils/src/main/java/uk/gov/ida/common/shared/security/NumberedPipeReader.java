package uk.gov.ida.common.shared.security;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import uk.gov.ida.common.shared.security.exceptions.KeyLoadingException;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

import static sun.misc.SharedSecrets.getJavaIOFileDescriptorAccess;

public class NumberedPipeReader {

    private final PrivateKeyFactory privateKeyFactory;

    public NumberedPipeReader(PrivateKeyFactory privateKeyFactory) {
        this.privateKeyFactory = privateKeyFactory;
    }

    public PrivateKey readKey(int fileDescriptorNumber) {
        FileDescriptor fileDescriptor = new FileDescriptor();
        getJavaIOFileDescriptorAccess().set(fileDescriptor, fileDescriptorNumber);
        InputStream fileInputStream = new FileInputStream(fileDescriptor);
        try {
            if (fileInputStream.available() == 0)
                throw new KeyLoadingException("Key not loaded: No data found at file descriptor");

            byte[] cert = ByteStreams.toByteArray(fileInputStream);

            return privateKeyFactory.createPrivateKey(cert);
        } catch (IOException e) {
            throw new KeyLoadingException(fileDescriptorNumber, e);
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
    }
}
