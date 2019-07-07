package com.miamioh.ridesharing.app.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.miamioh.ridesharing.app.data.entity.RideSharingRequestHash;
import com.miamioh.ridesharing.app.data.entity.Taxi;
import com.miamioh.ridesharing.app.data.repository.RideSharingRequestRepository;
import com.miamioh.ridesharing.app.request.RideSharingRequest;
import com.miamioh.ridesharing.app.utilities.helper.TaxiUtility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RideSharingRequestConsumer {
	
	@Autowired
	private TaxiUtility taxiUtility;
	
	@Autowired
	private RideSharingRequestRepository rideSharingRequestRepository;
	
	@KafkaListener(topics="${kafka.topic}", containerFactory="batchFactory")
	public void consumeRideSharingRequest(RideSharingRequest rideSharingRequest, Acknowledgment ack){
		log.info("Recieved Ride Sharing Request: "+rideSharingRequest);
		RideSharingRequestHash rideSharingRequestHash = new RideSharingRequestHash();
		rideSharingRequestHash.setRequestId(rideSharingRequest.getRequestID());
		rideSharingRequestHash.setRideSharingRequest(rideSharingRequest);
		rideSharingRequestRepository.save(rideSharingRequestHash);
		taxiUtility.shareRide(rideSharingRequest);
		ack.acknowledge();
	}
	
	@KafkaListener(topics="${kafka.taxi.topic}", containerFactory="registerTaxi")
	public void registerTaxi(Taxi taxi, Acknowledgment ack){
		log.info("Recieved Register Taxi Request: "+taxi);
		taxiUtility.registerTaxi(taxi);
		ack.acknowledge();
	}
	
}
