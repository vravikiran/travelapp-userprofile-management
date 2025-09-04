package com.localapp.mgmt.userprofile.repostories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localapp.mgmt.userprofile.entities.UserPreferences;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {

}
