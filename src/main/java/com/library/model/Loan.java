package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private String id;
    private String userId;
    private String bookIsbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private int renewalsCount;

    public Loan() {
    }

    public Loan(String id, String userId, String bookIsbn, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, LoanStatus status, int renewalsCount) {
        this.id = id;
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.renewalsCount = renewalsCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public int getRenewalsCount() { return renewalsCount; }
    public void setRenewalsCount(int renewalsCount) { this.renewalsCount = renewalsCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
