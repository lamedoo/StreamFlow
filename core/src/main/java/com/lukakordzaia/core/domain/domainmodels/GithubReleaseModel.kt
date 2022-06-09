package com.lukakordzaia.core.domain.domainmodels

data class GithubReleaseModel(
    val id: Long,
    val tag: String,
    val createdAt: String,
    val downloadUrl: String?
)
