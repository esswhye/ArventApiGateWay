package com.arvent.zuul.filter;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class LogPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        HttpServletRequest request = ctx.getRequest();
        log.info("input url {} ,content-type {}",request.getRequestURI(),request.getContentType());
        HttpServletResponse response = ctx.getResponse();
        log.info("Response status {}", response.getStatus());
        /*
        try(InputStream is = ctx.getResponseDataStream())
        {
            String respData = CharStreams.toString(new InputStreamReader(is, CharEncoding.UTF_8));
            log.info("Response Data={}",respData);
            ctx.setResponseBody(respData);
        }catch(IOException ex)
        {
            log.info(ex.getMessage());
            ex.printStackTrace();
        }
        */
        return null;
    }
}