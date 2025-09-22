package daryna.lab01.controllers;


import daryna.lab01.services.CounterService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class CounterController {

    private final CounterService counterService;

    @PostMapping("/inc")
    public void inc() {
        counterService.increment();
    }

    @GetMapping("/count")
    public int getCounter(){
        return counterService.get();
    }

    @PostMapping("/reset")
    public void resetCounter() {
        counterService.reset();
    }

}
