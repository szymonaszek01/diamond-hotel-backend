package com.app.diamondhotelbackend.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponseDto {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("encoded_file")
    private String encodedFile;

    private byte[] file;
}
