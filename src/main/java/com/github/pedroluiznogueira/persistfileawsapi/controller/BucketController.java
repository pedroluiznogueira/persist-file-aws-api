package com.github.pedroluiznogueira.persistfileawsapi.controller;

import com.github.pedroluiznogueira.persistfileawsapi.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/bucket")
public class BucketController {

    // dependency injection to use bucket service
    @Autowired
    private BucketService bucketService;

    // end-point that recieves the files
    @PostMapping("/push")
    public ResponseEntity<String> pushFile(@RequestPart(value = "file") MultipartFile file) throws IOException {
        // recieving file and passing it along to the pushFile() method in bucket service

        String resp = bucketService.pushFile(file);
        if (resp == null) return new ResponseEntity("something went wrong", HttpStatus.BAD_REQUEST);

        return new ResponseEntity(resp, HttpStatus.OK);
    }
}
