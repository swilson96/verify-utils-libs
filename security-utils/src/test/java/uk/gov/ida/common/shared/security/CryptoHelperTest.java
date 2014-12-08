package uk.gov.ida.common.shared.security;

import com.google.common.base.Optional;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoHelperTest extends TestCase {

    static final String EXAMPLE_IDP = "http://example.com/idp";
    static final int KEY_LENGTH_IN_BITS = 128;
    static final String KEY = base64(new byte[KEY_LENGTH_IN_BITS / 8]);
    private CryptoHelper cryptoHelper;

    private static String base64(byte[] data) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
    }

    private static byte[] unbase64(String data) {
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(data);
    }

    @BeforeClass
    public void setUp() throws Exception {
        cryptoHelper = new CryptoHelper(KEY);
    }

    @Test
    public void testInitializationVectorIsCorrectLength() {
        assertEquals(cryptoHelper.getInitializationVector().length, 16);
    }

    @Test
    public void testShouldDecryptToTheOriginalValue() {
        assertEquals(EXAMPLE_IDP,
                cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()).get());
    }

    @Test
    public void testMultipleEncryptionsOfSameIDPEntityIDResultInDifferentValues() {
        assertNotSame(
                cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get(),
                cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()
        );
    }

    @Test
    public void testEncryptedDataShouldNotContainUnencryptedData() {
        final String encrypted = unbase64(cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get()).toString();
        assertFalse(encrypted.contains(EXAMPLE_IDP));
    }

    @Test
    public void testOrderOfEncryptedNotImportantByDecryptngInADifferentOrder() {
        final int count = 100;
        Map<String, String> encryptedValues = new HashMap<>();
        for (int i=0; i<count; i++) {
            String idpEntityId = EXAMPLE_IDP + "/" + i;
            encryptedValues.put(idpEntityId, cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(idpEntityId).get());
        }
        List<Object> shuffledKeys = new ArrayList(encryptedValues.keySet());
        Collections.shuffle(shuffledKeys);
        for(Object key:shuffledKeys) {
            assertEquals(key, cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValues.get(key)).get());
        }

    }

    @Test
    public void testEncryptedValuesShouldDecryptRepeatedly() {
        String encryptedValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get();
        for (int i=0; i<100; i++) {
            assertEquals(EXAMPLE_IDP, cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValue).get());
        }
     }

    @Test
    public void testEncryptedValuesShouldNotDecryptIfIVIsChanged() {
        String encryptedValue = cryptoHelper.encrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(EXAMPLE_IDP).get();
        CryptoHelper alteredCryptoHelper = new CryptoHelper(KEY) {
            @Override
            protected byte[] getInitializationVector() {
                 return new byte[] {
                        0x01, 0x01, 0x01, 0x01,
                        0x01, 0x01, 0x01, 0x01,
                        0x01, 0x01, 0x01, 0x01,
                        0x01, 0x01, 0x01, 0x01};
                    }
            };
        Optional<String> originalDecrypt = cryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValue);
        Optional<String> alteredDecrypt = alteredCryptoHelper.decrypt_yesIKnowThisCryptoCodeHasNotBeenAudited(encryptedValue);
        fail();
    }
//
//
//    // encrypted values should not decrypt if key is changed
//    def newKey = base64([1] * (KEY_LENGTH_IN_BITS/8) as byte[])
//    def g = new AesEncryptionThingy(newKey, iv, NONCE_LENGTH)
//    try {
//        assert g.decrypt(e.encrypt(EXAMPLE_IDP)) != EXAMPLE_IDP
//    } catch(BadPaddingException _) { /* expected */ }
//
//    // encrypted values should not decrypt if IV is changed
//    def newIv = base64([1] * (IV_LENGTH_IN_BITS/8) as byte[])
//    def f = new AesEncryptionThingy(key, newIv, NONCE_LENGTH)
//    assert f.decrypt(e.encrypt(EXAMPLE_IDP)) != EXAMPLE_IDP
//
//// big nonce should not be allowed
//    try {
//        new AesEncryptionThingy(key, iv, 16)
//        fail("Should have failed due to nonce length 16.");
//    } catch(IllegalArgumentException _) { /* expected */ }
//
//// zero nonce should not be allowed
//    try {
//        new AesEncryptionThingy(key, iv, 0)
//        fail("Should have failed due to nonce length 0.");
//    } catch(IllegalArgumentException _) { /* expected */ }
//
//// negative nonce should not be allowed
//    try {
//        new AesEncryptionThingy(key, iv, -1)
//        fail("Should have failed due to nonce length -1.");
//    } catch(IllegalArgumentException _) { /* expected */ }
//
//// really short entityId should encrypt to same length as really long entityId
//    assert debase64(enc("h")).length ==
//    debase64(enc("http://example.com/something/incredibly/long/which/wont/be/beaten/by/a/real/life/entityId/like_______________________________this")).length
//
//// really long entityIds should not be accepted
//    try {
//        enc("h" * 2000)
//        fail("Should have failed due to very long idpEntityId");
//    } catch(IllegalArgumentException _) { /* expected */ }
//
//    println "-----"
//    println "TESTS PASSED."
//    println "-----"
}