package com.localapp.mgmt.userprofile.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localapp.mgmt.userprofile.util.DateStringConverter;
import com.localapp.mgmt.userprofile.util.EncryptDecryptHelper;
import com.localapp.mgmt.userprofile.util.IsValidPhoneNumber;
import com.localapp.mgmt.userprofile.util.LongStringConverter;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Table(name = "user_profile")
@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class UserProfile {
    @Id
    @JsonIgnore
    private String mobileNoHash;
    @Convert(converter = EncryptDecryptHelper.class)
    @Nonnull
    private String firstName;
    @Convert(converter = EncryptDecryptHelper.class)
    private String lastName;
    @Convert(converter = EncryptDecryptHelper.class)
    private String email;
    @Convert(converter = LongStringConverter.class)
    @IsValidPhoneNumber(message = "invalid phone number")
    @Column(nullable = false)
    private long mobileNo;
    @JsonIgnore
    private String emailHash;
    private boolean isKycVerified;
    @JsonIgnore
    private LocalDate createdDate;
    @JsonIgnore
    private LocalDate updatedDate;
    @Convert(converter = DateStringConverter.class)
    @Column(updatable = false)
    private LocalDate dateOfBirth;
    private String profileImageUri;
    private boolean active;
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonIgnore
    private Role role;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private UserPreferences preferences;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addressList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kyc_id", referencedColumnName = "kyc_id")
    private KycDetails kycDetails;

}
