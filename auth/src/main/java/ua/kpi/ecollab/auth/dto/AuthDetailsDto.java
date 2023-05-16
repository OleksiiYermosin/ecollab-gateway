package ua.kpi.ecollab.auth.dto;

import lombok.Builder;

@Builder
public class AuthDetailsDto {

    private String token;

    private String username;

    private long id;

}
