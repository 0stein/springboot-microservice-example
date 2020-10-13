package microservices.book.socialmultiplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class RandomGeneratorServiceImplTest {
    private RandomGeneratorServiceImpl randomGeneratorServiceImpl;

    @BeforeEach
    public void setUp(){
        randomGeneratorServiceImpl = new RandomGeneratorServiceImpl();
    }

    @Test
    public void generateRandomFactorsBetweenExpectedLimits(){
        List<Integer> randomFactors = IntStream.range(0, 10)
                .map(i -> randomGeneratorServiceImpl.generateRandomFactor())
                .boxed()
                .collect(Collectors.toList());
        randomFactors.forEach(x -> assertThat(x).isGreaterThan(11).isLessThan(99));
    }
}