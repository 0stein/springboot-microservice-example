package microservices.book.gamification.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import microservices.book.gamification.client.MultiplicationResultAttemptDeserializer;


/**
 * 기존의 multiplicationResultAttempt 에서 식별자(id)를 없애고 필드를 축소시켰다.
 * @Jsondeserialize 에너테이션은 RestTemplate 의 메시지 컨버터가 json 데이터를
 * 읽어서 역직렬화할 떄 특정 클래스를 이용하게 한다.
 * 받을 json 구조가 이 클래스와 매치되지 않기 때문에 필요한 작업.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@JsonDeserialize(using = MultiplicationResultAttemptDeserializer.class)
public class MultiplicationResultAttempt {
    private final String userAlias;
    private final int multiplicationFactorA;
    private final int multiplicationFactorB;
    private final int resultAttempt;
    private final boolean correct;

    MultiplicationResultAttempt(){
        userAlias = null;
        multiplicationFactorA = -1;
        multiplicationFactorB = -1;
        resultAttempt = -1;
        correct = false;
    }
}
