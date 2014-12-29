package uk.gov.ida.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class AcceptLanguageFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new HideAcceptLanguage((HttpServletRequest) servletRequest), servletResponse);
    }

    @Override
    public void destroy() {
    }

    private class HideAcceptLanguage extends HttpServletRequestWrapper {

        public HideAcceptLanguage(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if (name.equals(HttpHeaders.ACCEPT_LANGUAGE)) {
                return null;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            ArrayList<String> headers =  Collections.list(super.getHeaderNames());
            headers.remove(HttpHeaders.ACCEPT_LANGUAGE);
            return Collections.enumeration(headers);
        }
    }
}