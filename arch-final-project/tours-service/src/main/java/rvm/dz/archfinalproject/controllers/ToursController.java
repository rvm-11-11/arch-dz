package rvm.dz.archfinalproject.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.ToursRepository;
import rvm.dz.archfinalproject.repositories.UserEntity;
import rvm.dz.archfinalproject.repositories.UsersRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToursController {

    private final ToursRepository toursRepository;
    private final UsersRepository usersRepository;
    private final ResponseEntity UNAUTHORIZED_RESPONSE =
            new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    private final ResponseEntity FORBIDDEN_RESPONSE =
                new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    private final String USER_ID_HEADER = "X-Auth-Request-User";


    @GetMapping("/toursSearchFilter") //TODO can be CQRS
    public ResponseEntity toursSearch(@RequestParam(required = false) String orderField,
                                       @RequestParam(required = false) String fromDestination) {
        List<TourEntity> filteredList = toursRepository.findAll().stream().filter(tour -> {
            if (fromDestination == null) {
                return true;
            } else {
                return tour.getFromDestination().equalsIgnoreCase(fromDestination);
            }
        }).collect(Collectors.toList());

        if(orderField != null && orderField.equals("price")) {
            filteredList.sort(Comparator.comparing(TourEntity::getPrice));
        }

        return ResponseEntity.ok(filteredList);
    }


    @GetMapping("/tours")
    public ResponseEntity getAllTours() {
        return ResponseEntity.ok(toursRepository.findAll());
    }

    @GetMapping("/tours/{tourId}")
    public ResponseEntity getTourById(@PathVariable Long tourId) {
        return ResponseEntity.ok(toursRepository.findById(tourId));
    }

    @PostMapping("/tours")
    public ResponseEntity createTour(@RequestHeader(USER_ID_HEADER) String userId,
                                     @RequestBody CreateUpdateTourRequest request) {

        log.info("POST /tours: {}", userId);

        if (isUserUnauthorized(userId)) {
            return UNAUTHORIZED_RESPONSE;
        } else if (isUserNotAdmin(userId)) {
            return FORBIDDEN_RESPONSE;
        } else {
            TourEntity createdTour = toursRepository.save(
                    TourEntity.builder()
                            .name(request.getName())
                            .description(request.getDescription())
                            .price(request.getPrice())
                            .fromDate(request.getFromDate().toInstant())
                            .toDate(request.getToDate().toInstant())
                            .fromDestination(request.getFromDestination())
                            .toDestination(request.getToDestination())
                            .hotelId(request.getHotelId())
                            .build()
            );

            return ResponseEntity.ok(Map.of("tourId", createdTour.getTourId()));
        }
    }

    private boolean isUserNotAdmin(String userId) {
        return ! usersRepository
                .findById(Long.valueOf(userId))
                .filter(userEntity -> userEntity.getRole().equals(UserEntity.Role.ADMIN)).isPresent();
    }

    private boolean isUserUnauthorized(String userId) {
        return userId == null || userId.isEmpty();
    }

    @PutMapping("/tours/{tourId}")
    public ResponseEntity updateTour(@RequestHeader(USER_ID_HEADER) String userId,
                                     @PathVariable Long tourId,
                                     @RequestBody CreateUpdateTourRequest request) {
        log.info("PUT /tours: {}", userId);

        if (isUserUnauthorized(userId)) {
            return UNAUTHORIZED_RESPONSE;
        } else if (isUserNotAdmin(userId)) {
            return FORBIDDEN_RESPONSE;
        } else {
            TourEntity updatedTour = toursRepository.save(
                    TourEntity.builder()
                            .tourId(tourId)
                            .name(request.getName())
                            .description(request.getDescription())
                            .price(request.getPrice())
                            .fromDate(request.getFromDate().toInstant())
                            .toDate(request.getToDate().toInstant())
                            .fromDestination(request.getFromDestination())
                            .toDestination(request.getToDestination())
                            .hotelId(request.getHotelId())
                            .build()
            );

            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("/tours/{tourId}")
    public ResponseEntity deleteTourById(@RequestHeader(USER_ID_HEADER) String userId,
                                         @PathVariable Long tourId) {
        log.info("DELETE /tours: {}", userId);

        if (isUserUnauthorized(userId)) {
            return UNAUTHORIZED_RESPONSE;
        } else if (isUserNotAdmin(userId)) {
            return FORBIDDEN_RESPONSE;
        } else {
            toursRepository.deleteById(tourId);

            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/resetTours")
    public String resetTours() {
        log.info("Calling resetOrders()");
        toursRepository.deleteAll();
        return "All tours removed";
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }

}
