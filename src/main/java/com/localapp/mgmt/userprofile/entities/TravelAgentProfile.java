package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.localapp.mgmt.userprofile.util.DateStringConverter;
import com.localapp.mgmt.userprofile.util.EncryptDecryptHelper;
import com.localapp.mgmt.userprofile.util.IsValidPhoneNumber;
import com.localapp.mgmt.userprofile.util.LongStringConverter;
import com.localapp.mgmt.userprofile.util.PatchableObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Table(name = "travel_agent_profile")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TravelAgentProfile extends PatchableObject {
    @Id
    @JsonIgnore
    private String mobile_no_hash;
    @Convert(converter = EncryptDecryptHelper.class)
    private String first_name;
    @Convert(converter = EncryptDecryptHelper.class)
    private String last_name;
    @Convert(converter = EncryptDecryptHelper.class)
    private String email;
    @Convert(converter = LongStringConverter.class)
    @IsValidPhoneNumber(message = "invalid phone number")
    @Column(name = "mobile_no", nullable = false)
    private Long mobileNo;
    private boolean is_kyc_verified;
    @Convert(converter = EncryptDecryptHelper.class)
    private String org_name;
    @ElementCollection
    @CollectionTable(name = "travel_agent_languages", joinColumns = @JoinColumn(name = "mobile_no_hash"))
    @Column(name = "lang")
    private List<String> languages;
    @Convert(converter = DateStringConverter.class)
    @Column(updatable = false)
    private LocalDate date_of_birth;
    private String location;
    private String image_uri;
    @JsonIgnore
    private String email_hash;
    @JsonIgnore
    private LocalDate created_date;
    @JsonIgnore
    private LocalDate updated_date;
    private boolean active;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;
    @Transient
    @JsonInclude(Include.NON_NULL)
    private List<Integer> serviceIds;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kyc_id", referencedColumnName = "kyc_id")
    private TravelAgentKycDetails agentKycDetails;
    @ManyToMany
    @JoinTable(name = "travel_agent_services", joinColumns = @JoinColumn(name = "mobile_no_hash"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<AgentService> services;
}
