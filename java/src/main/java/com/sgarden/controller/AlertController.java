package com.sgarden.controller;

import com.sgarden.dto.AlertResponse;
import com.sgarden.dto.ErrorResponse;
import com.sgarden.dto.ThresholdRequest;
import com.sgarden.service.AlertService;
import static com.sgarden.util.ErrorMessages.THRESHOLD_INVALID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts() {
        return ResponseEntity.ok(alertService.getAlerts());
    }

    @PutMapping("/threshold")
    public ResponseEntity<?> setThreshold(@RequestBody ThresholdRequest request) {
        if (request.getThreshold() == null || request.getThreshold() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(THRESHOLD_INVALID));
        }
        int newThreshold = alertService.setThreshold(request.getThreshold());
        return ResponseEntity.ok(Map.of("threshold", newThreshold));
    }
}
