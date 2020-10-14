package com.rsocket.reconnectclient;

import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.awt.*;
import java.time.Duration;

@Log4j2
@SpringBootApplication
public class ReconnectClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReconnectClientApplication.class, args);
	}

	@Bean
	Mono<RSocketRequester> rSocketRequesterMono (RSocketRequester.Builder  builder){
		return builder.rsocketConnector(
				rSocketConnector -> rSocketConnector.reconnect(
						Retry.fixedDelay(
								Integer.MAX_VALUE, Duration.ofSeconds(1)
						).doBeforeRetry(retrySignal -> log.info("Trying to ")
				)
		)
		).connectTcp("192.168.1.75",7000);
	}
}

@Log4j2
@RestController
class ReconnectClient {

	@Autowired
	private Mono<RSocketRequester>  rSocketRequesterMono;

	@GetMapping(value = "/greet",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Publisher<String> greet(){
		return rSocketRequesterMono.flatMapMany(
				rSocketRequester ->
						rSocketRequester
								.route("reconnect-me")
								.data(Mono.empty())
								.retrieveFlux(String.class)
								.doOnNext(s->log.info("{}",s))
		);
	}
}
