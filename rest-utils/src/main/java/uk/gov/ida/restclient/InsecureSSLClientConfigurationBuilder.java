package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.util.HashMap;

public abstract class InsecureSSLClientConfigurationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InsecureSSLClientConfigurationBuilder.class);

    public static InsecureSSLClientConfiguration aConfigWithInsecureSSLSchemeRegistry(
            SSLContext sslContext){

        final TrustManager[] trustManagers = getTrustManagers();
        try {
            sslContext.init(null, trustManagers, null);
        } catch (KeyManagementException e){
            LOG.error("Error when trying to create SSL.", e);
            throw Throwables.propagate(e);
        }

        final Scheme http = new Scheme("http", 80, new PlainSocketFactory());
        final Scheme https = new Scheme("https", 443, new SSLSocketFactory(sslContext, new AllowAllHostnameVerifier()));
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(http);
        schemeRegistry.register(https);

        return new InsecureSSLClientConfiguration(schemeRegistry, new HashMap<String, Object>());
    }

    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new InsecureTrustManager()};
    }
}
