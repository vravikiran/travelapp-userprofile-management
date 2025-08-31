package com.localapp.mgmt.userprofile.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "service")
@Entity
@Data
public class AgentService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int service_id;
    private String name;
    @ManyToMany(mappedBy = "services")
    @JsonIgnore
    private List<TravelAgentProfile> agents;
}
