package uk.gov.ida.common.shared.security;

import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.apache.commons.codec.binary.StringUtils.getBytesUtf8;
import static org.apache.commons.codec.binary.StringUtils.newStringUtf8;

abstract class StringEncoding {

    public static String toBase64Encoded(byte[] bytes) {
        return newStringUtf8(Base64.encodeBase64(bytes));
    }
}
