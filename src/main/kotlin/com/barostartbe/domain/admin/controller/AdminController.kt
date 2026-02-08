package com.barostartbe.domain.admin.controller

import com.barostartbe.domain.admin.entity.MentorMenteeMapping
import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.response.type.SuccessCode
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(
    private val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository
) {

    @PostMapping("/mapping")
    @Operation(summary = "테스트용 멘토, 멘티 매핑", description = "테스트를 위해 회원가입한 멘토, 멘티를 매핑합니다.")
    fun mappingMentorMentee(
        @RequestParam mentorId: Long,
        @RequestParam menteeId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        val mentor = mentorRepository.findByIdOrNull(mentorId) ?: throw ServiceException(ErrorCode.MENTOR_NOT_FOUND)
        val mentee = menteeRepository.findByIdOrNull(menteeId) ?: throw ServiceException(ErrorCode.MENTEE_NOT_FOUND)

        val mapping = MentorMenteeMapping(mentor, mentee)

        mentorMenteeMappingRepository.save(mapping)

        return ApiResponse.success(SuccessCode.CREATE_OK)
    }
}
