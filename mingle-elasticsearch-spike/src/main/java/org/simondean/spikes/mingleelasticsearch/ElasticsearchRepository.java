package org.simondean.spikes.mingleelasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ElasticsearchRepository implements Repository, Closeable {
  public static final String MINGLE_INDEX = "mingle";
  public static final String MINGLE_EVENTS_INDEX_PREFIX = "mingle-events-";
  public static final String CARD_TYPE = "card";
  public static final DateTimeFormatter timeBasedIndexDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//  private final Node node;
  private final Client client;
  private final ObjectMapper mapper;

  public ElasticsearchRepository(String clusterName) throws UnknownHostException {
//    node = NodeBuilder.nodeBuilder()
////      .clusterName("yourclustername")
//      .settings(Settings.settingsBuilder().put("http.enabled", false))
//      .client(true)
//      .node();
//    client = node.client();
    client = TransportClient.builder()
      .settings(Settings.builder()
        .put("cluster.name", clusterName))
      .build()
      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLoopbackAddress(), 9300));
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  public void init() throws IOException {
    if (!indexExists(MINGLE_INDEX)) {
      client.admin().indices().prepareCreate(MINGLE_INDEX)
        .addMapping(CARD_TYPE, createCardMapping())
        .get();
    }
//    client.admin().indices().preparePutTemplate("mingle")
//      .setTemplate("mingle")
//      .setSettings(Settings.builder()
//        .put("number_of_shards", 1))
//      .addMapping(CARD_TYPE, createCardMapping())
//      .get();

    client.admin().indices().preparePutTemplate("mingle-events")
      .setTemplate("mingle-events-*")
      .setSettings(Settings.builder()
        .put("number_of_shards", 1))
      .addMapping(CARD_TYPE, createCardMapping())
      .get();
  }

  private XContentBuilder createCardMapping() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .startObject("properties")
            .startObject("number")
              .field("type", "integer")
            .endObject()
            .startObject("time")
              .field("type", "date")
            .endObject()
            .startObject("name")
              .field("type", "string")
            .endObject()
          .endObject()
        .endObject();
  }

  @Override
  public Card getCard(String projectName, int cardNumber) throws IOException {
    System.out.println("Get card " + cardNumber);
    GetResponse response = client.prepareGet(MINGLE_INDEX, CARD_TYPE, getDocumentId(projectName, cardNumber))
      .get();

    if (!response.isExists()) {
      return null;
    }

    return mapper.readValue(response.getSourceAsBytes(), Card.class);
  }

  @Override
  public void upsertCard(Card card) throws IOException {
    System.out.println("Upsert card " + card.getNumber());

    client.prepareIndex(MINGLE_INDEX, CARD_TYPE, getDocumentId(card))
      .setSource(mapper.writeValueAsBytes(card))
      .get();
  }

  @Override
  public void insertCardEvent(int eventId, Card card) throws IOException {
    client.prepareIndex(getMingleEventIndex(card.getTimestamp()), CARD_TYPE, getEventDocumentId(eventId, card))
      .setSource(mapper.writeValueAsBytes(card))
      .get();
  }

  private boolean indexExists(String index) {
    IndicesExistsResponse existsResponse = client.admin().indices().prepareExists(index).get();
    return existsResponse.isExists();
  }

  private String getMingleEventIndex(Instant time) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
    return MINGLE_EVENTS_INDEX_PREFIX + timeBasedIndexDateFormatter.format(localDateTime);
  }

  private String getDocumentId(Card card) {
    return getDocumentId(card.getProjectName(), card.getNumber());
  }

  private String getDocumentId(String projectName, int cardNumber) {
    return projectName + "-" + cardNumber;
  }

  private String getEventDocumentId(int eventId, Card card) {
    return getEventDocumentId(card.getProjectName(), eventId);
  }

  private String getEventDocumentId(String projectName, int eventId) {
    return projectName + "-" + eventId;
  }

  @Override
  public void close() throws IOException {
//    node.close();
    client.close();
  }
}
