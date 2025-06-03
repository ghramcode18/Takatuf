package geekcode.takatuf.dto;

import lombok.Data;
import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private long total;
    private int page;
    private int perPage;

    public PaginatedResponse() {
    }

    public PaginatedResponse(List<T> data, long total, int page, int perPage) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.perPage = perPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
