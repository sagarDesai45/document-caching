package com.example.caching.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {

    private String id;
    @NotBlank(message = "title should not be blank")
    private String title;
    @NotBlank(message = "content should not be blank")
    private String content;
    @NotBlank(message = "tenantId  should not be blank")
    private String tenantId;
}
