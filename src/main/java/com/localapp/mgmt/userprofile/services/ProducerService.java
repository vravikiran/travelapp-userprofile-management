package com.localapp.mgmt.userprofile.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localapp.mgmt.userprofile.util.Constants;

@Service
public class ProducerService {
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public void publishDataChanges(String tableName, Map<String, Object> payload) {
		System.out.println(tableName);
		try {
			switch (tableName.toLowerCase()) {
			case Constants.TRAVEL_AGENT_PROFILE_TABLE:
				kafkaTemplate.send(Constants.TRAVEL_AGENT_PROFILE_TOPIC, objectMapper.writeValueAsString(payload));
				break;
			case Constants.USER_MGMT_USER_PROFILE_TABLE:
				kafkaTemplate.send(Constants.USER_MGMT_USER_PROFILE_TOPIC, objectMapper.writeValueAsString(payload));
				break;
			case Constants.ROLE_TABLE:
				kafkaTemplate.send(Constants.ROLE_TOPIC, objectMapper.writeValueAsString(payload));
				break;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
