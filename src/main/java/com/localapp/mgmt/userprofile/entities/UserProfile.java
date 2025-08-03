package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localapp.mgmt.userprofile.util.DateStringConverter;
import com.localapp.mgmt.userprofile.util.EncryptDecryptHelper;
import com.localapp.mgmt.userprofile.util.IsValidPhoneNumber;
import com.localapp.mgmt.userprofile.util.LongStringConverter;
import com.localapp.mgmt.userprofile.util.PatchableObject;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "user_profile")
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer" })
public class UserProfile extends PatchableObject {
	@Id
	@JsonIgnore
	private String mobileno_hash;
	@Convert(converter = EncryptDecryptHelper.class)
	@Nonnull
	private String first_name;
	@Convert(converter = EncryptDecryptHelper.class)
	private String last_name;
	@Convert(converter = EncryptDecryptHelper.class)
	private String email;
	@Convert(converter = LongStringConverter.class)
	@IsValidPhoneNumber(message = "invalid phone number")
	private long mobileno;
	@JsonIgnore
	private String email_hash;
	private boolean is_kyc_verified;
	@JsonIgnore
	private LocalDate created_date;
	@JsonIgnore
	private LocalDate updated_date;
	@Convert(converter = DateStringConverter.class)
	@Column(updatable = false)
	private LocalDate date_of_birth;
	private String profile_image_uri;
	private boolean isactive;
	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "role_id")
	@JsonIgnore
	private Role role;

	@OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL)
	private UserPreferences preferences;
	
	@OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> addressList;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "kycid", referencedColumnName = "kycid")
	private KycDetails kycDetails;

	public KycDetails getKycDetails() {
		return kycDetails;
	}

	public void setKycDetails(KycDetails kycDetails) {
		this.kycDetails = kycDetails;
	}

	public List<Address> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Address> addressList) {
		this.addressList = addressList;
	}

	public UserProfile() {
		super();
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

	public long getMobileno() {
		return mobileno;
	}

	public void setMobileno(long mobileno) {
		this.mobileno = mobileno;
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

	public boolean isIs_kyc_verified() {
		return is_kyc_verified;
	}

	public void setIs_kyc_verified(boolean is_kyc_verified) {
		this.is_kyc_verified = is_kyc_verified;
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

	public LocalDate getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(LocalDate date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public String getProfile_image_uri() {
		return profile_image_uri;
	}

	public void setProfile_image_uri(String profile_image_uri) {
		this.profile_image_uri = profile_image_uri;
	}

	public boolean isIsactive() {
		return isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(UserPreferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addressList, created_date, date_of_birth, email, email_hash, first_name, is_kyc_verified,
				isactive, last_name, mobileno, mobileno_hash, preferences, profile_image_uri, role, updated_date,kycDetails);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserProfile other = (UserProfile) obj;
		return Objects.equals(addressList, other.addressList) && Objects.equals(created_date, other.created_date)
				&& Objects.equals(date_of_birth, other.date_of_birth) && Objects.equals(email, other.email)
				&& Objects.equals(email_hash, other.email_hash) && Objects.equals(first_name, other.first_name)
				&& is_kyc_verified == other.is_kyc_verified && isactive == other.isactive
				&& Objects.equals(last_name, other.last_name) && mobileno == other.mobileno
				&& Objects.equals(mobileno_hash, other.mobileno_hash) && Objects.equals(preferences, other.preferences)
				&& Objects.equals(profile_image_uri, other.profile_image_uri) && Objects.equals(role, other.role)
				&& Objects.equals(updated_date, other.updated_date) && Objects.equals(kycDetails, other.kycDetails);
	}
}
