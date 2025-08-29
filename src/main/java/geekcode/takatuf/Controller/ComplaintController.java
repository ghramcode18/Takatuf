package geekcode.takatuf.Controller;

import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.ComplaintService;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.complaint.ComplaintDto.*;
import lombok.RequiredArgsConstructor;
import geekcode.takatuf.Entity.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Exception.Types.BadRequestException;
import java.util.List;
import geekcode.takatuf.Enums.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @PostMapping("/submit")
    public ResponseEntity<ComplaintResponse> submitComplaint(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ComplaintRequest request) {

        User user = getAuthenticatedUser(userDetails);
        ComplaintResponse response = complaintService.submitComplaint(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/review/{id}")
    public ResponseEntity<ComplaintResponse> reviewComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ComplaintReviewRequest request) {

        User user = getAuthenticatedUser(userDetails);
        ComplaintResponse response = complaintService.reviewComplaint(id, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        List<ComplaintResponse> complaints = complaintService.getUserComplaints(user.getId());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getComplaints(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long userId) {

        User user = getAuthenticatedUser(userDetails);
        List<ComplaintResponse> complaints = complaintService.getComplaints(user.getId(), userId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/by-user-type")
    public ResponseEntity<PaginatedResponse<ComplaintResponse>> getComplaintsByUserTypePaginated(
            @RequestParam UserType userType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(name = "sort_dir", defaultValue = "DESC") String sortDir,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        PaginatedResponse<ComplaintResponse> response = complaintService.getComplaintsByUserTypePaginated(
                user.getId(), userType, page, perPage, q, sort, sortDir);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ComplaintResponse> cancelComplaint(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long complaintId) {

        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        ComplaintResponse resp = complaintService.cancelComplaint(userId, complaintId);
        return ResponseEntity.ok(resp);
    }

}
