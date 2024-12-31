package com.example.cryptotrading.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointProperties {
    @NotBlank
    private String scheme;

    @NotBlank
    private String host;

    private Integer port;

    @NotBlank
    private String resourcePath;

    public String toUrl(Object... pathParams) {
        String path = resourcePath;
        if (pathParams != null && pathParams.length > 0) {
            path = String.format(resourcePath, pathParams);
        }
        StringBuilder url = new StringBuilder(scheme)
                .append("://")
                .append(host);

        if (port != null && port > 0) {
            url.append(':').append(port);
        }
        return url.append(path).toString();
    }
}