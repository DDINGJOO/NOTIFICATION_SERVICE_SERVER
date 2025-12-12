package com.teambind.springproject.adapter.in.rest.consent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채널별 동의 변경 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelConsentRequest {

    @NotNull(message = "consented is required")
    private Boolean consented;
}
