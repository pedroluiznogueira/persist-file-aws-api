package com.github.pedroluiznogueira.persistfileawsapi.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class BucketService {

    // interface that has the method that pushes the new object to bucket
    private AmazonS3 amazonS3;

    // accessing our application.properties with environment variables
    @Value("${endpointUrl}")
    private String endpointUrl;
    @Value("${bucketName}")
    private String bucketName;
    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;
    @Value("${storedUrl}")
    private String storedUrl;
    @Value("${https}")
    private String https;

    // PostConstruct annotation is used on a method that needs to be executed
    // after dependency injection is done to perform any initialization
    @PostConstruct
    private void setCredentials() {
        // store our credentials in a AWSCredentials object

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.amazonS3 = new AmazonS3Client(credentials);
    }

    public String pushFile(MultipartFile multipartFile) throws IOException {
        // convert multipart file to file
        File file = convertMultipartFileToFile(multipartFile);

        // store filename
        String fileName = multipartFile.getOriginalFilename();

        // set up end-point url using credentials environment variables
        String destinationUrl = https + bucketName + storedUrl + "/" + fileName;

        // pushing file to bucket
        PutObjectResult resp =  uploadFileToBucket(fileName, file);
        file.delete();

        // error handling
        if (resp == null) return null;

        // returning where the location wich the file was stored in s3
        return destinationUrl;
    }

    // convert multipart file to file method
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    // push file to bucket
    private PutObjectResult uploadFileToBucket(String fileName, File file) {
        return amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
