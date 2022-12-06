package br.edu.utfpr.servicebook.sse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventSeeRepository extends JpaRepository<EventSse, Long> {

    @Query("SELECT e FROM EventSse e WHERE e.toUserEmail = :toUserEmail")
    List<EventSse> findByEmail(@Param("toUserEmail") String toUserEmail);

}
