package com.barostartbe.domain.comment.entity

import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "sub_comments")
class SubComment(
    @ManyToOne
    @JoinColumn(name = "comment_id")
    val comment: Comment,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    var content: String? = null
): BaseEntity() {

    companion object{
        fun of(user: User, comment: Comment, content: String): SubComment{
            return SubComment(
                user = user,
                comment = comment,
                content = content
            )
        }
    }

}