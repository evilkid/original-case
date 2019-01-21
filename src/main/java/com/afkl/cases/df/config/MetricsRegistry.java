package com.afkl.cases.df.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class MetricsRegistry {
    private final Logger logger = LoggerFactory.getLogger(MetricsRegistry.class);


    private long numberRequests;
    private long numberRequestsOK;
    private long numberRequests4XX;
    private long numberRequests5XX;
    private long totalResponse;
    private long minResponse = Long.MAX_VALUE;
    private long maxResponse = 0;

    public void processRequest(HttpServletResponse response, long executeTime) {
        logger.info("Processing a request...");

        numberRequests++;
        totalResponse += executeTime;

        if (minResponse > executeTime) {
            minResponse = executeTime;
        }

        if (maxResponse < executeTime) {
            maxResponse = executeTime;
        }

        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            numberRequestsOK++;
        }
        if (response.getStatus() >= 400 && response.getStatus() < 500) {
            numberRequests4XX++;
        }
        if (response.getStatus() >= 500 && response.getStatus() < 600) {
            numberRequests5XX++;
        }
    }


    public Long getNumberRequests() {
        return numberRequests;
    }

    public void setNumberRequests(Long numberRequests) {
        this.numberRequests = numberRequests;
    }

    public Long getNumberRequestsOK() {
        return numberRequestsOK;
    }

    public void setNumberRequestsOK(Long numberRequestsOK) {
        this.numberRequestsOK = numberRequestsOK;
    }

    public Long getNumberRequests4XX() {
        return numberRequests4XX;
    }

    public void setNumberRequests4XX(Long numberRequests4XX) {
        this.numberRequests4XX = numberRequests4XX;
    }

    public Long getNumberRequests5XX() {
        return numberRequests5XX;
    }

    public void setNumberRequests5XX(Long numberRequests5XX) {
        this.numberRequests5XX = numberRequests5XX;
    }

    public Long getAvgResponse() {
        return numberRequests == 0 ? 0 : totalResponse / numberRequests;
    }


    public Long getMinResponse() {
        return minResponse;
    }

    public void setMinResponse(Long minResponse) {
        this.minResponse = minResponse;
    }

    public Long getMaxResponse() {
        return maxResponse;
    }

    public void setMaxResponse(Long maxResponse) {
        this.maxResponse = maxResponse;
    }
}
