package com.afkl.cases.df.web.rest;


import com.afkl.cases.df.domain.CompleteFare;
import com.afkl.cases.df.domain.Fare;
import com.afkl.cases.df.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
public class TravelController {

    private final Logger logger = LoggerFactory.getLogger(TravelController.class);


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${simple-travel-api.base-url}${simple-travel-api.airport-endpoint}")
    private String airportsEndpoint;

    @Value("${simple-travel-api.base-url}${simple-travel-api.fare-endpoint}")
    private String fareEndpoint;

    @GetMapping("airports-count")
    public Callable<Long> getAirportsCount(@RequestAttribute("requestId") String requestId,
                                           @RequestParam(value = "term", required = false) String searchTerm) {
        return () -> {
            logger.info("{} : Processing a getAirports Count request ...", requestId);

            String url = airportsEndpoint;
            if(searchTerm != null){
                url += "?term=" + searchTerm;
            }

            ResponseEntity<PagedResources<Resource<Location>>> response = restTemplate
                    .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Resource<Location>>>() {
                    });

            if (response != null && response.getBody() != null && response.getBody().getMetadata() != null)
                return response.getBody().getMetadata().getTotalElements();

            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @GetMapping("airports")
    public Callable<List<Location>> getAirports(@RequestAttribute("requestId") String requestId,
                                                @RequestParam(value = "page", required = false) Integer page,
                                                @RequestParam(value = "size", required = false) Integer size,
                                                @RequestParam(value = "term", required = false) String searchTerm) {
        return () -> {
            logger.info("{} : Processing a getAirports request ...", requestId);

            String url = airportsEndpoint + "?";
            if (page != null && size != null) {
                url += "page=" + page + "&size=" + size;
            }
            if (searchTerm != null) {
                url += "&term=" + searchTerm;
            }

            ResponseEntity<PagedResources<Resource<Location>>> response = restTemplate
                    .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Resource<Location>>>() {
                    });

            if (response != null && response.getBody() != null) {
                return response.getBody().getContent().stream().map(Resource::getContent).collect(Collectors.toList());
            }

            logger.error("{} : Could not retrieve a list of airports", requestId);

            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @GetMapping("fares/{origin}/{destination}")
    public Callable<CompleteFare> getFare(@PathVariable("origin") String origin,
                                          @PathVariable("destination") String destination,
                                          @RequestParam(value = "currency", defaultValue = "EUR") String currency,
                                          @RequestAttribute("requestId") String requestId) {
        return () -> {

            logger.info("{} : Getting the fare for origin: '{}' and destination: '{}'", requestId, origin, destination);

            final String url;
            if (!StringUtils.isEmpty(currency)) {
                url = fareEndpoint + "/" + origin + "/" + destination + "?currency=" + currency;
            } else {
                url = fareEndpoint + "/" + origin + "/" + destination;
            }

            CompleteFare completeFare = new CompleteFare();

            Future fareRequest = fareRequest(url, completeFare);
            Future originRequest = locationRequest(airportsEndpoint + "/" + origin, completeFare, true);
            Future destinationRequest = locationRequest(airportsEndpoint + "/" + destination, completeFare, false);

            fareRequest.get();
            originRequest.get();
            destinationRequest.get();

            if (!completeFare.isValid()) {
                logger.error("{} : the fare request was not complete: {}", requestId, completeFare);
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return completeFare;
        };
    }

    private Future fareRequest(String url, CompleteFare completeFare) {
        return taskExecutor.submit(() -> {
            ResponseEntity<Fare> response = restTemplate
                    .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Fare>() {
                    });

            if (response != null && response.getBody() != null) {
                completeFare.setAmount(response.getBody().getAmount());
                completeFare.setCurrency(response.getBody().getCurrency());
            }
        });
    }

    private Future locationRequest(String url, CompleteFare completeFare, boolean isOrigin) {
        return taskExecutor.submit(() -> {
            ResponseEntity<Location> response = restTemplate
                    .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Location>() {
                    });

            if (isOrigin) {
                completeFare.setOrigin(response.getBody());
            } else {
                completeFare.setDestination(response.getBody());
            }
        });
    }


}