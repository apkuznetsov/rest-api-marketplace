package kuznetsov.marketplace.services.user;

import kuznetsov.marketplace.services.user.dto.UserAuthDto;

public interface UserAuthService {

  UserAuthDto getUserAuthByEmail(String userEmail);

  UserAuthDto confirmUserEmail(String userEmail);

  UserAuthDto registerCustomer(String email, String password);

}
