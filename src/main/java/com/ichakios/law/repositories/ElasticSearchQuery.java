package com.ichakios.law.repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ichakios.law.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class ElasticSearchQuery {

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	private final String indexName = "products";


	public String createOrUpdateDocument(Product product) throws IOException {

		IndexResponse response = elasticsearchClient.index(i -> i
				.index(indexName)
				.id(UUID.randomUUID().toString())
				.document(product)
		);
		if (response.result().name().equals("Created")) {
			return new StringBuilder("Document has been successfully created.").toString();
		} else if (response.result().name().equals("Updated")) {
			return new StringBuilder("Document has been successfully updated.").toString();
		}
		return new StringBuilder("Error while performing the operation.").toString();
	}

	public Product getDocumentById(String productId) throws IOException {
		Product product = null;
		GetResponse<Product> response = elasticsearchClient.get(g -> g
						.index(indexName)
						.id(productId),
				Product.class
		);

		if (response.found()) {
			product = response.source();
			System.out.println("Product name " + product.getName());
		} else {
			System.out.println("Product not found");
		}

		return product;
	}

	public String deleteDocumentById(String productId) throws IOException {

		DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(productId));

		DeleteResponse deleteResponse = elasticsearchClient.delete(request);
		if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
			return new StringBuilder("Product with id ").append(deleteResponse.id()).append(" has been deleted.").toString();
		}
		System.out.println("Product not found");
		return new StringBuilder("Product with id ").append(deleteResponse.id()).append(" does not exist.").toString();

	}

	public List<Product> searchAllDocuments() throws IOException {

		SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
		SearchResponse searchResponse = elasticsearchClient.search(searchRequest, Product.class);
		List<Hit> hits = searchResponse.hits().hits();
		List<Product> products = new ArrayList<>();
		Product prod = null;
		for (Hit object : hits) {
			prod = (Product) object.source();
			prod.setId(object.id());
//			System.out.print(object);
			products.add(prod);

		}
		return products;
	}
}