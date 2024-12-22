package com.nomad.gathr.util

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class S3Utils(
    private val amazonS3: AmazonS3,

    @Value("\${cloud.aws.bucket}")
    private val bucketName: String
) {
    private val allowedExtensions = listOf("jpg", "jpeg", "png")

    fun generatePreSignedUrl(fileName: String, fileType: String?): Pair<String, String> {
        // 파일 크기 제한은 S3 정책으로 처리

        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (extension !in allowedExtensions) {
            throw IllegalArgumentException("허용되지 않은 파일 형식입니다. 허용된 확장자는 ${allowedExtensions.joinToString(", ")} 입니다.")
        }

        val expiration = Date(System.currentTimeMillis() + 3 * 60 * 1000) // 만료 시간 (3분)
        val newFileName = UUID.randomUUID().toString() + "." + extension
        val newFilePath = "profile/$newFileName"

        val generatePreSignedUrlRequest = GeneratePresignedUrlRequest(bucketName, newFilePath)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)
        generatePreSignedUrlRequest.contentType = fileType

        val url = amazonS3.generatePresignedUrl(generatePreSignedUrlRequest).toString()
        return Pair(url, newFilePath)
    }
}