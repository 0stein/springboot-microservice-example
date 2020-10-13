package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.persistence.MultiplicationRepository;
import microservices.book.socialmultiplication.persistence.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

    private final RandomGeneratorService randomGeneratorService;
    private final MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;
    private final UserRepository userRepository;
    private final MultiplicationRepository multiplicationRepository;
    private final EventDispatcher eventDispatcher;
    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository multiplicationResultAttemptRepository,
                                     UserRepository userRepository,
                                     MultiplicationRepository multiplicationRepository,
                                     EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.multiplicationResultAttemptRepository = multiplicationResultAttemptRepository;
        this.userRepository = userRepository;
        this.multiplicationRepository = multiplicationRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA, factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(final MultiplicationResultAttempt attempt) {
        //해당 닉네임의 사용자가 존재하는지 확인
        Optional<User> user = userRepository.findByAlias(attempt.getUser().getAlias());
        //해당 곱셈이 이미 존재하는지 확인
        Optional<Multiplication> multiplication =
                multiplicationRepository.findByFactorAAndFactorB(
                        attempt.getMultiplication().getFactorA(),
                        attempt.getMultiplication().getFactorB());
        //조작된 답안(true 를 client 에서 전송한) 을 방지
        Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");
        //답안 체점
        boolean isCorrect = attempt.getResultAttempt() ==
                attempt.getMultiplication().getFactorA() * attempt.getMultiplication().getFactorB();
        //값 복사 및 optional 을 통한 값 중복 처리
        MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(
                user.orElseGet(attempt::getUser),
                multiplication.orElseGet(attempt::getMultiplication),
                attempt.getResultAttempt(), isCorrect);

        //답안을 저장
        multiplicationResultAttemptRepository.save(checkedAttempt);

        //이벤트로 결과를 전송
        eventDispatcher.send(new MultiplicationSolvedEvent(
                checkedAttempt.getId(),
                checkedAttempt.getUser().getId(),
                checkedAttempt.isCorrect()
        ));

        return isCorrect;
    }

    @Override
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
        return multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }

    @Override
    public MultiplicationResultAttempt getResultAttempt(final Long resultId){
        Optional<MultiplicationResultAttempt> result = multiplicationResultAttemptRepository.findById(resultId);
        return result.orElse(null);
    }
}
