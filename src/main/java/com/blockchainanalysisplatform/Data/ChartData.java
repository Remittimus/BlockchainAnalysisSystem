package com.blockchainanalysisplatform.Data;



import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class ChartData {

 private List<Double>  values = new ArrayList<>();
 private List<LocalDateTime>  dates = new ArrayList<>();
 private List<Double> gasPrices = new ArrayList<>();


}
