package com.afkl.cases.df.web.rest;

import com.afkl.cases.df.config.MetricsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
public class MetricsController {
    @Autowired
    private MetricsRegistry metricsRegistry;

    @GetMapping("metrics")
    public Callable<MetricsRegistry> getMetrics() {
        return () -> metricsRegistry;
    }
}
