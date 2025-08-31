package com.localapp.mgmt.userprofile.services;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.localapp.mgmt.userprofile.entities.KycDetails;
import com.localapp.mgmt.userprofile.entities.TravelAgentKycDetails;
import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.repostories.TravelAgentProfileRepository;
import com.localapp.mgmt.userprofile.repostories.UserProfileRepository;
import com.localapp.mgmt.userprofile.util.Constants;
import com.localapp.mgmt.userprofile.util.HashGenerator;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileService {
    @Autowired
    S3Client s3Client;
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    TravelAgentProfileRepository travelAgentProfileRepository;
    Logger logger = LoggerFactory.getLogger(FileService.class);

    public String uploadUserProfileImage(MultipartFile file, long mobileNo, String bucketName)
            throws IOException, UserNotFoundException {
        logger.info("Upload image for user profile with mobile number ::" + mobileNo);
        if (userProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo))) {
            String key = Constants.CUSTOMER + "/" + mobileNo + "/" + LocalDateTime.now() + "_"
                    + file.getOriginalFilename();
            URL url = imageUploadToS3(file, bucketName, key);
            logger.info("url of the uploaded image of user:: {}", url.toString());
            UserProfile userProfile = userProfileRepository
                    .getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
            userProfile.setProfile_image_uri(url.toString());
            userProfile.setUpdated_date(LocalDate.now());
            userProfileRepository.save(userProfile);
            logger.info("profile image uploaded successfully");
            return url.toString();
        } else {
            throw new UserNotFoundException("user not found with given mobile number : " + mobileNo);
        }
    }

    public String uploadTravelAgentProfileImage(MultipartFile file, long mobileNo, String bucketName)
            throws IOException, UserNotFoundException {
        logger.info("Upload image for travel agent profile with mobile number ::" + mobileNo);
        if (travelAgentProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo))) {
            String key = Constants.TRAVEL_AGENT + "/" + mobileNo + "/" + LocalDateTime.now() + "_"
                    + file.getOriginalFilename();
            URL url = imageUploadToS3(file, bucketName, key);
            logger.info("url of the uploaded image :: " + url.toString());
            TravelAgentProfile agentProfile = travelAgentProfileRepository
                    .getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
            agentProfile.setImage_uri(url.toString());
            agentProfile.setUpdated_date(LocalDate.now());
            travelAgentProfileRepository.save(agentProfile);
            logger.info("Travel Agent profile image uploaded successfully");
            return url.toString();
        } else {
            throw new UserNotFoundException("Travel Agent not found with given mobile number : " + mobileNo);
        }
    }

    public String uploadTravelAgentKycImage(MultipartFile file, long mobileNo, String bucketName, boolean isFrontImage)
            throws IOException, UserNotFoundException {
        if (travelAgentProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo))) {
            TravelAgentProfile agentProfile = travelAgentProfileRepository
                    .getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
            String key = Constants.TRAVEL_AGENT + "/" + mobileNo + "/" + LocalDateTime.now() + "_"
                    + file.getOriginalFilename();
            URL url = imageUploadToS3(file, bucketName, key);
            TravelAgentKycDetails agentKycDetails = agentProfile.getAgentKycDetails();
            if (isFrontImage) {
                agentKycDetails.setKyc_img_url1(url.toString());
            } else {
                agentKycDetails.setKyc_img_url2(url.toString());
            }
            agentKycDetails.setKyc_date(LocalDate.now());
            agentProfile.setAgentKycDetails(agentKycDetails);
            agentProfile.setUpdated_date(LocalDate.now());
            travelAgentProfileRepository.save(agentProfile);
            return url.toString();
        } else {
            throw new UserNotFoundException("No travel agent exists with given mobile number : " + mobileNo);
        }
    }

    public String uploadUserKycImage(MultipartFile file, long mobileNo, String bucketName, boolean isFrontImage)
            throws UserNotFoundException, IOException {
        if (userProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo))) {
            UserProfile userProfile = userProfileRepository
                    .getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
            String key = Constants.CUSTOMER + "/" + mobileNo + "/" + LocalDateTime.now() + "_"
                    + file.getOriginalFilename();
            URL url = imageUploadToS3(file, bucketName, key);
            KycDetails kycDetails = userProfile.getKycDetails();
            if (isFrontImage) {
                kycDetails.setKyc_img_url1(url.toString());
            } else {
                kycDetails.setKyc_img_url2(url.toString());
            }
            kycDetails.setKyc_date(LocalDate.now());
            userProfile.setKycDetails(kycDetails);
            userProfileRepository.save(userProfile);
            return url.toString();
        } else {
            throw new UserNotFoundException("user not found with given mobile number : " + mobileNo);
        }
    }

    public URL imageUploadToS3(MultipartFile file, String bucketName, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getBytes().length));
        GetUrlRequest request = GetUrlRequest.builder().bucket(bucketName).key(key).build();
        return s3Client.utilities().getUrl(request);
    }
}
