package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.ComplaintStatus;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.complaint.ComplaintDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

        private final ComplaintRepository complaintRepository;
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;

        public ComplaintResponse submitComplaintByEmail(String email, ComplaintRequest request) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new BadRequestException("User not found"));
                return submitComplaint(user.getId(), request);
        }

        public ComplaintResponse reviewComplaintByEmail(Long complaintId, String email,
                        ComplaintReviewRequest request) {
                User admin = userRepository.findByEmail(email)
                                .orElseThrow(() -> new BadRequestException("Reviewer not found"));
                return reviewComplaint(complaintId, admin.getId(), request);
        }

        public List<ComplaintResponse> getUserComplaintsByEmail(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new BadRequestException("User not found"));
                return getUserComplaints(user.getId());
        }

        public ComplaintResponse submitComplaint(Long userId, ComplaintRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new BadRequestException("User not found"));

                Order order = null;
                if (request.getOrderId() != null) {
                        order = orderRepository.findById(request.getOrderId())
                                        .orElseThrow(() -> new BadRequestException("Order not found"));
                }

                Complaint complaint = Complaint.builder()
                                .subject(request.getSubject())
                                .details(request.getDetails())
                                .status(ComplaintStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .submittedBy(user)
                                .order(order)
                                .build();

                return mapToResponse(complaintRepository.save(complaint));
        }

        public ComplaintResponse reviewComplaint(Long complaintId, Long adminId, ComplaintReviewRequest request) {
                Complaint complaint = complaintRepository.findById(complaintId)
                                .orElseThrow(() -> new BadRequestException("Complaint not found"));

                User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new BadRequestException("Reviewer not found"));

                complaint.setStatus(request.getStatus());
                complaint.setDecision(request.getDecision());
                complaint.setReviewedBy(admin);
                complaint.setReviewedAt(LocalDateTime.now());

                return mapToResponse(complaintRepository.save(complaint));
        }

        public List<ComplaintResponse> getUserComplaints(Long userId) {
                return complaintRepository.findBySubmittedBy_Id(userId)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<ComplaintResponse> getAllComplaints() {
                return complaintRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private ComplaintResponse mapToResponse(Complaint complaint) {
                return ComplaintResponse.builder()
                                .id(complaint.getId())
                                .subject(complaint.getSubject())
                                .details(complaint.getDetails())
                                .status(complaint.getStatus())
                                .createdAt(complaint.getCreatedAt())
                                .reviewedAt(complaint.getReviewedAt())
                                .decision(complaint.getDecision())
                                .submittedBy(complaint.getSubmittedBy().getName())
                                .reviewedBy(complaint.getReviewedBy() != null ? complaint.getReviewedBy().getName()
                                                : null)
                                .orderId(complaint.getOrder() != null ? complaint.getOrder().getId() : null)
                                .build();
        }

}
