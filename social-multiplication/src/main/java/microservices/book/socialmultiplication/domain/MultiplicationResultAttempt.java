package microservices.book.socialmultiplication.domain;

import lombok.*;

import javax.persistence.*;

/**
 * {@Link User} 가 {@Link Multiplication} 을 계산한 답안을 적는 클래스
 */

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Entity
public final class MultiplicationResultAttempt {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID")
    private final User user;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "MULTIPLICATION_ID")
    private final Multiplication multiplication;
    private final int resultAttempt;
    private final boolean correct;

    public MultiplicationResultAttempt(){
        user = null;
        multiplication = null;
        resultAttempt = -1;
        correct = false;
    }
}
