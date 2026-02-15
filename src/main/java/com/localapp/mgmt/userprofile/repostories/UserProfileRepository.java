package com.localapp.mgmt.userprofile.repostories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.localapp.mgmt.userprofile.entities.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    @Query(value = "select case when count(u) > 0 then true else false end from UserProfile u where u.emailHash =:email_hash")
    public boolean isUserExistsWithEmail(String email_hash);

    @Query(value = "select u from UserProfile u where u.emailHash =:email_hash")
    public UserProfile getUserProfileByEmail(String email_hash);
}
