package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "user_kycdetails")
@Entity
public class KycDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int kycid;
	private String kycimgurl1;
	private String kycimgurl2;
	private String document_type;
	private String document_no;
	@OneToOne(mappedBy = "kycDetails")
	@JsonIgnore
	private UserProfile userProfile;
	@JsonIgnore
	private LocalDate kyc_date;

	public KycDetails() {
		super();
	}

	public LocalDate getKyc_date() {
		return kyc_date;
	}

	public void setKyc_date(LocalDate kyc_date) {
		this.kyc_date = kyc_date;
	}

	public int getKycid() {
		return kycid;
	}

	public void setKycid(int kycid) {
		this.kycid = kycid;
	}

	public String getKycimgurl1() {
		return kycimgurl1;
	}

	public void setKycimgurl1(String kycimgurl1) {
		this.kycimgurl1 = kycimgurl1;
	}

	public String getKycimgurl2() {
		return kycimgurl2;
	}

	public void setKycimgurl2(String kycimgurl2) {
		this.kycimgurl2 = kycimgurl2;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getDocument_no() {
		return document_no;
	}

	public void setDocument_no(String document_no) {
		this.document_no = document_no;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(document_no, document_type, kycid, kycimgurl1, kycimgurl2, userProfile, kyc_date);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KycDetails other = (KycDetails) obj;
		return Objects.equals(document_no, other.document_no) && Objects.equals(document_type, other.document_type)
				&& kycid == other.kycid && Objects.equals(kycimgurl1, other.kycimgurl1)
				&& Objects.equals(kycimgurl2, other.kycimgurl2) && Objects.equals(userProfile, other.userProfile)
				&& Objects.equals(kyc_date, other.kyc_date);
	}
}
