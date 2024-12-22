package com.nomad.gathr.execption.handler

import com.nomad.gathr.execption.constant.ErrorCode
import java.lang.RuntimeException

open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)