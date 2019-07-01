package com.miamioh.ridesharing.app.utilities.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.stereotype.Component;

import com.miamioh.ridesharing.app.constants.AppConstants;
import com.miamioh.ridesharing.app.entity.Taxi;
import com.miamioh.ridesharing.app.request.RideSharingRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TaxiUtility {
	
	@Resource(name="redisTemplate")
	private GeoOperations<String, String> geoOperations;
	
	@Autowired
	private ScheduleTaxiEventsHelperPSO scheduleTaxiEventsHelperPSO;
	
	private static final String GEO_SPATIAL_KEY = UUID.randomUUID().toString();
	
	private static final Map<String, Taxi> taxiHub = new ConcurrentHashMap<>();
	
	public void registerTaxi(Taxi taxi) {
		log.info("Registering Taxi with taxiId: "+taxi.getTaxiId());
		if(taxi!=null && !taxiHub.containsKey(taxi.getTaxiId())) {
			this.geoOperations.add(GEO_SPATIAL_KEY, new Point(taxi.getLongitude(), taxi.getLatitude()), taxi.getTaxiId());
			taxiHub.putIfAbsent(taxi.getTaxiId(), taxi);
		}
	}
	
	/*public boolean deregisterTaxi(Taxi taxi) {
			return taxiHub.remove(taxi.getTaxiId(), taxi);
	}*/
	
	public void shareRide(RideSharingRequest request) {
		Circle circle = new Circle(new Point(request.getPickUpEvent().getLongitude(), request.getPickUpEvent().getLatitude()), new Distance(AppConstants.FIND_TAXI_WITHIN_RADIUS_IN_KMS, DistanceUnit.KILOMETERS));
		GeoResults<GeoLocation<String>> radius = this.geoOperations.radius(GEO_SPATIAL_KEY, circle);
		List<GeoResult<GeoLocation<String>>> content = radius.getContent();
		List<Taxi> nearByTaxiList = new ArrayList<>();
		for(GeoResult< GeoLocation<String>> geoResult: content) {
			nearByTaxiList.add(taxiHub.get((geoResult.getContent().getName())));
		}
		
		List<Taxi> avalableNearByTaxiList = nearByTaxiList.stream().filter(i -> i.getNoOfPassenger().get() < AppConstants.TAXI_MAX_CAPACITY ).collect(Collectors.toList());
		log.info("RequestId: "+request.getRequestID()+" Total Number of near by Taxis fetched: "+avalableNearByTaxiList.size());
		log.info("RequestId: "+request.getRequestID()+" List of near by Taxis fetched: "+avalableNearByTaxiList);
		for(Taxi taxi: avalableNearByTaxiList) {
			//taxi.addEventSchedule(request);
			CompletableFuture.runAsync(() -> scheduleTaxiEventsHelperPSO.findPSO(taxi, request));
		}
		
	}
	
	public Taxi getTaxiInstance(String taxiId) {
		return taxiHub.get(taxiId);
	}
	
	public List<Taxi> getAllTaxi(){
		List<Taxi> taxis = new ArrayList<>(taxiHub.values());
		return taxis;
	}

}
