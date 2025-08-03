package com.localapp.mgmt.userprofile.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "user_preferences")
@Entity
public class UserPreferences {
	@Id
	@JsonIgnore
	private String mobileno_hash;
	@ElementCollection
	@CollectionTable(name="user_destination_preferences", joinColumns = @JoinColumn(name="mobileno_hash"))
	@Column(name="dest_type_pref")
	private List<String> destination_types;
	@ElementCollection
	@CollectionTable(name="user_cuisine_preferences", joinColumns = @JoinColumn(name="mobileno_hash"))
	@Column(name="cuisine_type")
	private List<String> cuisine_preferences;
	private String vehicle_type;
	private String accomodation_type;
	@ElementCollection
	@CollectionTable(name="user_language_preferences", joinColumns = @JoinColumn(name="mobileno_hash"))
	@Column(name="pref_lang")
	private List<String> languages;
	
	@MapsId
	@OneToOne
	@JoinColumn(name="mobileno_hash")
	@JsonIgnore
	private UserProfile userProfile;

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public String getMobileno_hash() {
		return mobileno_hash;
	}

	public void setMobileno_hash(String mobileno_hash) {
		this.mobileno_hash = mobileno_hash;
	}

	public String getAccomodation_type() {
		return accomodation_type;
	}

	public void setAccomodation_type(String accomodation_type) {
		this.accomodation_type = accomodation_type;
	}

	public List<String> getDestination_types() {
		return destination_types;
	}

	public void setDestination_types(List<String> destination_types) {
		this.destination_types = destination_types;
	}

	public List<String> getCuisine_preferences() {
		return cuisine_preferences;
	}

	public void setCuisine_preferences(List<String> cuisine_preferences) {
		this.cuisine_preferences = cuisine_preferences;
	}

	public String getVehicle_type() {
		return vehicle_type;
	}

	public void setVehicle_type(String vehicle_type) {
		this.vehicle_type = vehicle_type;
	}


	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	@Override
	public int hashCode() {
		return Objects.hash(destination_types, cuisine_preferences, languages, mobileno_hash, accomodation_type, userProfile,
				vehicle_type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserPreferences other = (UserPreferences) obj;
		return Objects.equals(destination_types, other.destination_types)
				&& Objects.equals(cuisine_preferences, other.cuisine_preferences)
				&& Objects.equals(languages, other.languages) && Objects.equals(mobileno_hash, other.mobileno_hash)
				&& Objects.equals(accomodation_type, other.accomodation_type) && Objects.equals(userProfile, other.userProfile)
				&& Objects.equals(vehicle_type, other.vehicle_type);
	}
}
