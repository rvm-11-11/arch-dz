package rvm.dz.archfinalproject.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.ToursRepository;

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
    public ResponseEntity createTour(@RequestBody CreateUpdateTourRequest request) {
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

        return ResponseEntity.ok( Map.of("tourId", createdTour.getTourId()) );
    }

    @PutMapping("/tours/{tourId}")
    public ResponseEntity updateTour(@PathVariable Long tourId, @RequestBody CreateUpdateTourRequest request) {
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

    @DeleteMapping("/tours/{tourId}")
    public ResponseEntity deleteTourById(@PathVariable Long tourId) {
        toursRepository.deleteById(tourId);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }

}
