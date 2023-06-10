package com.blockchainanalysisplatform.Data;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class OnlineFilter implements FilterInterface{


   private Double minValue, maxValue;
   private LocalDateTime startDate, endDate;
   private String orderBy;
   private String orderType;

   public OnlineFilter(){
      this.orderBy="timestamp";
      this.orderType="asc";
   }

   //This function don't check orderBy and orderType values
   @Override
   public boolean isNull(){

      return (this.minValue == null && this.maxValue == null && this.startDate == null && this.endDate == null );
   }
}
