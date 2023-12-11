package com.blockchainanalysisplatform.Services;


import com.blockchainanalysisplatform.Data.EventeumRequest;
import com.blockchainanalysisplatform.Data.EventeumResponse;
import com.blockchainanalysisplatform.Data.Subscription;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;



@Service
@AllArgsConstructor
@Slf4j
public class EventeumService {

    private WebClient webClient;

    public void subscribe(Subscription subscription) throws Exception{
        webClient.post()
                .uri("/api/rest/v1/transaction")
                .body(Mono.just(new EventeumRequest(subscription)), EventeumRequest.class)
                .retrieve()
                .bodyToMono(EventeumResponse.class)
                .doOnError(e -> {
                    if (e instanceof WebClientResponseException webClientResponseException) {
                        throw new RuntimeException("Error during monitor creation: " + webClientResponseException.getResponseBodyAsString());
                    } else {
                        throw new RuntimeException("Error during monitor creation: " + e.getCause());
                    }
                })

                .flatMap(response -> {
                    subscription.setTopicId((response.getId()));
                    subscription.setId(subscription.getTopicId());
                    return Mono.just(ResponseEntity.ok().build());
                }).block();

    }

    public void unsubscribe(String monitorId){
        webClient.delete()
                .uri("/api/rest/v1/transaction/"+monitorId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(response ->
                    log.info("Monitor {} was deleted",monitorId)
                )
                .doOnError(response ->{ //TODO: throw custom exception
                    log.warn("Monitor {} was not deleted",monitorId);
                })
                .subscribe();
    }
}
