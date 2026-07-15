package com.ltfullstack.userservice.dto.identity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credential {
    private String value;
    private boolean temporary;
    private String type;
}
