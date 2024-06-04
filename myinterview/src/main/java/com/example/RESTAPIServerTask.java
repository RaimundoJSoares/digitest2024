package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@SpringBootApplication
@RestController
public class RESTAPIServerTask {

    private S3Client s3Client;

    public RESTAPIServerTask() {
        // Create AWS credentials
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("eusouRAIMUNDOJUNIOR", "UIBDIUBSADBASBDASBFBAFBAFBAS");

        // Create static credentials provider
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCreds);

        // Create S3 client
        s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @GetMapping("/buckets")
    public String listBuckets() {
        // List all buckets in S3
        return s3Client.listBuckets().toString();
    }

    @PostMapping("/buckets/{bucketName}")
    public String createBucket(@PathVariable String bucketName) {
        // Create a new bucket in S3
        s3Client.createBucket(bucketName);
        return "Bucket created successfully";
    }

    @PutMapping("/buckets/{bucketName}/objects/{objectKey}")
    public String uploadObject(@PathVariable String bucketName, @PathVariable String objectKey, @RequestBody String objectContent) {
        // Upload an object to S3
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request, objectContent);
        return "Object uploaded successfully";
    }

    @GetMapping("/buckets/{bucketName}/objects/{objectKey}")
    public String getObject(@PathVariable String bucketName, @PathVariable String objectKey) {
        // Get an object from S3
        S3Object object = s3Client.getObject(bucketName, objectKey);
        return object.toString();
    }

    @DeleteMapping("/buckets/{bucketName}/objects/{objectKey}")
    public String deleteObject(@PathVariable String bucketName, @PathVariable String objectKey) {
        // Delete an object from S3
        s3Client.deleteObject(bucketName, objectKey);
        return "Object deleted successfully";
    }

    public static void main(String[] args) {
        SpringApplication.run(RESTAPIServerTask.class, args);
    }
}