package com.RideShare.MatchingService.Client;

import com.RideShare.MatchingService.DTOs.NearByDriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "LocationService", url = "${location.service.url}")
public interface LocationServiceClient {

    @GetMapping("/api/v1/locations/drivers/nearby")
    List<NearByDriverResponse> getNearByDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    );
}
