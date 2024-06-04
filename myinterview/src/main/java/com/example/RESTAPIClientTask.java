package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class RESTAPIClientTask {

    public static void main(String[] args) throws IOException, InterruptedException {
        // API endpoint
        String apiUrl = "https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda";

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create a GET request
        HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(apiUrl))
               .GET()
               .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray records = jsonObject.getJSONArray("records");

        // Count the number of records for each gender
        Map<String, Integer> genderCounts = new HashMap<>();
        for (int i = 0; i < records.length(); i++) {
            JSONObject record = records.getJSONObject(i);
            String gender = record.getString("gender");
            genderCounts.put(gender, genderCounts.getOrDefault(gender, 0) + 1);
        }

        // Print the counts
        for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
            System.out.println("Gender: " + entry.getKey() + ", Count: " + entry.getValue());
        }

        // Bonus: generate a CSV file and upload it to AWS S3
        String filename = "john_doe.csv"; // replace with your entire name, separated by underscores
        String bucketName = "interview-digiage";

        // Create a CSV file
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Gender,Count\n");
        for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
            csvContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }

        // Upload the CSV file to AWS S3
        S3Client s3Client = S3Client.create();
        PutObjectRequest requestS3 = PutObjectRequest.builder()
               .bucket(bucketName)
               .key(filename)
               .contentType("text/csv")
               .build();

        s3Client.putObject(requestS3, RequestBody.fromString(csvContent.toString()));
        System.out.println("CSV file uploaded to AWS S3: " + filename);
    }
}