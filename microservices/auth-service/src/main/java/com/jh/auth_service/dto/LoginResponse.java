package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
		@Schema(example = "token: sdkjfalkdfnaljdnfa")
		String token
		) {

}
