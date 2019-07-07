package com.miamioh.ridesharing.app.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.miamioh.ridesharing.app.data.entity.Taxi;

@Repository
public interface TaxiHub extends CrudRepository<Taxi, String> {

}
