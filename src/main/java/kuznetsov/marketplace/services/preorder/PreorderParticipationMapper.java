package kuznetsov.marketplace.services.preorder;

import java.util.List;
import java.util.stream.Collectors;
import kuznetsov.marketplace.models.preorder.PreorderInfo;
import kuznetsov.marketplace.models.preorder.PreorderParticipation;
import kuznetsov.marketplace.models.preorder.PreorderParticipationStatus;
import kuznetsov.marketplace.models.user.Customer;
import kuznetsov.marketplace.services.preorder.dto.PreorderParticipantDto;
import kuznetsov.marketplace.services.preorder.dto.PreorderParticipantsDtoPage;
import org.springframework.data.domain.Page;

public interface PreorderParticipationMapper {

  default PreorderParticipation toPreorderParticipation(
      PreorderInfo preorderInfo, Customer customer) {

    return PreorderParticipation.builder()
        .preorderInfo(preorderInfo)
        .customer(customer)
        .status(PreorderParticipationStatus.CLIENT_AWAITING_PAYMENT)
        .build();
  }

  default PreorderParticipantDto toPreorderParticipantDto(
      PreorderParticipation customerParticipation) {

    Customer customer = customerParticipation.getCustomer();
    String customerEmail = customer.getUser().getEmail();

    return PreorderParticipantDto.builder()
        .email(customerEmail)
        .name(customer.getName())
        .address(customer.getAddress())
        .preorderedQuantity(1)
        .participationStatus(customerParticipation.getStatus())
        .build();
  }

  default PreorderParticipantsDtoPage toPreorderParticipationsDtoPage(
      Long preorderId, Page<PreorderParticipation> pagedPreorderParticipations) {

    List<PreorderParticipantDto> preorderParticipants = pagedPreorderParticipations.stream()
        .map(this::toPreorderParticipantDto)
        .collect(Collectors.toList());

    return PreorderParticipantsDtoPage.builder()
        .totalPreorderParticipants(pagedPreorderParticipations.getTotalElements())
        .totalPreorderParticipantsPages(pagedPreorderParticipations.getTotalPages())
        .preorderParticipantsMaxPageSize(pagedPreorderParticipations.getSize())
        .preorderParticipantsPageNumber(pagedPreorderParticipations.getNumber() + 1)
        .preorderId(preorderId)
        .preorderParticipants(preorderParticipants)
        .build();
  }

}
