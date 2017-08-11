package com.df.zuulsvr.filters;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

@Component
public class ResponseFilter extends ZuulFilter {
  private static final int FILTER_ORDER = 1;
  private static final boolean SHOULD_FILTER = true;

  private final Tracer tracer;

  @Autowired
  public ResponseFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public String filterType() {
    return "post";
  }

  @Override
  public int filterOrder() {
    return FILTER_ORDER;
  }

  @Override
  public boolean shouldFilter() {
    return SHOULD_FILTER;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    ctx.getResponse().addHeader("df-correlation-id", tracer.getCurrentSpan().traceIdString());

    return null;
  }
}
