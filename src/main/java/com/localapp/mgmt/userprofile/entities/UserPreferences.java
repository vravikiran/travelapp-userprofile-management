package com.localapp.mgmt.userprofile.entities;

import java.util.List;

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
import lombok.Data;

@Table(name = "user_preferences")
@Entity
@Data
public class UserPreferences {
	@Id
	@JsonIgnore
	private String mobile_no_hash;
	@ElementCollection
	@CollectionTable(name="user_destination_preferences", joinColumns = @JoinColumn(name="mobile_no_hash"))
	@Column(name="dest_type_pref")
	private List<String> destination_types;
	@ElementCollection
	@CollectionTable(name="user_cuisine_preferences", joinColumns = @JoinColumn(name="mobile_no_hash"))
	@Column(name="cuisine_type")
	private List<String> cuisine_preferences;
	private String vehicle_type;
	private String accommodation_type;
	@ElementCollection
	@CollectionTable(name="user_language_preferences", joinColumns = @JoinColumn(name="mobile_no_hash"))
	@Column(name="pref_lang")
	private List<String> languages;
	
	@MapsId
	@OneToOne
	@JoinColumn(name="mobile_no_hash")
	@JsonIgnore
	private UserProfile userProfile;
}
