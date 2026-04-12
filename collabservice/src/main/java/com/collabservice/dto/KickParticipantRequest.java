package com.collabservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KickParticipantRequest {

    @NotNull
    private Integer userId;
}