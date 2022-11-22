package kuznetsov.marketplace.services.customer.publisher;

import kuznetsov.marketplace.services.customer.events.CustomerRegistrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerPublisherImpl implements CustomerPublisher {

  private final ApplicationEventPublisher publisher;

  public void publishCustomerRegistrationEvent(String customerEmail) {
    publisher.publishEvent(new CustomerRegistrationEvent(customerEmail));
  }

}