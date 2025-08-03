package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
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
	@JoinColumn(name = "mobileno_hash")
	@JsonIgnore
	private UserProfile userProfile;
	@JsonIgnore
	private LocalDate created_date;
	@JsonIgnore
	private LocalDate updated_date;

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

	public Address() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress_type() {
		return address_type;
	}

	public void setAddress_type(String address_type) {
		this.address_type = address_type;
	}

	public String getAddress_line_1() {
		return address_line_1;
	}

	public void setAddress_line_1(String address_line_1) {
		this.address_line_1 = address_line_1;
	}

	public String getAddress_line_2() {
		return address_line_2;
	}

	public void setAddress_line_2(String address_line_2) {
		this.address_line_2 = address_line_2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address_line_1, address_line_2, address_type, city, country, created_date, id, state,
				updated_date, userProfile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(address_line_1, other.address_line_1)
				&& Objects.equals(address_line_2, other.address_line_2)
				&& Objects.equals(address_type, other.address_type) && Objects.equals(city, other.city)
				&& Objects.equals(country, other.country) && Objects.equals(created_date, other.created_date)
				&& id == other.id && Objects.equals(state, other.state)
				&& Objects.equals(updated_date, other.updated_date) && Objects.equals(userProfile, other.userProfile);
	}
}
