package microservices.book.gamification.service;

import microservices.book.gamification.client.MultiplicationResultAttemptClient;
import microservices.book.gamification.client.dto.MultiplicationResultAttempt;
import microservices.book.gamification.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class GameServiceImplTest {

    private GameServiceImpl gameServiceImpl;
    @Mock
    private ScoreCardRepository scoreCardRepository;
    @Mock
    private BadgeCardRepository badgeCardRepository;
    @Mock
    private MultiplicationResultAttemptClient attemptClient;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        gameServiceImpl = new GameServiceImpl(scoreCardRepository, badgeCardRepository, attemptClient);
    }
    @Test
    public void processFirstCorrectAttemptTest(){
        Long userId = 1L; Long attemptId = 8L; int totalScore = 10;
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        //stub
        given(scoreCardRepository.getTotalScoreForUser(userId))
                .willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId))
                .willReturn(Collections.singletonList(scoreCard));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId))
                .willReturn(Collections.emptyList());
        given(attemptClient.retrieveMultiplicationResultAttemptById(attemptId))
                .willReturn(new MultiplicationResultAttempt(
                        "Jhon",20,20,400,true
                ));
        GameStats iteration = gameServiceImpl.newAttemptForUser(userId, attemptId, true);

        assertThat(iteration.getScore()).isEqualTo(scoreCard.getScore());
        assertThat(iteration.getBadges()).containsOnly(Badge.FIRST_WON);
    }

    @Test
    public void processCorrectAttemptForScoreBadgeTest(){
        Long userId = 1L; Long attemptId = 29L; int totalScore = 100;
        BadgeCard firstWonBadge = new BadgeCard(userId, Badge.FIRST_WON);
        given(scoreCardRepository.getTotalScoreForUser(userId))
                .willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId))
                .willReturn(createNScoreCards(10, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId))
                .willReturn(Collections.singletonList(firstWonBadge));
        given(attemptClient.retrieveMultiplicationResultAttemptById(attemptId))
                .willReturn(new MultiplicationResultAttempt(
                        "Jhon",20,20,400,true
                ));
        GameStats iteration = gameServiceImpl.newAttemptForUser(userId, attemptId, true);

        assertThat(iteration.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(iteration.getBadges()).containsOnly(Badge.BRONZE_MULTIPLICATOR);
    }

    private List<ScoreCard> createNScoreCards(int n, Long userId) {
        return IntStream.range(0, n)
                .mapToObj(i -> new ScoreCard(userId, (long) i))
                .collect(Collectors.toList());
    }
}