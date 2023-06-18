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


//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;


    private   Double minValue, maxValue;
    private  LocalDateTime startDate, endDate;
    private  String fromAddress, toAddress;

//    @ToString.Exclude
//    @OneToOne()
//    private Subscription subscription;
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
