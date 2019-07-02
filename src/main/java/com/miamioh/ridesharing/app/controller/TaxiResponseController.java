package com.miamioh.ridesharing.app.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miamioh.ridesharing.app.constants.AppConstants;
import com.miamioh.ridesharing.app.data.dao.TaxiResponseDao;
import com.miamioh.ridesharing.app.data.entity.RideSharingRequestHash;
import com.miamioh.ridesharing.app.data.entity.TaxiResponse;
import com.miamioh.ridesharing.app.data.entity.TaxiResponseArchive;
import com.miamioh.ridesharing.app.data.entity.TempScheduledEventList;
import com.miamioh.ridesharing.app.data.repository.RideSharingRequestRepository;
import com.miamioh.ridesharing.app.data.repository.TaxiResponseArchiveRepository;
import com.miamioh.ridesharing.app.data.repository.TempScheduledEventListRepository;
import com.miamioh.ridesharing.app.entity.Event;
import com.miamioh.ridesharing.app.entity.Taxi;
import com.miamioh.ridesharing.app.request.RideSharingConfirmation;
import com.miamioh.ridesharing.app.request.RideSharingConfirmationAck;
import com.miamioh.ridesharing.app.utilities.helper.TaxiUtility;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TaxiResponseController {
	
	@Autowired
	private TaxiUtility taxiUtility;
	
	@Autowired
	private TaxiResponseDao taxiResponseDao;
	
	@Autowired
	private TempScheduledEventListRepository tempScheduledEventListRepository;
	
	@Resource(name="redisTemplate")
	private SetOperations<String, String> setOperations;
	
	@Resource(name="redisTemplate")
	private ZSetOperations<String, String> zSetOperations;
	
	@Autowired
	private RideSharingRequestRepository rideSharingRequestRepository;
	
	@Autowired
	private TaxiResponseArchiveRepository taxiResponseArchiveRepository;
	
	
	@GetMapping(value = "/RideSharing/TaxiResponse/{request_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TaxiResponse getTaxiResponsesByRequestId(@NotBlank @PathVariable(value="request_id") String requestId){
		log.info("Inside Get Taxi Response Controller");
		Iterable<TaxiResponse> taxiResponses = taxiResponseDao.getTaxiResponses(taxiResponseDao.getResponseIds(requestId));
		
		List<TaxiResponse> taxiResponsesList = new ArrayList<>();
		taxiResponses.forEach(a -> taxiResponsesList.add(a));
		log.info("Total No. Of Responses: "+taxiResponsesList.size());
		if(taxiResponsesList != null && !taxiResponsesList.isEmpty()) {
			Collections.sort(taxiResponsesList, ((a,b)->{
				int result = Double.valueOf(a.getCost()).compareTo(Double.valueOf(b.getCost()));
				if(result==0) {
					result = Long.valueOf(a.getPickTimeInMinutes()).compareTo(Long.valueOf(b.getPickTimeInMinutes()));
				}
				if(result == 0) {
					result = Long.valueOf(a.getTimeToDestinationInMinutes()).compareTo(Long.valueOf(b.getTimeToDestinationInMinutes()));
				}
				if(result == 0) {
					result = Integer.valueOf(a.getAvailableSeats()).compareTo(Integer.valueOf(b.getAvailableSeats()));
				}
				return result;
			}));
			TaxiResponse response = taxiResponsesList.get(0);
			CompletableFuture.runAsync(() ->{
				TaxiResponseArchive archiveResponse = new TaxiResponseArchive();
				archiveResponse.setResponseId(response.getResponseId());
				archiveResponse.setTaxiResponse(response);
				taxiResponseArchiveRepository.save(archiveResponse);
				taxiResponseDao.deleteAll(taxiResponsesList);
				taxiResponsesList.remove(response);
				taxiResponsesList.forEach(resp -> tempScheduledEventListRepository.deleteById(resp.getResponseId()));
			});
			
			return response;
		}else {
			return null;
		}
		
	}
	
	@GetMapping(value="/RideSharing/Taxis", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Taxi> getAllTaxi(){
		List<Taxi> allTaxi = taxiUtility.getAllTaxi();
		log.info("Total number of Taxis: "+allTaxi.size());
		return allTaxi;
	}
	
	
	@PostMapping(value = "/RideSharing/RideConfirmation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RideSharingConfirmationAck confirmRide(@RequestBody RideSharingConfirmation rideSharingConfirmation) {
		
		RideSharingConfirmationAck ack = new RideSharingConfirmationAck();
		ack.setResponseId(rideSharingConfirmation.getResponseId());
		Taxi taxi = taxiUtility.getTaxiInstance(rideSharingConfirmation.getTaxiId());
		int noOfPassenger = taxi.getNoOfPassenger().get();//add synchronized block
		if(rideSharingConfirmation.isConfirmed() && noOfPassenger < AppConstants.TAXI_MAX_CAPACITY) {
			
			 Optional<TempScheduledEventList> tempEvents = tempScheduledEventListRepository.findById(rideSharingConfirmation.getResponseId());
			 tempEvents.ifPresent(a -> {
				 try {
				 ObjectMapper mapper = new ObjectMapper();
				 String pickUpEventObjToStr = mapper.writeValueAsString(a.getPickUpEvent());
				 String dropEventObjToStr = mapper.writeValueAsString(a.getDropEvent());
				
				 zSetOperations.add(rideSharingConfirmation.getTaxiId(), pickUpEventObjToStr, a.getPickUpEvent().getIndex());
				 zSetOperations.add(rideSharingConfirmation.getTaxiId(), dropEventObjToStr, a.getDropEvent().getIndex());
				 Set<String> taxiResponseIds = setOperations.members("TaxiResponseMap:"+taxi.getTaxiId());
				 taxiResponseIds.forEach(responseId -> tempScheduledEventListRepository.deleteById(responseId));
				 setOperations.remove("TaxiResponseMap:"+taxi.getTaxiId(), taxiResponseIds.toArray());
				// taxiResponseDao.delete(rideSharingConfirmation.getResponseId());// change code to delete by requestId
				 taxi.getNoOfPassenger().incrementAndGet();
				 } catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 });
			 
			 if(tempEvents.isPresent()) {
				 ack.setAckStatus(true);
				 ack.setTaxi(taxiUtility.getTaxiInstance(rideSharingConfirmation.getTaxiId()));
				 ack.setMessage("Booking Confirmed");
			 }else {
				 ack.setAckStatus(false);
				 ack.setMessage("Timed Out");
			 }
		}else {
			ack.setAckStatus(false);
			ack.setMessage("Taxi Max capacity reached");
		}
		if(!ack.isAckStatus()) {
			CompletableFuture.runAsync(() -> {
				Optional<RideSharingRequestHash> rideSharingRequest = rideSharingRequestRepository.findById(rideSharingConfirmation.getRequestId());
				rideSharingRequest.ifPresent(request -> taxiUtility.shareRide(request.getRideSharingRequest()));
				});
		}
		return ack;
	}
}
