package kuznetsov.marketplace.services.user;

import kuznetsov.marketplace.services.exception.ServiceErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ServiceErrorCode {

  USER_NOT_FOUND(
      "User Not Found",
      "The specified user is not found in the system.",
      HttpStatus.NOT_FOUND),

  USER_ALREADY_EXISTS(
      "User Already Exists",
      "The specified user already exists in the system.",
      HttpStatus.BAD_REQUEST),

  EMAIL_ALREADY_CONFIRMED(
      "Email Already Confirmed",
      "The email address has already been confirmed.",
      HttpStatus.BAD_REQUEST);

  private final String name;
  private final String message;
  private final HttpStatus httpStatus;

}