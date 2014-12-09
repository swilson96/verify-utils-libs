package uk.gov.ida.common.shared.security;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static java.text.MessageFormat.format;

/**
 * The initial purpose of this code was to obfuscate cookies in Frontend.
 * It has not been audited by a cryptographer.
 *
 * The length of data it will encrypt is PADDED_LENGTH bytes and
 * that should be changed/paramaterised if this code is reused.  The data
 * that is encrypted is padded to PADDED_LENGTH so the length of the
 * resulting encrypted data does not allow anyone to draw inferences about
 * the unencrypted data.
 *
 */
public class CryptoHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoHelper.class);

    /**
     * the length in bytes of the nonce to be used for salting each value
     */
    static final int NONCE_AND_IV_LENGTH = 16;

    private static final String UTF8 = "UTF-8";
    private static final String CIPHER_SUITE = "AES/CBC/PKCS5Padding";
    private static final int PADDED_LENGTH = 512 + NONCE_AND_IV_LENGTH;

    private final SecretKeySpec aesKey;
    private final SecureRandom random;
    private IvParameterSpec iv;

    /**
     * This helper is intended to obfuscate cookies relating to relying parties
     */
    public CryptoHelper(String base64EncodedAesKey) {
        this.aesKey = new SecretKeySpec(unBase64(base64EncodedAesKey), "AES");
        this.random = new SecureRandom();

        byte[] ivBytes = getInitializationVector();
        this.iv = new IvParameterSpec(ivBytes);
    }

    public Optional<String> encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(String idpEntityId) {
        byte[] idpEntityIdAsByteArray;
        try {
            idpEntityIdAsByteArray = bytes(idpEntityId);
        } catch (UnsupportedEncodingException e) {
            //fixme: log
            return Optional.absent();
        }
        byte[] decryptedIdpNameWithNonce = addNonceAndPadding(idpEntityIdAsByteArray);
        byte[] encryptedIdpNameWithNonce;
        try {
            encryptedIdpNameWithNonce = encrypt(decryptedIdpNameWithNonce);
        } catch (GeneralSecurityException e) {
            //fixme: log
            return Optional.absent();
        }
        return Optional.of(base64(encryptedIdpNameWithNonce));
    }

    public Optional<String> decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(String base64EncodedEncryptedIdpNameWithNonce) {
        byte[] encryptedIdpNameWithNonce = unBase64(base64EncodedEncryptedIdpNameWithNonce);
        byte[] decryptedIdpNameWithNonce;
        try {
            decryptedIdpNameWithNonce = decrypt(encryptedIdpNameWithNonce);
        } catch (BadPaddingException e) {
            LOG.warn(format("BadPadding (possibly incorrect key) trying to decrypt message: {0}", encryptedIdpNameWithNonce));
            return Optional.absent();
        } catch (InvalidKeyException e) {
            LOG.warn(format("Key is invalid for message: {0}", encryptedIdpNameWithNonce));
            return Optional.absent();
        } catch (GeneralSecurityException e) {
            LOG.warn(format("Failed to decrypt message: {0}", encryptedIdpNameWithNonce));
            return Optional.absent();
        }
        byte[] idpEntityIdAsByteArray = removeNonceAndPadding(decryptedIdpNameWithNonce);
        try {
            return Optional.of(string(idpEntityIdAsByteArray));
        } catch (UnsupportedEncodingException e) {
            //fixme: log
            return Optional.absent();
        }
    }

    private byte[] unBase64(String data) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(data);
    }

    private String base64(byte[] data) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
    }

    private byte[] bytes(String string) throws UnsupportedEncodingException {
        return string.getBytes(UTF8);
    }

    private String string(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, UTF8);
    }

    private byte[] addNonceAndPadding(byte[] withoutNonce) {
        int lenWithNonce = withoutNonce.length + NONCE_AND_IV_LENGTH;
        if(lenWithNonce > PADDED_LENGTH) {
            throw new IllegalArgumentException("That's a very long IDP entityId!");
        }

        byte[] withNonceAndPadding = new byte[PADDED_LENGTH];
        System.arraycopy(newNonce(), 0, withNonceAndPadding, 0, NONCE_AND_IV_LENGTH);
        System.arraycopy(withoutNonce, 0, withNonceAndPadding, NONCE_AND_IV_LENGTH, withoutNonce.length);

        return withNonceAndPadding;
    }

    private byte[] removeNonceAndPadding(byte[] withNonce) {
        int paddingStart;
        for(paddingStart= NONCE_AND_IV_LENGTH; paddingStart<withNonce.length; ++paddingStart) {
            if(withNonce[paddingStart] == 0) break;
        }
        int lenWithoutNonceOrPadding = paddingStart - NONCE_AND_IV_LENGTH;

        byte[] withoutNonceOrPadding = new byte[lenWithoutNonceOrPadding];
        System.arraycopy(withNonce, NONCE_AND_IV_LENGTH, withoutNonceOrPadding, 0, withoutNonceOrPadding.length);

        return withoutNonceOrPadding;
    }

    private byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER_SUITE);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
        return cipher.doFinal(plaintext);
    }

    private byte[] decrypt(byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(CIPHER_SUITE);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
        return cipher.doFinal(ciphertext);
    }

    private byte[] getInitializationVector() {
        return newNonce();
    }

    private byte[] newNonce() {
        byte[] nonce = new byte[NONCE_AND_IV_LENGTH];
        random.nextBytes(nonce);
        return nonce;
    }

}