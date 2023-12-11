package com.blockchainanalysisplatform.Controllers;


import com.blockchainanalysisplatform.Services.ClickhouseService;
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

    private final ClickhouseService clickService;
    @GetMapping
    public String getController(@RequestParam(name="id" )String id, Model model){

        model.addAttribute("dataForCharts", clickService.findDataForChartsById(id));

        return "Charts";
    }
}
