package uk.gov.ida.common.shared.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyStore;

import javax.crypto.Mac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Mac.class)
public class HmacDigestTest {

    @Mock
    private HmacDigest.HmacSha256MacFactory macFactory;

    @Mock
    private Mac mac;

    @Mock
    private SecureCookieKeyStore secureCookieKeyStore;

    @Test
    public void digest_shouldDDigestAValueUsingHmac() throws Exception {
        when(macFactory.getInstance()).thenReturn(mac);
        when(mac.doFinal(any(byte[].class))).thenReturn("Hello".getBytes("UTF-8"));

        HmacDigest digest = new HmacDigest(macFactory, secureCookieKeyStore);

        String result = digest.digest("string to be encoded");
        assertThat(result).isEqualTo("SGVsbG8=");

        verify(mac).init(secureCookieKeyStore.getKey());
    }
}
