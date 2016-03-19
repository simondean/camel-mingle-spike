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
  public static final String MINGLE_EVENTS_TEMPLATE = "mingle-events";
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
//    client.admin().indices().prepareDeleteTemplate(MINGLE_INDEX)
//      .get();

    client.admin().indices().prepareDeleteTemplate(MINGLE_EVENTS_TEMPLATE)
      .get();

    client.admin().indices().prepareGetTemplates("*").get().getIndexTemplates().forEach(template -> {
      System.out.println(template.getName());
    });

    if (indexExists(MINGLE_INDEX)) {
      client.admin().indices().prepareDelete(MINGLE_INDEX)
        .get();
    }

    client.admin().indices().prepareDelete(MINGLE_EVENTS_INDEX_PREFIX + "*")
      .get();

    client.admin().indices().prepareCreate(MINGLE_INDEX)
//      .addMapping(CARD_TYPE, createCardMapping())
      .get();

    System.out.println(createCardEventMapping().string());

    client.admin().indices().preparePutTemplate(MINGLE_EVENTS_TEMPLATE)
      .setCreate(true)
      .setTemplate("mingle-events-*")
//      .setSettings(Settings.builder()
//        .put("number_of_shards", 1))
      .addMapping(CARD_TYPE, createCardEventMapping())
      .get();
  }

  private XContentBuilder createCardEventMapping() throws IOException {
    return XContentFactory.jsonBuilder()
      .startObject()
        .startObject(CARD_TYPE)
          .startObject("properties")
            .startObject("eventId")
              .field("type", "integer")
            .endObject()
            .startObject("@timestamp")
              .field("type", "date")
            .endObject()
            .startObject("card")
              .field("type", "object")
              .startObject("properties")
                .startObject("number")
                  .field("type", "integer")
                .endObject()
                .startObject("name")
                  .field("type", "string")
                  .field("index", "not_analyzed")
                .endObject()
                .startObject("properties")
                  .field("type", "object")
                .endObject()
              .endObject()
            .endObject()
          .endObject()
          .startArray("dynamic_templates")
//            .startObject()
//              .startObject("strings")
//                .field("match_mapping_type", "string")
//                .startObject("mapping")
//                  .field("type", "string")
//                  .field("index", "not_analyzed")
//                .endObject()
//              .endObject()
//            .endObject()
            .startObject()
              .startObject("card")
                .field("path_match", "card.*")
                .startObject("mapping")
                  .field("type", "string")
                  .field("index", "not_analyzed")
                .endObject()
              .endObject()
            .endObject()
            .startObject()
              .startObject("cardProperty")
                .field("path_match", "card.properties.*")
                .startObject("mapping")
                  .field("index", "not_analyzed")
                .endObject()
              .endObject()
            .endObject()
            .startObject()
              .startObject("changedPropertyNewValue")
                .field("path_match", "propertyChanges.*.newValue")
                .startObject("mapping")
                  .field("index", "not_analyzed")
                .endObject()
              .endObject()
            .endObject()
            .startObject()
              .startObject("changedPropertyOldValue")
                .field("path_match", "propertyChanges.*.oldValue")
                .startObject("mapping")
                  .field("index", "not_analyzed")
                .endObject()
              .endObject()
            .endObject()
          .endArray()
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

    System.out.println(mapper.writeValueAsString(card));
    client.prepareIndex(MINGLE_INDEX, CARD_TYPE, getDocumentId(card))
      .setSource(mapper.writeValueAsBytes(card))
      .get();
  }

  @Override
  public void insertCardEvent(CardEvent cardEvent) throws IOException {
    System.out.println("Insert card event " + cardEvent.getEventId());

    System.out.println(mapper.writeValueAsString(cardEvent  ));
    client.prepareIndex(getMingleEventIndex(cardEvent.getTimestamp()), CARD_TYPE, getEventDocumentId(cardEvent))
      .setSource(mapper.writeValueAsBytes(cardEvent))
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

  private String getEventDocumentId(CardEvent cardEvent) {
    return getEventDocumentId(cardEvent.getCard().getProjectName(), cardEvent.getEventId());
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
