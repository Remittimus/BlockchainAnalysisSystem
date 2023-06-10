package com.blockchainanalysisplatform.Services;


import com.blockchainanalysisplatform.Data.EventeumRequest;
import com.blockchainanalysisplatform.Data.EventeumResponse;
import com.blockchainanalysisplatform.Data.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EventeumService {

    private WebClient webClient;

    public void subscribe(Subscription subscription) throws Exception{
        webClient.post()
                .uri("/api/rest/v1/transaction")
                .body(Mono.just(new EventeumRequest(subscription)), EventeumRequest.class)
                .retrieve()
                .bodyToMono(EventeumResponse.class) //TODO:remove subId class, json parser?
                .doOnError(response ->{

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
                .doOnSuccess(response -> {
                    System.out.println("Monitor "+monitorId+" was deleted");
                })
                .doOnError(response ->{ //TODO: throw custom exception
                    System.out.println("Monitor "+monitorId+" was not deleted");
                })
                .subscribe();
    }
}
