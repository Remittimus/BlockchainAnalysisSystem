package com.blockchainanalysisplatform.Data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
//@Entity
@AllArgsConstructor
@Embeddable
//@Table(name="filters")
public class Filter implements FilterInterface {


    private Double minValue;
    private Double maxValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String fromAddress;
    private String toAddress;


    public Filter() {
        this.minValue = null;
        this.maxValue = null;
        this.startDate = null;
        this.endDate = null;
        this.fromAddress = "";
        this.toAddress = "";
    }

    @Override
    public boolean isEmpty() {

        return (this.minValue == null && this.maxValue == null && this.startDate == null && this.endDate == null && this.fromAddress.isEmpty() && this.toAddress.isEmpty());
    }


}
