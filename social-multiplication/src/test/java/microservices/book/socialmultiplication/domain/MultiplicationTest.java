package microservices.book.socialmultiplication.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiplicationTest {
    @Test
    public void domainclassTest(){
        Multiplication multiplication = new Multiplication(3, 5);
    }
}