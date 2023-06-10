package com.blockchainanalysisplatform.Controllers;


import com.blockchainanalysisplatform.Repositories.JDBCClickhouseTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/Charts")
@AllArgsConstructor
public class ChartsController {

    private final JDBCClickhouseTransactionRepository clickRepo;
    @GetMapping
    public String getController(@RequestParam(name="id" )String id, Model model){


        //List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        model.addAttribute("dataForCharts", clickRepo.findDataForChartsById(id));

        return "Charts";
    }
}
