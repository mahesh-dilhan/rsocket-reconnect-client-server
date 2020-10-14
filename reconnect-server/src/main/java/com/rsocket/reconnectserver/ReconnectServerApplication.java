package com.rsocket.reconnectserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class ReconnectServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReconnectServerApplication.class, args);
	}

}

@Controller
class ReconnectServer {

	@MessageMapping("reconnect-me")
	public Flux<String> greet(){
		return Flux.fromStream(
				Stream.generate(
						() -> new String("Greet from Serevr" + Instant.now())
				)
		).delayElements(Duration.ofSeconds(1));
	}
}