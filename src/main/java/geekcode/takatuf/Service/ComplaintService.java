package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.complaint.ComplaintDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import geekcode.takatuf.Enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

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

        public ComplaintResponse reviewComplaint(Long complaintId, Long reviewerId, ComplaintReviewRequest request) {
                Complaint complaint = complaintRepository.findById(complaintId)
                                .orElseThrow(() -> new BadRequestException("Complaint not found"));

                User reviewer = userRepository.findById(reviewerId)
                                .orElseThrow(() -> new BadRequestException("Reviewer not found"));

                if (reviewer.getType() != UserType.ADMIN) {
                        throw new UnauthorizedException("Only admins can review complaints");
                }

                complaint.setStatus(request.getStatus());
                complaint.setDecision(request.getDecision());
                complaint.setReviewedBy(reviewer);
                complaint.setReviewedAt(LocalDateTime.now());

                return mapToResponse(complaintRepository.save(complaint));
        }

        public List<ComplaintResponse> getUserComplaints(Long userId) {
                return complaintRepository.findBySubmittedBy_Id(userId)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<ComplaintResponse> getComplaints(Long requesterId, Long targetUserId) {
                User requester = userRepository.findById(requesterId)
                                .orElseThrow(() -> new BadRequestException("Requester not found"));

                if (requester.getType() == UserType.ADMIN) {
                        List<Complaint> complaints = (targetUserId != null)
                                        ? complaintRepository.findBySubmittedBy_Id(targetUserId)
                                        : complaintRepository.findAll();

                        return complaints.stream().map(this::mapToResponse).collect(Collectors.toList());
                }

                if (targetUserId != null && !targetUserId.equals(requester.getId())) {
                        throw new UnauthorizedException("You are not authorized to view complaints of other users");
                }

                return complaintRepository.findBySubmittedBy_Id(requester.getId())
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public PaginatedResponse<ComplaintResponse> getComplaintsByUserTypePaginated(
                        Long requesterId,
                        UserType userType,
                        int page,
                        int perPage,
                        String q,
                        String sort,
                        String sortDir) {

                User requester = userRepository.findById(requesterId)
                                .orElseThrow(() -> new BadRequestException("Requester not found"));

                if (requester.getType() != UserType.ADMIN) {
                        throw new UnauthorizedException("Only admins can access complaints by user type");
                }

                Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

                Page<Complaint> pageResult = (q != null && !q.trim().isEmpty())
                                ? complaintRepository.findBySubmittedBy_TypeAndSubjectContainingIgnoreCase(userType, q,
                                                pageable)
                                : complaintRepository.findBySubmittedBy_Type(userType, pageable);

                List<ComplaintResponse> data = pageResult.map(this::mapToResponse).getContent();

                return new PaginatedResponse<>(data, pageResult.getTotalElements(), page, perPage);
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


@Transactional
public ComplaintResponse cancelComplaint(Long userId, Long complaintId) {
    Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new BadRequestException("Complaint not found"));

    User requester = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

    boolean isOwner = complaint.getSubmittedBy() != null
            && complaint.getSubmittedBy().getId().equals(requester.getId());
    boolean isAdmin = requester.getType() == UserType.ADMIN;

    if (!isOwner && !isAdmin) {
        throw new UnauthorizedException("You are not authorized to cancel this complaint");
    }

    if (complaint.getStatus() != ComplaintStatus.PENDING) {
        throw new BadRequestException("Only pending complaints can be cancelled");
    }

    complaint.setStatus(ComplaintStatus.CANCELLED);
    complaint.setDecision(null);
    complaint.setReviewedBy(null);
    complaint.setReviewedAt(null);

    Complaint saved = complaintRepository.save(complaint);
    return mapToResponse(saved);
}
}
