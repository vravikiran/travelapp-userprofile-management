package com.localapp.mgmt.userprofile.repostories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;

public interface TravelAgentProfileRepository extends JpaRepository<TravelAgentProfile, String> {
    @Query(value = "select case when count(tap) > 0 then true else false end from TravelAgentProfile tap where tap.emailHash = :email_hash")
    public boolean isUserExistsWithEmail(String email_hash);

    public TravelAgentProfile getAgentProfileByEmail(String email_hash);

	/*@Query(value = "select tap from travel_agent_profile tap where tap.mobileno_hash in (select distinct(tas.mobileno_hash) from travel_agent_services tas where tas.service_id  in (ids))", nativeQuery = true)
	public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids);*/
	
	/*@Query(value=" select * from travel_agent_profile tap where tap.mobileno_hash in(select distinct(tas.mobileno_hash) from travel_agent_services tas where tas.service_id in (:ids)) and tap.location = :location", nativeQuery = true)
	public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids,String location);*/
	
	/*
	@Query(value="select * from travel_agent_profile tap where tap.mobileno_hash in (select distinct(tal.mobileno_hash) from travel_agent_langauges tal where tal.lang in (:languages)) and tap.location = :location", nativeQuery = true)
	public List<TravelAgentProfile> filterTravelAgentsByLanguages(List<String> languages, String location);*/

    @Query(value = "select tap from TravelAgentProfile tap where tap.location = :location")
    public List<TravelAgentProfile> filterTravelAgentsByLocation(String location);
}
