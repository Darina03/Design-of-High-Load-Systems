package daryna.lab01.repository;

import daryna.lab01.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {

    @Modifying
    @Query("update Counter c set c.incrementingValue = c.incrementingValue + 1 where c.id = :id")
    void increment(@Param("id") Long id);

    @Modifying
    @Query("update Counter c set c.incrementingValue = 0 where c.id = :id")
    void reset(@Param("id") Long id);
}
