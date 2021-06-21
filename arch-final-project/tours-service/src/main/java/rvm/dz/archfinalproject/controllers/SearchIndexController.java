package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.Event;
import rvm.dz.archfinalproject.repositories.ToursRepository;
import rvm.dz.archfinalproject.repositories.UsersRepository;
import rvm.dz.archfinalproject.repositories.SuccessfulOrdersRepository;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.UserEntity;
import rvm.dz.archfinalproject.repositories.SuccessfulOrderEntity;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SearchIndexController {

    private final ToursRepository toursRepository;
    private final UsersRepository usersRepository;
    private final SuccessfulOrdersRepository successfulOrdersRepository;
    private final ResponseEntity UNAUTHORIZED_RESPONSE =
            new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    private final ResponseEntity FORBIDDEN_RESPONSE =
                new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    private final String USER_ID_HEADER = "X-Auth-Request-User";

    @KafkaListener(topics = "travel-agency-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
        if (event.getEventType().equals(Event.EventType.ORDER_OVERALL_APPROVED)) {
            Optional<TourEntity> tourEntityOptional =
                    toursRepository.findById(parseLong(event.getEventData().get("tourId")));
            Optional<UserEntity> userEntityOptional =
                    usersRepository.findById(event.getEventData().get("userId"));

            successfulOrdersRepository.save(SuccessfulOrderEntity
                    .builder()
                    .tourId(tourEntityOptional.map(TourEntity::getTourId).orElse(-1L))
                    .tourName(tourEntityOptional.map(TourEntity::getName).orElse(""))
                    .tourDescription(tourEntityOptional.map(TourEntity::getDescription).orElse(""))
                    .price(tourEntityOptional.map(TourEntity::getPrice).orElse(-1L))
                    .fromDate(tourEntityOptional.map(TourEntity::getFromDate).orElse(null))
                    .toDate(tourEntityOptional.map(TourEntity::getToDate).orElse(null))
                    .fromDestination(tourEntityOptional.map(TourEntity::getFromDestination).orElse(""))
                    .toDestination(tourEntityOptional.map(TourEntity::getToDestination).orElse(""))
                    .hotelId(tourEntityOptional.map(TourEntity::getHotelId).orElse(-1L))
                    .userId(userEntityOptional.map(UserEntity::getUserId).orElse(""))
//                    .userName(userEntityOptional.map(UserEntity::getName).orElse(""))
                    .overallOrderStatus(SuccessfulOrderEntity.Status.APPROVED)
                    .build());
        }
    }

    @GetMapping("/successfulOrders")
    public ResponseEntity successfulOrders(@RequestParam(required = false) String orderField,
                                       @RequestParam(required = false) String fromDestination) {
        return ResponseEntity.ok(successfulOrdersRepository.findAll());
    }


    @PostMapping("/successfulOrders/reset")
    public String resetSuccessfulOrders() {
        log.info("Calling resetSuccessfulOrders()");
        successfulOrdersRepository.deleteAll();
        return "All successfulOrders removed";
    }

}
