package com.github.mkopylec.charon.forwarding.interceptors.latency;

import java.time.Duration;

import com.github.mkopylec.charon.forwarding.interceptors.HttpRequest;
import com.github.mkopylec.charon.forwarding.interceptors.HttpRequestExecution;
import com.github.mkopylec.charon.forwarding.interceptors.HttpResponse;
import com.github.mkopylec.charon.forwarding.interceptors.MetricsUtils;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;

import static com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType.LATENCY_METER;
import static java.lang.System.nanoTime;
import static java.time.Duration.ofNanos;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.Assert.notNull;

class LatencyMeter implements RequestForwardingInterceptor {

    private static final Logger log = getLogger(LatencyMeter.class);

    private boolean enabled;
    private MeterRegistry meterRegistry;

    LatencyMeter(MeterRegistry meterRegistry) {
        enabled = true;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public HttpResponse forward(HttpRequest request, HttpRequestExecution execution) {
        if (!enabled) {
            return execution.execute(request);
        }
        log.trace("[Start] Collect metrics of '{}' request mapping", execution.getMappingName());
        long startingTime = nanoTime();
        try {
            return execution.execute(request);
        } finally {
            captureLatencyMetric(execution.getMappingName(), startingTime);
            log.trace("[End] Collect metrics of '{}' request mapping", execution.getMappingName());
        }
    }

    @Override
    public void validate() {
        notNull(meterRegistry, "No meter registry set");
    }

    @Override
    public int getOrder() {
        return LATENCY_METER.getOrder();
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void captureLatencyMetric(String mappingName, long startingTime) {
        String metricName = MetricsUtils.metricName(mappingName, "latency");
        Duration responseTime = ofNanos(nanoTime() - startingTime);
        meterRegistry.timer(metricName).record(responseTime);
    }
}