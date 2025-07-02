package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.ComplaintService;
import geekcode.takatuf.dto.complaint.ComplaintDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping("/submit")
    public ResponseEntity<ComplaintResponse> submitComplaint(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ComplaintRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        ComplaintResponse response = complaintService.submitComplaintByEmail(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/review/{id}")
    public ResponseEntity<ComplaintResponse> reviewComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ComplaintReviewRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        ComplaintResponse response = complaintService.reviewComplaintByEmail(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<ComplaintResponse> complaints = complaintService.getUserComplaintsByEmail(userDetails.getUsername());
        return ResponseEntity.ok(complaints);
    }
     @GetMapping("/all")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<ComplaintResponse> complaints = complaintService.getUserComplaintsByEmail(userDetails.getUsername());
        return ResponseEntity.ok(complaints);
    }
}
