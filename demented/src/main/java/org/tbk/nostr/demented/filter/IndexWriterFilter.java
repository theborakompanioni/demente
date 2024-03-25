package org.tbk.nostr.demented.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.tbk.nostr.relay.utils.MoreHttpRequests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static org.tbk.nostr.relay.utils.MoreHttpRequests.*;


public class IndexWriterFilter implements Filter {

    private final byte[] content;

    public IndexWriterFilter(ClassPathResource resource) throws IOException {
        this.content = requireNonNull(resource).getContentAsByteArray();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest &&
            servletResponse instanceof HttpServletResponse httpServletResponse) {
            if (isIndexHtmlRequest(httpServletRequest)) {
                writeIndexHtml(httpServletResponse);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void writeIndexHtml(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    private boolean isIndexHtmlRequest(HttpServletRequest request) {
        boolean isGetRequest = HttpMethod.GET.name().equalsIgnoreCase(request.getMethod());
        if (!isGetRequest) {
            return false;
        }
        boolean isIndexPath = requestUriMatches(request, "/") || requestUriMatches(request, "/index.html");
        if (!isIndexPath) {
            return false;
        }
        if (isWebSocketHandshakeRequest(request)) {
            return false;
        }
        boolean hasExpectedHeader = hasAcceptHeaderWithValue(request, MediaType.TEXT_HTML_VALUE);
        if (!hasExpectedHeader) {
            return false;
        }

        return true;
    }

    private boolean hasAcceptHeaderWithValue(HttpServletRequest request, String requiredValue) {
        return headerMatches(request, HttpHeaders.ACCEPT, it -> Arrays.stream(it.split(","))
                .anyMatch(val -> val.equalsIgnoreCase(requiredValue)));
    }
}
