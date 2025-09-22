package daryna.lab01.controllers;

import daryna.lab01.services.CounterDBService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/db")
public class CounterDBController {

    private final CounterDBService counterDBService;

    @PostMapping("/inc")
    public void inc() {
        counterDBService.increment();
    }

    @GetMapping("/count")
    public int getCounter() {
        return counterDBService.get();
    }

    @PostMapping("/reset")
    public void resetCounter() {
        counterDBService.reset();
    }
}
