package web.crea.movie.catalog;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}



	@Bean
	@LoadBalanced
	public WebClient.Builder getWebClient(){
		// Configure Apache HttpClient with timeouts
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofSeconds(5))  // Timeout for receiving a response
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Timeout for establishing a connection
				.doOnConnected(conn ->
						conn.addHandlerLast(new ReadTimeoutHandler(5))  // Timeout for reading data
								.addHandlerLast(new WriteTimeoutHandler(5)) // Timeout for writing data
				);

		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient));
		
	}

}
