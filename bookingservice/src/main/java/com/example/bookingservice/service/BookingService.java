package com.example.bookingservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bookingservice.dto.BookingEvent;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.BookingRequest;
import com.example.bookingservice.model.Flight;
import com.example.bookingservice.repo.BookingRepo;
import com.example.bookingservice.repo.FlightRepo;

@Service

public class BookingService {
  @Autowired
  private BookingRepo bookingRepo;
  @Autowired
  private FlightRepo flightRepo;
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private  RestTemplate restTemplate;

  // @Value("${rabbitmq.exchange.name3}")
  // private String paymentExchange;

  // @Value("${rabbitmq.routing.key.name3}")
  // private String paymentRoutingkey;

  public String bookFlight(BookingRequest request) {
    if (request.getFlightId() == null) {
      throw new IllegalArgumentException("Flight Id having issue");
    }
    Flight flight = flightRepo.findById(request.getFlightId()).orElseThrow(() -> new RuntimeException("Flight not found"));

    if (flight.getAvailableSeats() < request.getSeats()) {
      throw new RuntimeException("Not enough seats available");

    }
    // // flight.setAvailableSeats(flight.getAvailableSeats() - seats);

    // // flightRepo.save(flight);

    // double amount = flight.getPrice() * seats;
    // Booking booking = new Booking(null, flightId, email, seats, LocalDateTime.now(), amount, "PENDING",
    //     flight.getFlightNumber());



    // Booking saveBooking = bookingRepo.save(booking);

    Booking booking = new Booking();
    booking.setFlightId(request.getFlightId());
    booking.setUserEmail(request.getUserEmail());
    booking.setSeatsBooked(request.getSeats());
    booking.setPrice(request.getSeats() * 500); // Example logic
    booking.setStatus("PENDING");
    booking.setFlightNumber(flight.getFlightNumber());
    booking.setBookingTime(LocalDateTime.now());
    Booking saveBooking = bookingRepo.save(booking);

   




    BookingEvent event = new BookingEvent();
    event.setBookingId(saveBooking.getId());
    event.setUserEmail(saveBooking.getUserEmail());
    event.setSeatsBooked(saveBooking.getSeatsBooked());
    event.setFlightNumber(flight.getFlightNumber());
    event.setFlightId(saveBooking.getFlightId());
    event.setPrice(saveBooking.getPrice());
    event.setStatus(saveBooking.getStatus());


    ResponseEntity<Map> response = restTemplate.postForEntity(
        "http://localhost:8083/payment/create-checkout-session",
        event,
        Map.class);

    //  rabbitTemplate.convertAndSend(paymentExchange, paymentRoutingkey, event);

    // return saveBooking;

    Map<String, String> body = response.getBody();
    return body != null ? body.get("checkoutUrl") : "";   


  }

  public List<Booking> getBooking(String email) {
    return bookingRepo.findByUserEmail(email);
  }

}
