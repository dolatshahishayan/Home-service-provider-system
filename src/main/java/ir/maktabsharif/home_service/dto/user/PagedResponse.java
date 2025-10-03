package ir.maktabsharif.home_service.dto.user;

import java.util.List;

public record PagedResponse<T>(List<T> content, int page, int size, long totalElements) {}
