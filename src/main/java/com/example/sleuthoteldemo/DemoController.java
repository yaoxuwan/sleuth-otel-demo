package com.example.sleuthoteldemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.async.TraceRunnable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping
public class DemoController {

    private final static Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    private final Executor executor = new ThreadPoolExecutor(1, 2, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    private final Tracer tracer;

    private final SpanNamer spanNamer;


    public DemoController(Tracer tracer, SpanNamer spanNamer) {
        this.tracer = tracer;
        this.spanNamer = spanNamer;
    }

    @GetMapping
    public void demo() {
        LOGGER.info("start");
        Runnable runnable = () -> LOGGER.info("executing");
        TraceRunnable traceRunnable = new TraceRunnable(tracer, spanNamer, runnable);
        executor.execute(traceRunnable);
        LOGGER.info("end");
    }
}
