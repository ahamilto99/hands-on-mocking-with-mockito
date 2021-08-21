package de.rieckpil.courses.verification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.rieckpil.courses.Address;
import de.rieckpil.courses.BannedUsersClient;
import de.rieckpil.courses.RegistrationService;
import de.rieckpil.courses.User;
import de.rieckpil.courses.UserRepository;
import de.rieckpil.courses.Utils;

@ExtendWith(MockitoExtension.class)
// when the stubbing strictness is set to Strictness.STRICT_STUBS (default), we must use all stubbed methods
@MockitoSettings(strictness = Strictness.WARN)
public class RegistrationServiceVerificationTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BannedUsersClient bannedUsersClient;

	@Captor
	private ArgumentCaptor<User> userArgumentCaptor; // User is the type of the stubbed method's argument

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	@Captor
	private ArgumentCaptor<Address> addressArgumentCaptor;

	@InjectMocks
	private RegistrationService cut;

	@Test
	void basicVerification() {

		Mockito.when(
				bannedUsersClient.isBanned(ArgumentMatchers.eq("duke"), ArgumentMatchers.any(Address.class)))
				.thenReturn(true);

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> cut.registerUser("duke", Utils.createContactInformation("duke@mockito.org")));

		Mockito.verify(bannedUsersClient).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.argThat(address -> address.getCity().equals("Berlin")));
		Mockito.verify(bannedUsersClient, Mockito.times(1)).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.any(Address.class));
		Mockito.verify(bannedUsersClient, Mockito.atLeastOnce()).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.any(Address.class));
		Mockito.verify(bannedUsersClient, Mockito.atMost(1)).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.any(Address.class));
		Mockito.verify(bannedUsersClient, Mockito.never()).bannedUserId();

		Mockito.verifyNoMoreInteractions(bannedUsersClient, userRepository); // seldom use this

		// outputs the description if the test fails
		// Mockito.verify(bannedUsersClient, Mockito.description("Nobody checked for Mike"))
		// .isBanned(ArgumentMatchers.eq("Mike"), ArgumentMatchers.any(Address.class));
	}

	@Test
	void additionalVerificationOptions() {

		Mockito.when(
				bannedUsersClient.isBanned(ArgumentMatchers.eq("duke"), ArgumentMatchers.any(Address.class)))
				.thenReturn(false);
		Mockito.when(userRepository.findByUsername("duke")).thenReturn(null);
		Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId(42L);
			return user;
		});

		User user = cut.registerUser("duke", Utils.createContactInformation("duke@mockito.org"));

		Assertions.assertNotNull(user);

		Mockito.verify(userRepository).save(ArgumentMatchers.any(User.class));
		Mockito.verify(userRepository).findByUsername("duke");
		Mockito.verify(bannedUsersClient).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.any(Address.class));

		InOrder inOrder = Mockito.inOrder(userRepository, bannedUsersClient);

		inOrder.verify(bannedUsersClient).isBanned(ArgumentMatchers.eq("duke"),
				ArgumentMatchers.any(Address.class));
		inOrder.verify(userRepository).findByUsername("duke");
		inOrder.verify(userRepository).save(ArgumentMatchers.any(User.class));
	}

	@Test
	void argumentCaptorsWhenVerifying() {

		Mockito.when(
				bannedUsersClient.isBanned(ArgumentMatchers.eq("duke"), ArgumentMatchers.any(Address.class)))
				.thenReturn(false);
		Mockito.when(userRepository.findByUsername("duke")).thenReturn(null);
		Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(new User());

		User user = cut.registerUser("duke", Utils.createContactInformation());

		Assertions.assertNotNull(user);

		Mockito.verify(userRepository).save(userArgumentCaptor.capture());
		Mockito.verify(bannedUsersClient).isBanned(ArgumentMatchers.eq("duke"),
				addressArgumentCaptor.capture());

		User userToStore = userArgumentCaptor.getValue();

		Assertions.assertNotNull(userToStore.getUsername());
		Assertions.assertNotNull(userToStore.getCreatedAt());
		Assertions.assertTrue(userToStore.getEmail().contains("@myorg.io"));
		Assertions.assertNull(userToStore.getId());
	}

}
