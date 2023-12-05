package com.ichakios.law.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class ElasticSearchConfiguration {

	@Bean
	public RestClient getRestClient() {
		SSLContext sslContext = TransportUtils
				.sslContextFromCaFingerprint("228c68e7cc170b26cce2cd053d83451a153a645a7196b592be618383d19b637f");

		BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
		credsProv.setCredentials(
				AuthScope.ANY, new UsernamePasswordCredentials("elastic", "sGzVEZDS_0S-XC7_qX3o")
		);

		RestClient restClient = RestClient
				.builder(new HttpHost("localhost", 9200, "https"))
				.setHttpClientConfigCallback(hc -> hc
						.setSSLContext(sslContext)
						.setDefaultCredentialsProvider(credsProv)
				)
				.build();
		return restClient;
	}

	@Bean
	public ElasticsearchTransport getElasticsearchTransport() {
		return new RestClientTransport(
				getRestClient(), new JacksonJsonpMapper());
	}


	@Bean
	public ElasticsearchClient getElasticsearchClient() {
		ElasticsearchClient client = new ElasticsearchClient(getElasticsearchTransport());
		return client;
	}
}
