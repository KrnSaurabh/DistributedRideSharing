package com.miamioh.ridesharing.app.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.miamioh.ridesharing.app.config.params.KafkaConsumerConfigParams;
import com.miamioh.ridesharing.app.entity.Taxi;
import com.miamioh.ridesharing.app.request.RideSharingRequest;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	
	@Bean("batchFactory")
	@DependsOn("kafkaConsumerConfigParams")
	public ConcurrentKafkaListenerContainerFactory<String, RideSharingRequest> batchFactory(KafkaConsumerConfigParams kafkaConsumerConfigParams) {
		ConcurrentKafkaListenerContainerFactory<String, RideSharingRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		DefaultKafkaConsumerFactory<String, RideSharingRequest> consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigParams.getConsumerConfigWithJsonDeserializer(), 
				new StringDeserializer(), new JsonDeserializer<>(RideSharingRequest.class));
		factory.setConsumerFactory(consumerFactory);
		factory.setConcurrency(1);
		factory.setBatchListener(false);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		return factory;
	}
	
	@Bean("registerTaxi")
	@DependsOn("kafkaConsumerConfigParams")
	public ConcurrentKafkaListenerContainerFactory<String, Taxi> batchFactoryRegisterTaxi(KafkaConsumerConfigParams kafkaConsumerConfigParams) {
		ConcurrentKafkaListenerContainerFactory<String, Taxi> factory = new ConcurrentKafkaListenerContainerFactory<>();
		JsonDeserializer<Taxi> ds = new JsonDeserializer<>(Taxi.class);
		//ds.addTrustedPackages("*");
		DefaultKafkaConsumerFactory<String, Taxi> consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigParams.getConsumerConfigWithJsonDeserializer(), 
				new StringDeserializer(), ds);
		
		factory.setConsumerFactory(consumerFactory);
		factory.setConcurrency(1);
		factory.setBatchListener(false);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		return factory;
	}
	
	/*@Bean
    public StringJsonMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }*/
	
}
