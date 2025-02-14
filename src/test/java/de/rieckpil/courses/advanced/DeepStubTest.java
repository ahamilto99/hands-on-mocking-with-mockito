package de.rieckpil.courses.advanced;

import de.rieckpil.courses.Address;
import de.rieckpil.courses.ContactInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeepStubTest {

  @Test
  void withoutDeepStubs() {

    ContactInformation contactInfo = Mockito.mock(ContactInformation.class);
    Address address = Mockito.mock(Address.class);

    Mockito.when(contactInfo.getAddress()).thenReturn(address);
    Mockito.when(address.getCity()).thenReturn("Berlin");

    System.out.println(contactInfo.getAddress().getCity());
  }

  @Test
  void deepStubs() {
    ContactInformation contactInfo = Mockito.mock(ContactInformation.class, Answers.RETURNS_DEEP_STUBS);

    Mockito.when(contactInfo.getAddress().getCity()).thenReturn("Berlin");

    System.out.println(contactInfo.getAddress().getCity());
  }
}
