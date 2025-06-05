package com.example.bookingservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.BookingRequest;
import com.example.bookingservice.service.BookingService;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/booking")

public class BookingController {
  
  @Autowired
  private BookingService bookingService;

 @PostMapping("/book")
  public ResponseEntity<Map<String,String>>bookFight(@RequestBody BookingRequest request){
         String checkoutUrl = bookingService.bookFlight(request);
        Map<String, String> response = new HashMap<>();
        response.put("checkoutUrl", checkoutUrl);
        return ResponseEntity.ok(response);
  }


  @GetMapping("/myBooking")
  public ResponseEntity<List<Booking>> booking(@RequestParam String email){
  return ResponseEntity.ok(bookingService.getBooking(email));
  }




}
