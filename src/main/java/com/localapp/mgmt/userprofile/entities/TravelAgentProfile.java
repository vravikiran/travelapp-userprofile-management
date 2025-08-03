package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

@Table(name = "travel_agent_profile")
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer" })
public class TravelAgentProfile extends PatchableObject {
	@Id
	@JsonIgnore
	private String mobileno_hash;
	@Convert(converter = EncryptDecryptHelper.class)
	private String first_name;
	@Convert(converter = EncryptDecryptHelper.class)
	private String last_name;
	@Convert(converter = EncryptDecryptHelper.class)
	private String email;
	@Convert(converter = LongStringConverter.class)
	@IsValidPhoneNumber(message = "invalid phone number")
	private long mobileno;
	private boolean is_kyc_verified;
	@Convert(converter = EncryptDecryptHelper.class)
	private String org_name;
	@ElementCollection
	@CollectionTable(name = "travel_agent_langauges", joinColumns = @JoinColumn(name = "mobileno_hash"))
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
	private boolean isactive;
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "role_id")
	private Role role;
	@Transient
	@JsonInclude(Include.NON_NULL)
	private List<Integer> serviceids;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "kycid", referencedColumnName = "kycid")
	private TravelAgentKycDetails agentKycDetails;

	public TravelAgentKycDetails getAgentKycDetails() {
		return agentKycDetails;
	}

	public void setAgentKycDetails(TravelAgentKycDetails agentKycDetails) {
		this.agentKycDetails = agentKycDetails;
	}

	public List<Integer> getServiceids() {
		return serviceids;
	}

	public void setServiceids(List<Integer> serviceids) {
		this.serviceids = serviceids;
	}

	@ManyToMany
	@JoinTable(name = "travel_agent_services", joinColumns = @JoinColumn(name = "mobileno_hash"), inverseJoinColumns = @JoinColumn(name = "service_id"))
	private Set<AgentService> services;

	public TravelAgentProfile() {
		super();
	}

	public Set<AgentService> getServices() {
		return services;
	}

	public void setServices(Set<AgentService> services) {
		this.services = services;
	}

	public boolean isIsactive() {
		return isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isIs_kyc_verified() {
		return is_kyc_verified;
	}

	public void setIs_kyc_verified(boolean is_kyc_verified) {
		this.is_kyc_verified = is_kyc_verified;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImage_uri() {
		return image_uri;
	}

	public void setImage_uri(String image_uri) {
		this.image_uri = image_uri;
	}

	public String getEmail_hash() {
		return email_hash;
	}

	public void setEmail_hash(String email_hash) {
		this.email_hash = email_hash;
	}

	public String getMobileno_hash() {
		return mobileno_hash;
	}

	public void setMobileno_hash(String mobileno_hash) {
		this.mobileno_hash = mobileno_hash;
	}

	public LocalDate getCreated_date() {
		return created_date;
	}

	public void setCreated_date(LocalDate created_date) {
		this.created_date = created_date;
	}

	public LocalDate getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(LocalDate updated_date) {
		this.updated_date = updated_date;
	}

	public long getMobileno() {
		return mobileno;
	}

	public void setMobileno(long mobileno) {
		this.mobileno = mobileno;
	}

	public LocalDate getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(LocalDate date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mobileno_hash);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TravelAgentProfile other = (TravelAgentProfile) obj;
		return Objects.equals(mobileno_hash, other.mobileno_hash);
	}
}
