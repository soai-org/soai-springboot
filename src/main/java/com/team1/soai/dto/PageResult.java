package com.team1.soai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalCount;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    
    public PageResult(List<T> content, int page, int size, int totalCount) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / size);
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }
}
