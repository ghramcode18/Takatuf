package geekcode.takatuf.dto.category;

import lombok.*;

public class CategoryDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {
        private String name;
        private String description;
        private String image;
        private Boolean active;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private String image;
        private Boolean active;
    }
}
