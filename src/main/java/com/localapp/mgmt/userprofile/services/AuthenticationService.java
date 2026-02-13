package com.localapp.mgmt.userprofile.services;

import java.util.Objects;
import java.util.Random;

import com.localapp.mgmt.userprofile.config.TwilioConfig;
import com.localapp.mgmt.userprofile.dto.AuthRequest;
import com.localapp.mgmt.userprofile.dto.EmailAuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.MessagingException;

/**
 * service to send otp and validate otp sent to registered email/mobile number
 */
@Service
public class AuthenticationService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TwilioConfig twilioConfig;

    private static final Logger logger =
            LoggerFactory.getLogger(AuthenticationService.class);

    private static final int OTP_MIN = 1000;
    private static final int OTP_MAX = 9999;

    /**
     * generates a four digit random otp
     *
     * @return otp
     */
    private String getRandomOtp() {
        return String.valueOf(new Random().nextInt(OTP_MAX - OTP_MIN + 1) + OTP_MIN);
    }

    /**
     * generates an otp and sends to registered mobile number
     *
     * @param mobileNo - mobile number of user/travel agent/admin
     */
    public void generateMobileOtp(long mobileNo) {
        String otp = getRandomOtp();
        Cache cache = Objects.requireNonNull(
                cacheManager.getCache("otpCache"),
                "otpCache not configured"
        );
        cache.put(mobileNo, otp);
        PhoneNumber to = new PhoneNumber("+91" + mobileNo);
        String message = "Please find the OTP to login into Travel With Locals App: " + otp;
        Message.creator(to, twilioConfig.getServiceId(), message).create();
        logger.info("OTP generated for mobile number :: {}", mobileNo);
    }

    /**
     * validates otp provided by the user which is sent to their registered mobile number
     *
     * @param authRequest - combination of mobile number and otp sent to it
     * @return boolean - valid or invalid otp
     */
    public boolean validateMobileOtp(AuthRequest authRequest) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache == null) return false;
        Cache.ValueWrapper wrapper = cache.get(authRequest.getMobileNo());
        if (wrapper == null) return false;
        String cachedOtp = (String) wrapper.get();
        if (cachedOtp != null && cachedOtp.equals(authRequest.getOtp())) {
            cache.evict(authRequest.getMobileNo());
            logger.info("OTP validated successfully for mobile :: {}",
                    authRequest.getMobileNo());
            return true;
        }
        return false;
    }

    /**
     * generates an otp and sends to registered email
     *
     * @param email - email of user/travel agent/admin
     * @throws MessagingException - exception thrown when otp cannot be sent to email
     */
    public void generateEmailOtp(String email)
            throws MessagingException {
        String otp = getRandomOtp();
        Cache cache = Objects.requireNonNull(
                cacheManager.getCache("otpCache"),
                "otpCache not configured"
        );
        cache.put(email, otp);
        emailService.sendEmail(email, "OTP to validate user", otp);
        logger.info("OTP generated for email :: {}", email);
    }

    /**
     * validates otp provided by the user which is sent to their registered email
     *
     * @param authRequest - combination of email and otp sent to it
     * @return boolean - valid or invalid otp
     */
    public boolean validateEmailOtp(EmailAuthRequest authRequest) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache == null) return false;
        Cache.ValueWrapper wrapper = cache.get(authRequest.getEmail());
        if (wrapper == null) return false;
        String cachedOtp = (String) wrapper.get();
        if (cachedOtp != null && cachedOtp.equals(authRequest.getOtp())) {
            logger.info("OTP validated successfully for email :: {}",
                    authRequest.getEmail());
            return true;
        }
        return false;
    }
}
