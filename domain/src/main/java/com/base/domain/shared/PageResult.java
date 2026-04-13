package com.base.domain.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PageResult<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public <U> PageResult<U> map(Function<? super T, ? extends U> converter) {
        List<U> mappedContent = content.stream().map(converter).collect(Collectors.toList());
        return new PageResult<>(mappedContent, pageNumber, pageSize, totalElements, totalPages);
    }
}
