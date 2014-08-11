package uk.gov.ida.cache;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheControlFilter implements Filter {

    public static final String CACHE_CONTROL_KEY = org.apache.http.HttpHeaders.CACHE_CONTROL; //"Cache-Control"
    public static final String CACHE_CONTROL_NO_CACHE_VALUE = "no-cache, no-store";
    public static final String PRAGMA_KEY = org.apache.http.HttpHeaders.PRAGMA;
    public static final String PRAGMA_NO_CACHE_VALUE = "no-cache";
    public static final String MAX_AGE = "max-age";

    private AssetCacheConfiguration configuration;

    public CacheControlFilter(final AssetCacheConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!isCacheableAsset(((HttpServletRequest) request).getRequestURI())) {
            setNoCacheHeaders((HttpServletRequest) request, (HttpServletResponse) response);
        } else if (!shouldCacheAssets()){
            setNoCacheHeaders((HttpServletRequest) request, (HttpServletResponse) response);
        } else {
            setCacheHeaders((HttpServletRequest) request, (HttpServletResponse) response);
        }

        // This line must be called last (a bit of a broken api)
        chain.doFilter(request, response);
    }

    private boolean shouldCacheAssets() {
        return configuration.shouldCacheAssets();
    }

    private void setCacheHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_KEY, MAX_AGE + "=" + configuration.getAssetsCacheDuration());
    }

    private void setNoCacheHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        response.setHeader(CACHE_CONTROL_KEY, CACHE_CONTROL_NO_CACHE_VALUE);
        response.setHeader(PRAGMA_KEY, PRAGMA_NO_CACHE_VALUE);
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    public boolean isCacheableAsset(final String localAddr) {
        return localAddr.contains("/assets/fonts/") || localAddr.contains("/assets/images/");
    }
}