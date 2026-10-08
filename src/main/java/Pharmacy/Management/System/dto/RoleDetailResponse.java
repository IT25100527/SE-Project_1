package Pharmacy.Management.System.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDetailResponse {

    private Long id;
    private String name;
    private String description;
    private List<String> permissions;
}