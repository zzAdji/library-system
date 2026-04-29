package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class HoldRequest {
    private String id;
    private String userId;
    private String bookIsbn;
    private LocalDate requestDate;
    private HoldStatus status;

    public HoldRequest() {
    }

    public HoldRequest(String id, String userId, String bookIsbn, LocalDate requestDate, HoldStatus status) {
        this.id = id;
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.requestDate = requestDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public HoldStatus getStatus() { return status; }
    public void setStatus(HoldStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoldRequest holdRequest = (HoldRequest) o;
        return Objects.equals(id, holdRequest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
