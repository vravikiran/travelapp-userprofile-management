package com.localapp.mgmt.userprofile.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Table(name = "service")
@Entity
public class AgentService {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int service_id;
	private String name;
	@ManyToMany(mappedBy = "services")
	@JsonIgnore
	private List<TravelAgentProfile> agents;

	public AgentService() {
		super();
	}

	public int getService_id() {
		return service_id;
	}

	public void setService_id(int service_id) {
		this.service_id = service_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TravelAgentProfile> getAgents() {
		return agents;
	}

	public void setAgents(List<TravelAgentProfile> agents) {
		this.agents = agents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(agents, name, service_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentService other = (AgentService) obj;
		return Objects.equals(agents, other.agents) && Objects.equals(name, other.name)
				&& service_id == other.service_id;
	}
}
