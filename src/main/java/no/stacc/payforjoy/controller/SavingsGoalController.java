package no.stacc.payforjoy.controller;

import no.stacc.payforjoy.constants.ApiEndPoints;
import no.stacc.payforjoy.enums.GoalStatus;
import no.stacc.payforjoy.interfaces.service.SavingsGoalService;
import no.stacc.payforjoy.model.dto.SavingsGoalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping( "/api/v1/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsGoalDto>> getUserGoals(@PathVariable Long userId) {
        List<SavingsGoalDto> goals = savingsGoalService.findAllByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<SavingsGoalDto>> getActiveGoals(@PathVariable Long userId) {
        List<SavingsGoalDto> goals = savingsGoalService.findActiveGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalDto> getGoal(@PathVariable Long id) {
        SavingsGoalDto goal = savingsGoalService.findById(id);
        return ResponseEntity.ok(goal);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<SavingsGoalDto> createGoal(
            @PathVariable Long userId,
            @Valid @RequestBody SavingsGoalDto goalDto) {
        SavingsGoalDto createdGoal = savingsGoalService.createGoal(goalDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalDto> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalDto goalDto) {
        SavingsGoalDto updatedGoal = savingsGoalService.updateGoal(id, goalDto);
        return ResponseEntity.ok(updatedGoal);
    }

    @PostMapping("/{id}/add-savings")
    public ResponseEntity<SavingsGoalDto> addSavings(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        SavingsGoalDto updatedGoal = savingsGoalService.addSavings(id, amount);
        return ResponseEntity.ok(updatedGoal);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SavingsGoalDto> updateGoalStatus(
            @PathVariable Long id,
            @RequestParam GoalStatus status) {
        SavingsGoalDto updatedGoal = savingsGoalService.updateGoalStatus(id, status);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        savingsGoalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
