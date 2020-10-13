package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.persistence.MultiplicationRepository;
import microservices.book.socialmultiplication.persistence.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.persistence.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class MultiplicationServiceImplTest {
    @Mock
    private RandomGeneratorService randomGeneratorService;
    @Mock
    private MultiplicationResultAttemptRepository attemptRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventDispatcher eventDispatcher;
    @Mock
    private MultiplicationRepository multiplicationRepository;
    private MultiplicationServiceImpl multiplicationServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository, userRepository, multiplicationRepository, eventDispatcher);
    }

    @Test
    public void createRandomMultiplicationTest(){
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);
        //when
        Multiplication multiplication = multiplicationServiceImpl.createRandMultiplication();

        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(30);
    }

    @Test
    public void checkCorrectAttemptTest(){
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("Jhon_doe");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);
        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(attempt.getId(),
                attempt.getUser().getId(), true);
        given(userRepository.findByAlias("Jhon_doe")).willReturn(Optional.empty());
        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, attemptResult);


        assertThat(attemptResult).isTrue();
        verify(attemptRepository).save(verifiedAttempt);
        verify(eventDispatcher).send(eq(event));
    }


    @Test
    public void checkWrongAttemptTest(){
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("Jhon_doe");
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        given(userRepository.findByAlias("Jhon_doe")).willReturn(Optional.empty());
        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);
        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3010, attemptResult);

        assertThat(attemptResult).isFalse();
        verify(attemptRepository).save(verifiedAttempt);
    }

    @Test
    public void retrieveStatsTest(){
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("Jhon_doe");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3051, false);
        List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);

        given(userRepository.findByAlias("Jhon_doe")).willReturn(Optional.empty());
        given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("Jhon_doe")).willReturn(latestAttempts);

        List<MultiplicationResultAttempt> latestAttemptResult = multiplicationServiceImpl.getStatsForUser("Jhon_doe");
        assertThat(latestAttemptResult).isEqualTo(latestAttempts);
    }


}