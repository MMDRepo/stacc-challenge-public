package no.stacc.payforjoy.interfaces.repository;

import no.stacc.payforjoy.model.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserIdOrderByEarnedAtDesc(Long userId);
    List<Reward> findByUserIdAndIsClaimed(Long userId, Boolean isClaimed);

    @Query("SELECT SUM(r.points) FROM Reward r WHERE r.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);
}