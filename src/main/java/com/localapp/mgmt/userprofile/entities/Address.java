package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String address_type;
    private String address_line_1;
    private String address_line_2;
    private String city;
    private String state;
    private String country;
    @ManyToOne
    @JoinColumn(name = "mobile_no_hash")
    @JsonIgnore
    private UserProfile userProfile;
    @JsonIgnore
    private LocalDate created_date;
    @JsonIgnore
    private LocalDate updated_date;
}
