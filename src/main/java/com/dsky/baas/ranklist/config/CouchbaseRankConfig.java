package com.dsky.baas.ranklist.config;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;

/**
 * Spring configuration file
 */

@Configuration
public class CouchbaseRankConfig {

	@Value("${couchbase.workerSize}")
	private int couchbaseWorkerSize;
	@Value("${couchbase.rank.bucketName}")
	private String rankBucketName;
	@Value("${couchbase.rank.pwd}")
	private String rankBucketPassword;
	@Value("${couchbase.rank.uri1}")
	private String couchbaseShareUri1;
	@Value("${couchbase.rank.uri2}")
	private String couchbaseShareUri2;
	@Value("${couchbase.rank.uri3}")
	private String couchbaseShareUri3;

	@Bean(name = "rankCouchbaseClient")
	public CouchbaseClient rankCouchbaseClient() throws IOException {
		//couchbase的连接生成工厂
		CouchbaseConnectionFactory rankFactory = getShareCouchbaseConnectionFactory();
		CouchbaseClient client = new CouchbaseClient(rankFactory);
		return client;
	}

	private CouchbaseConnectionFactory getShareCouchbaseConnectionFactory()
			throws IOException {
		CouchbaseConnectionFactoryBuilder builder = getCouchbaseConnectionFactoryBuilder();
		List<URI> baseList = getBaseShareUris();
		return builder.buildCouchbaseConnection(baseList, rankBucketName,
				rankBucketPassword);
	}

	private List<URI> getBaseShareUris() {
		//多线程访问
		List<URI> baseList = new ArrayList<URI>();
		baseList.add(URI.create(couchbaseShareUri1));
		baseList.add(URI.create(couchbaseShareUri2));
		baseList.add(URI.create(couchbaseShareUri3));
		return baseList;
	}

	private CouchbaseConnectionFactoryBuilder getCouchbaseConnectionFactoryBuilder() {
		CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
		builder.setViewWorkerSize(couchbaseWorkerSize);
		return builder;
	}
}
