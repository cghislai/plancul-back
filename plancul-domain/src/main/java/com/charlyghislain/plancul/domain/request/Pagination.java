package com.charlyghislain.plancul.domain.request;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class Pagination {

    private int offset = 0;
    private int size = 0;

    public Pagination() {
    }

    public Pagination(int size) {
        this.size = size;
    }

    public Pagination(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

}
