package Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import org.elasticsearch.common.xcontent.XContentFactory.*;

import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.*;

import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.mapper.object.ObjectMapper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.json.JSONObject;

import Model.FileManager;
import Test.TestFileManager;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Schnittstelle zu ElasticSearch. Regelt die Befehle zu ElasticSearch und returnt die Ergebnisse.
 * @author swiebe
 *
 */

public class ESInterface {
	
	String esPath = "C:/Users/Lenovo/Desktop/Informatik/Semester 4/Praktikum Swe goebel/PraktikumSE/elasticsearch-2.3.3";
	Client client;
	Node node;
	String clusterName = "brNews";
	String indexName = "myex";
	String typeName = "myexample";
	String id = "17";
	String jsonString = "{\"user\":\"swiebe\",\"postDate\":\"2016-05-03\",\"message\":\"Das ist ein Test 3\"}";
	
	/**
	// Index name
	String _index = response.getIndex();
	// Type name
	String _type = response.getType();
	// Document ID (generated or not)
	String _id = response.getId();
	// Version (if it's the first time you index this document, you will get: 1)
	long _version = response.getVersion();
	// isCreated() is true if the document is a new one, false if it has been updated
	boolean created = response.isCreated();
	*/
	
	/**
	 * Diese Methode erstellt eine Verbindung zu Elasticsearch
	 * und konfiguriert den Client. 
	 * @return
	 */
	public boolean connectES() {
		
		try {
			client = TransportClient.builder().build()
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300))
			        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			Settings settings = Settings.settingsBuilder()
			        .put("cluster.name", clusterName).build();
			client = TransportClient.builder().settings(settings).build();
			node = nodeBuilder().clusterName(clusterName).client(true).node();
			client = node.client();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			return false;
		}
		return !node.isClosed();
	}
	
	/**
	 * Diese Methode schließt die Verbindung zum Client und Node
	 */
	public void closeES() {
		node.close();
		client.close();
	}
	
	/**
	 * Diese Methode werde ich mir noch genauer ansehen
	 * aber im Prinzip nichts anderes als Connection zu ES
	 */
	public void startClient(){
		Settings.Builder settings = Settings.builder();
		settings.put("path.home", esPath);
		NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();
		nodeBuilder.settings(settings);
		nodeBuilder.client(true);
		Node node = nodeBuilder.node();
		client = node.client();
		
	}
	
	
	/**
	 * Diese Methode aktualisiert den Index
	 */
	public void refreshIndex() {
		// Nachdem ein Document-Update gemacht wurde, sollte der Index aktualisiert werden
		client.admin().indices().prepareRefresh().execute().actionGet();
	}
	
	/**
	 * Diese Methode fügt ein Dokument zu der Node hinzu
	 * @param jsonString
	 * @return
	 */
	public boolean addDocument(String jsonString) {
		IndexResponse response = client.prepareIndex(indexName, typeName)
		        .setSource(jsonString)
		        .execute()
		        .actionGet();
		return response.isCreated();
	}
	
	/**
	 * Diese Methode macht eine Query-Anfrage an den Index nach field und value
	 * @param field
	 * @param queryParam
	 * @return
	 */
	String fieldQuery(String field, String queryParam) {
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(typeName)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery(field, queryParam))
		        .setFrom(0)
		        .setSize(10)
		        .setExplain(true)
		        .execute()
		        .actionGet();
		return response.toString();
	}
	
	/**
	 * Diese Methode wird verwendet, um eine 
	 * Bereichssuche auf einem Feld auszuführen
	 * @param field
	 * @param from
	 * @param to
	 * @return
	 */
	String rangeQuery(String field, int from, int to) {
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(typeName)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setPostFilter(QueryBuilders.rangeQuery("id").from(0).to(3))
		        .setFrom(0)
		        .setSize(10)
		        .setExplain(true)
		        .execute()
		        .actionGet();
		return response.toString();
	}
	
	/**
	 * Diese Methode aktualisiert ein existierendes Document
	 * @param contrib
	 */
	public void updateDocument() {
		//client.prepareUpdate() mal probieren
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(indexName);
		updateRequest.type(typeName);
		updateRequest.id();
		updateRequest.doc();
		try {
			client.update(updateRequest).get();
		} catch (InterruptedException e) {
			System.out.println("ERROR: " + e.getMessage());
		} catch (ExecutionException e) {
			System.out.println("ERROR: " + e.getMessage());
		}

	}
	
	/**
	 * Diese Methode löscht ein existierendes Document
	 */
	public void deleteDocument() {
		@SuppressWarnings("unused")
		DeleteResponse response = client.prepareDelete(indexName, typeName, id)
		        .execute()
		        .actionGet();
	}
	
	/**
	 * Diese Methode ist ein Beispiel vom Professor
	 * KA ob wird die benutzen werden
	 */
	
	public void addXmlField(){
		XContentBuilder builder;
		try {
			builder = jsonBuilder();
			builder.startObject();
			builder.field("user", "swiebe");
			builder.field("postDate", new Date());
			builder.field("age", 28);
			builder.field("message", "Wann werden wir lernen, dass wir nicht besser sind");
			builder.endObject();
			
			client.prepareIndex(indexName, typeName, id).setSource(builder).get();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		IndicesAdminClient indices = client.admin().indices();  
		indices.flush(new FlushRequest("myex").force(true)).actionGet();
	}
	
	/**
	 * Diese Methode ist eine einfache Form einen jsonString
	 * in ES einzufügen. Wird nicht benutzt!!!
	 * Nur erstmal zu Anschaungszwecken
	 */
	public void addXmlString(){
		
		String json = "{" +
		        "\"user\":\"kimchyy\"," +
		        "\"postDate\":\"2013-01-30\"," +
		        "\"message\":\"Wann werden wir lernen, dass wir nicht besser sind "
		        + "als andere Menschen? Wann werden wir lernen, dass unsere Kultur, "
		        + "egal wie sehr wir sie wertschätzen, nicht die einzige ist? Wann werden "
		        + "wir lernen, dass es im Grunde keinen Unterschied zwischen jenen gibt, "
		        + "die wir bewundernd auf ein Podest stellen und jenen, die wir so gerne verachten? "
		        + "Wann werden wir lernen, dass unsere westliche Kultur keiner östlichen Kultur\"" +
		    "}";

		IndexResponse response  = client.prepareIndex("myex", "myexample2", "4").setSource(json).get();
		
		IndicesAdminClient indices = client.admin().indices();  
		indices.flush(new FlushRequest("myex").force(true)).actionGet();
	}
	
	/**
	 * In dieser Methode sind mehrere Queries an ES demonstriert
	 * Wird erstmal zu Veranschaulichungszwecken benutzt
	 */
	public void search(String indexName, String typeName){
		SearchResponse re = client.prepareSearch(indexName)
		        .setTypes(typeName)
//		        .setSearchType()
		       
		        .setQuery(QueryBuilders.termQuery("message", "wann")) 
//		        .setQuery(QueryBuilders.termQuery("message", "wertschätzen"))// Query
//		        .setQuery(QueryBuilders.termQuery("id", "4"))// Query
//		        .setPostFilter(QueryBuilders.rangeQuery("message").from("wann").to("kultur"))   // Filter
//		        .setQuery(QueryBuilders.moreLikeThisQuery("massage").like("menschen"))
//		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();

	System.out.println(re);
	}

	/**The get API allows to get a typed JSON 
	 * document from the index based on its id. 
	 * The following example gets a JSON document 
	 * from an index called twitter, under a type 
	 * called tweet, with id valued 1:
	 */

	public String getResp(){
		GetResponse response = client.prepareGet("test4", "meinTest4", "17").get();
		return response.getId()+response.getIndex()+response.getType()+response.getSourceAsString();
	}
	
	
	public static void main(String[]args){
		FileManager fm = new FileManager();
		ESInterface es = new ESInterface();
		String file = "C:/Users/Lenovo/Desktop/Informatik/Semester 4/Praktikum Swe goebel/"
				+ "PraktikumSE/RSSArchive/RSS/rssfiles/germany/de/science/WissenFAZNET/y2016/m2/d26/RSS1081890068.xml";
//		System.out.println("Client gestartet");
		es.startClient();
		es.addXmlField();
//		es.search(es.indexName,es.typeName);
//		System.out.println("dokument hinzugefügt = "+es.addDocument(fm.readXML(file)));
		
//		System.out.println(es.getResp());
	}
	
}
