package com.arvent.zuul.filter;

import com.google.common.io.CharStreams;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs"))
        {
            return false;
        }

        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        List<Pair<String, String>> headers = ctx.getZuulResponseHeaders();
        //HttpServletRequest request = ctx.getRequest();
        //String headerString = headers.stream().map(e -> e.second()).reduce(" ", String::concat);
        //log.info("input url {}", request.getRequestURI());
        String headerString="";
        for (Pair header:headers
             ) {
            headerString = headerString.concat(header.first() + ": "+ header.second()+" ");
        }
        //log.info("Exposed Headers " +"{"+"{}"+"}", headerString);
        /*
        log.info("input url {} \n " +
                ",content-type " +"{"+"{}"+"}",request.getRequestURI(), headerString);
        */
        HttpServletResponse response = ctx.getResponse();
        /*
        if (response.getHeader("Authorization") != null) {
            log.info(response.getHeader("Authorization"));
        }*/

        log.info("Response status {}", response.getStatus());

        try(InputStream is = ctx.getResponseDataStream())
        {
            String respData = CharStreams.toString(new InputStreamReader(is, CharEncoding.UTF_8));
            if(respData.isEmpty()||respData.contains("<html>"))
            {
                ctx.setResponseBody(respData);
                return null;
            }
            log.info("Response Data = {}",respData);
            ctx.setResponseBody(respData);
        }catch(IOException ex)
        {
            log.info(ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }
}