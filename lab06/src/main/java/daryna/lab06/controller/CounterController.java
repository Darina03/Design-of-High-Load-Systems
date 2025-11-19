package daryna.lab06.controller;

import daryna.lab06.service.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hazelcast")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PostMapping("/inc/{id}")
    public void increment(@PathVariable Long id) {
        counterService.increment(id);
    }

    @GetMapping("/count/{id}")
    public int getCounter(@PathVariable Long id) {
        return counterService.get(id);
    }

    @PostMapping("/reset/{id}")
    public void resetCounter(@PathVariable Long id) {
        counterService.reset(id);
    }
}