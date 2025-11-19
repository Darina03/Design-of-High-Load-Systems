package daryna.lab06.hazelcast;

import com.hazelcast.map.MapStore;
import daryna.lab06.entity.Counter;
import daryna.lab06.repository.CounterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CounterMapStore implements MapStore<Long, Counter> {

    private final CounterRepository counterRepository;

    @Transactional
    @Override
    public void store(Long key, Counter value) {
        value.setId(key);
        counterRepository.save(value);
    }

    @Transactional
    @Override
    public void storeAll(Map<Long, Counter> map) {
        counterRepository.saveAll(map.values());
    }

    @Transactional
    @Override
    public void delete(Long key) {
        counterRepository.deleteById(key);
    }

    @Transactional
    @Override
    public void deleteAll(Collection<Long> keys) {
        counterRepository.deleteAllById(keys);
    }

    @Override
    public Counter load(Long key) {
        return counterRepository.findById(key).orElse(null);
    }

    @Override
    public Map<Long, Counter> loadAll(Collection<Long> keys) {
        return counterRepository.findAllById(keys).stream()
                .collect(Collectors.toMap(Counter::getId, c -> c));
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        return counterRepository.findAll().stream()
                .map(Counter::getId)
                .collect(Collectors.toList());
    }
}