package de.rieckpil.courses.stubbing;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.rieckpil.courses.BannedUsersClient;
import de.rieckpil.courses.RegistrationService;
import de.rieckpil.courses.User;
import de.rieckpil.courses.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceBDDTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BannedUsersClient bannedUsersClient;

  @InjectMocks
  private RegistrationService cut;

  @Test
  void basicStubbingWithBDD() {

    BDDMockito
      .given(userRepository.findByUsername("duke"))
      .willReturn(new User());

    BDDMockito
      .given(userRepository.save(any(User.class)))
      .willAnswer(invocation -> {
        User user = invocation.getArgument(0);
        user.setId(42L);
        return user;
      });

    BDDMockito
      .given(userRepository.findByUsername("mike"))
      .willThrow(new RuntimeException("Error in DB"));

    Assertions.assertNotNull(userRepository.findByUsername("duke"));
    Assertions.assertNull(userRepository.findByUsername("DUKE"));
    Assertions.assertThrows(RuntimeException.class, () -> userRepository.findByUsername("mike"));
    Assertions.assertEquals(42L, userRepository.save(new User()).getId());
    }
  
}
