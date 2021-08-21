package de.rieckpil.courses.advanced;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import de.rieckpil.courses.JpaUserRepository;
import de.rieckpil.courses.User;

// since 3.5.0
class ConstructorMockTest {

	@Test
	void constructorMocking() {

		System.out.println(new JpaUserRepository().findByUsername("mike"));

		try (MockedConstruction<JpaUserRepository> mocked = Mockito
				.mockConstruction(JpaUserRepository.class)) {

			JpaUserRepository jpaUserRepository = new JpaUserRepository();

			Mockito.when(jpaUserRepository.findByUsername("duke")).thenReturn(new User());

			assertNotNull(jpaUserRepository.findByUsername("duke"));

			Mockito.verify(jpaUserRepository).findByUsername("duke");
			
			System.out.println(jpaUserRepository.findByUsername("duke"));
		}

		System.out.println(new JpaUserRepository().findByUsername("duke"));
	}

	@Test
	void constructorMockingWithDirectStubbing() {
		// try with resources
		try (MockedConstruction<JpaUserRepository> mocked = Mockito.mockConstruction(JpaUserRepository.class,
				(mock, context) -> Mockito.when(mock.findByUsername("duke")).thenReturn(new User()))) {
			JpaUserRepository jpaUserRepository = new JpaUserRepository();

			Assertions.assertNotNull(jpaUserRepository.findByUsername("duke"));

			Mockito.verify(jpaUserRepository).findByUsername("duke");
		}

	}
}
