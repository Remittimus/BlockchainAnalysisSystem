package com.blockchainanalysisplatform.Data;

import java.time.LocalDateTime;

public interface FilterInterface {

     boolean isEmpty();

     Double getMaxValue();
     Double getMinValue();

     LocalDateTime getStartDate();
     LocalDateTime getEndDate();
}
