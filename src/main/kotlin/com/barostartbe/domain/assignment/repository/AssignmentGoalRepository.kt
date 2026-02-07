package com.barostartbe.domain.assignment.repository

import com.barostartbe.domain.assignment.entity.AssignmentGoal
import org.springframework.data.jpa.repository.JpaRepository

interface AssignmentGoalRepository : JpaRepository<AssignmentGoal, Long>
