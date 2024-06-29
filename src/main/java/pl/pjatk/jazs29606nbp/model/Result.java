package pl.pjatk.jazs29606nbp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Result {
    @Id
    @GeneratedValue
    private Long id;
    private String currency;
    private String currencyCode;
    private double avgRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime requestDate;

    public Result(String currency, String currencyCode, double avgRate, LocalDateTime requestDate) {
        this.currency = currency;
        this.currencyCode = currencyCode;
        this.avgRate = avgRate;
        this.requestDate = requestDate;
    }

    public Result() {

    }

    public Result(String currency, String currencyCode, double avgRate, LocalDate startDate, LocalDate endDate, LocalDateTime requestDate) {
        this.currency = currency;
        this.currencyCode = currencyCode;
        this.avgRate = avgRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestDate = requestDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(double avgRate) {
        this.avgRate = avgRate;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate from) {
        this.startDate = from;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate to) {
        this.endDate = to;
    }
}
