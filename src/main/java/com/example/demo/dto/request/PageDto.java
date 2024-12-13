package com.example.demo.dto.request;

public class PageDto {
    private Integer page;
    private Integer size;

    public PageDto() {
        this.page = 0;
        this.size = 10;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }



    public PageDto(Integer page, Integer size) {
        setPage(page);
        setSize(size);
    }
}