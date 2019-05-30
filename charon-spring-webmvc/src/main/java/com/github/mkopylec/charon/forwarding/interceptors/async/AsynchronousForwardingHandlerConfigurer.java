package com.github.mkopylec.charon.forwarding.interceptors.async;

import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorConfigurer;

public class AsynchronousForwardingHandlerConfigurer extends RequestForwardingInterceptorConfigurer<AsynchronousForwardingHandler> {

    private AsynchronousForwardingHandlerConfigurer() {
        super(new AsynchronousForwardingHandler());
    }

    public static AsynchronousForwardingHandlerConfigurer asynchronousForwardingHandler() {
        return new AsynchronousForwardingHandlerConfigurer();
    }

    public AsynchronousForwardingHandlerConfigurer set(ThreadPoolConfigurer threadPoolConfigurer) {
        configuredObject.setThreadPool(threadPoolConfigurer.configure());
        return this;
    }
}