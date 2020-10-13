package microservices.book.gamification.service;


import microservices.book.gamification.domain.GameStats;

/**
 * 게임화 서비스의 주요로직을 다루는 서비스
 */
public interface GameService {

    /**
     * 주어진 사용자가 제출한 답안을 처리
     * @param userId
     * @param attemptId
     * @param correct
     * @return 새로운 점수와 배지 카드를 포함한 {@link GameStats} 갹체
     */
    GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct);

    /**
     * 주어진 사용자의 게임 통계를 조회
     * @param userId
     * @return 사용자의 통계 정보
     */
    GameStats retrieveStatsForUser(Long userId);
}
