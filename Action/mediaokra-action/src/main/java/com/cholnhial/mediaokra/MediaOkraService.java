package com.cholnhial.mediaokra;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.*;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.pubsub.v1.*;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MediaOkraService {

    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static final String MEDIAOKRA_TOPIC_PREFIX = "MEDIAOKRA-TOPIC-";
    private static final String MEDIAOKRA_SUBSCRIPTION_PREFIX = "MEDIAOKRA-SUB-";

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MediaOkraService.class);
    private  Datastore datastore = null;
    private String userEmail;

    public MediaOkraService(String userEmail) {
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
        this.userEmail = userEmail;
    }

    public String generateNewCode() throws Exception {
        String newCode = RandomStringUtils.randomAlphanumeric(7).toUpperCase();

        Entity userCodeEntity = getUserCodeEntity();
        if(userCodeEntity != null) {
            String oldCode = userCodeEntity.getString("code");
            Entity updatedCodeEntity = Entity.newBuilder(userCodeEntity.getKey())
                    .set("code", newCode)
                    .set("email", userEmail).build();
            datastore.update(updatedCodeEntity);
            createPubSubTopicAndSubscription(newCode, oldCode);
        } else {
            // save as new
            KeyFactory keyFactory =  datastore.newKeyFactory().setKind("User");
            Key key = datastore.allocateId(keyFactory.newKey());
            Entity  userCode = Entity.newBuilder(key)
                    .set("email", StringValue.newBuilder(userEmail).build())
                    .set("code", newCode)
                    .build();
            datastore.put(userCode);
            createPubSubTopicAndSubscription(newCode, null);
        }

        return newCode;
    }

    public String getUserCode() {
        Entity userCodeEntity = getUserCodeEntity();
        if(userCodeEntity != null) {
            return userCodeEntity.getString("code");
        }

        return null;
    }

    private Entity getUserCodeEntity() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("User")
                .setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.eq("email", userEmail)))
                .build();
        QueryResults<Entity> userCodes = datastore.run(query);

        if(userCodes.hasNext()) {
            return userCodes.next();
        }

        return null;
    }

    private static void createPubSubTopicAndSubscription(String newCode, String previousCode) throws IOException {

        /***
         * REMOVE OLD TOPIC AND SUBSCRIPTION
         */
        if (previousCode != null) {
            String oldTopicId = MEDIAOKRA_TOPIC_PREFIX + previousCode;
            try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
                ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, oldTopicId);
                topicAdminClient.deleteTopic(topicName);
            }

            try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
                ProjectSubscriptionName subscriptionName =
                        ProjectSubscriptionName.of(PROJECT_ID, MEDIAOKRA_SUBSCRIPTION_PREFIX + previousCode);
                subscriptionAdminClient.deleteSubscription(subscriptionName);
            }
        }

        /***
         *  CREATE NEW TOPIC
         */
        String newTopic = MEDIAOKRA_TOPIC_PREFIX + newCode;
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, newTopic);
            topicAdminClient.createTopic(topicName);
        }

        /**
         *  CREATE NEW SUBSCRIPTION
         */
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            ProjectTopicName topicNameToSubscribeTo = ProjectTopicName.of(PROJECT_ID, newTopic);
            ProjectSubscriptionName subscriptionName =
                    ProjectSubscriptionName.of(PROJECT_ID, MEDIAOKRA_SUBSCRIPTION_PREFIX + newCode);
            Subscription subscription =
                    subscriptionAdminClient.createSubscription(
                            subscriptionName, topicNameToSubscribeTo, PushConfig.getDefaultInstance(), 0);
        }
    }

    public void publishMediaCommand(String command, String userCode) throws ExecutionException, InterruptedException, IOException {
        String topicId = MEDIAOKRA_TOPIC_PREFIX + userCode;
        ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<>();
        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFromUtf8(command);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(data)
                    .build();
            ApiFuture<String> future = publisher.publish(pubsubMessage);
            futures.add(future);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (publisher != null) {
                publisher.shutdown();
            }
        }
    }

}