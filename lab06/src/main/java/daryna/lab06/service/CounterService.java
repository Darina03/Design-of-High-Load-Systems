package daryna.lab06.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import daryna.lab06.entity.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final HazelcastInstance hazelcastInstance;
    private static final String MAP_NAME = "counters";


    private IMap<Long, Counter> getCounterMap() {
        return hazelcastInstance.getMap(MAP_NAME);
    }

    public int get(Long id) {
        Counter counter = getCounterMap().get(id);
        if (counter == null) {

            throw new IllegalStateException("Counter " + id + " not found");
        }
        return counter.getIncrementingValue();
    }

    public void increment(Long id) {
        IMap<Long, Counter> map = getCounterMap();

        map.lock(id);
        try {
            Counter counter = map.get(id);
            if (counter != null) {
                counter.setIncrementingValue(counter.getIncrementingValue() + 1);

                map.put(id, counter);
            }
        } finally {
            map.unlock(id);
        }
    }

    public void reset(Long id) {
        IMap<Long, Counter> map = getCounterMap();
        map.lock(id);
        try {
            Counter counter = map.get(id);
            if (counter != null) {
                counter.setIncrementingValue(0);
                map.put(id, counter);
            }
        } finally {
            map.unlock(id);
        }
    }

    public void initCounterIfNotExists(Long id) {
        IMap<Long, Counter> map = getCounterMap();
        map.putIfAbsent(id, new Counter(id, 0));
    }
}
