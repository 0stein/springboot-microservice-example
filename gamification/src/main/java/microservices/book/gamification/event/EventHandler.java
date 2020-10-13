package microservices.book.gamification.event;

import lombok.extern.slf4j.Slf4j;
import microservices.book.gamification.service.GameService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventHandler {

    private final GameService gameService;

    @Autowired
    public EventHandler(final GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(queues = "${multiplication.queue}")
    void handleMultiplicationSolved(final MultiplicationSolvedEvent event){
        log.info("Multiplication Solved Event 수신 : {}" , event.getMultiplicationResultAttemptId());
        try {
            gameService.newAttemptForUser(event.getUserId(), event.getMultiplicationResultAttemptId(), event.isCorrect());
        }catch (final Exception e){
            log.error("MultiplicationSolvedEvent 처리 시 에러", e);
            //해당 이벤트가 다시 큐로 돌아가거나 두 번 처리되지 않도록 예외를 발생시킨다.
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
