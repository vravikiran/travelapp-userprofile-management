package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "user_kyc_details")
@Entity
@Data
public class KycDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int kyc_id;
    private String kyc_img_url1;
    private String kyc_img_url2;
    private String document_type;
    private String document_no;
    @OneToOne(mappedBy = "kycDetails")
    @JsonIgnore
    private UserProfile userProfile;
    @JsonIgnore
    private LocalDate kyc_date;
}
