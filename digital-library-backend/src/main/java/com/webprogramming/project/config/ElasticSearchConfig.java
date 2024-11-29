package com.webprogramming.project.config;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchConfig {
	private static RestHighLevelClient restHighLevelClient;

	public static RestHighLevelClient getClient() {
		if (restHighLevelClient == null) {
			restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
		}
		return restHighLevelClient;
	}

	public static void closeClient() {
		if (restHighLevelClient != null) {
			try {
				restHighLevelClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
