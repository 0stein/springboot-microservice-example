package microservices.book.socialmultiplication.controller;

import microservices.book.socialmultiplication.service.MultiplicationService;
import microservices.book.socialmultiplication.domain.Multiplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/multiplications")
public class MultiplicationController {
    private final MultiplicationService multiplicationService;

    @Autowired
    MultiplicationController(MultiplicationService multiplicationService){
        this.multiplicationService = multiplicationService;
    }

    @GetMapping("/random")
    Multiplication getRandomMultiplication(){
        return multiplicationService.createRandMultiplication();
    }
}
