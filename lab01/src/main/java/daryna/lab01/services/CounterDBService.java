package daryna.lab01.services;

import daryna.lab01.repository.CounterRepository;
import daryna.lab01.entity.Counter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CounterDBService {
    private static final Long COUNTER_ID =1L ;
    private final CounterRepository counterRepository;

    public int get() {
        return counterRepository.findById(COUNTER_ID)
                .orElseThrow(() -> new IllegalStateException("Counter not found"))
                .getIncrementingValue();    }

    @Transactional
    public void increment() {
        counterRepository.increment(COUNTER_ID);
    }

    @Transactional
    public void reset() {
        counterRepository.reset(COUNTER_ID);
    }
}
