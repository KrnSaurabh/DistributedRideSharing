package com.miamioh.ridesharing.app.config.params;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class KafkaConsumerConfigurationProperties.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka")
@Data
/**
 * Instantiates a new kafka consumer configuration properties.
 *
 * @param bootstrapServers the bootstrap servers
 * @param consumerGroupId the consumer group id
 * @param consumerAutoOffsetResetConfig the consumer auto offset reset config
 * @param sslEnabled the ssl enabled
 */
/** The Constant log. */
@Slf4j
public class KafkaConsumerConfigParams {

    // Below default values should be overridden by individual applications.

    /** The bootstrap servers. */
    private String bootstrapServers;

    /** The consumer group id. */
    private String consumerGroupId;
    
    /** The consumer auto offset reset config. */
    private String consumerAutoOffsetResetConfig;
    
    private String enableAutoCommit;


    /**
     * Gets the consumer config.
     *
     * @return the consumer config
     */
    @Bean
    public Map<String, Object> getConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        log.info("Bootstrap servers for the kafka consumer app are:{}", bootstrapServers);
        log.info("AutoOffset Reset Config for the kafka consumer app is:{}", consumerAutoOffsetResetConfig);
        log.info("clientId for the kafka consumer app is:{}", consumerGroupId);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerAutoOffsetResetConfig); 
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
       // ConsumerConfig.MAX_POLL_RECORDS_CONFIG

        return props;
    }

    /**
     * Gets the consumer config with string deserializer.
     *
     * @return the consumer config with string deserializer
     */
    public Map<String, Object> getConsumerConfigWithJsonDeserializer() {
        Map<String, Object> consumerConfig = getConsumerConfig();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return consumerConfig;
    }

}
