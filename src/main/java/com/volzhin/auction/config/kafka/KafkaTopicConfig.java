package com.volzhin.auction.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topic.partitions}")
    private int partitions;
    @Value("${spring.kafka.topic.replicationFactor}")
    private short replicationFactor;

    @Value("${spring.kafka.topic.names.update-lot}")
    private String updateLotTopicName;
    @Value("${spring.kafka.topic.names.new-bid}")
    private String newBidTopicName;
    @Value("${spring.kafka.topic.names.delete-lot}")
    private String deleteLotTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic updateLotTopic() {
        return new NewTopic(updateLotTopicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic newBidTopic() {
        return new NewTopic(newBidTopicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic deleteLotTopic() {
        return new NewTopic(deleteLotTopicName, partitions, replicationFactor);
    }
}
