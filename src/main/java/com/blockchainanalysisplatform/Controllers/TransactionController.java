package com.blockchainanalysisplatform.Controllers;


import com.blockchainanalysisplatform.Data.ClickhouseTransaction;

import com.blockchainanalysisplatform.Data.OnlineFilter;
import com.blockchainanalysisplatform.Data.User;

import com.blockchainanalysisplatform.Repositories.JDBCClickhouseTransactionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;


@Controller
@AllArgsConstructor
@RequestMapping("/Transactions")
public class TransactionController {



    private final JDBCClickhouseTransactionRepository clr;




    @ModelAttribute("orderTypes")
    public ArrayList<String> addOrderTypes(){
       return ClickhouseTransaction.getField();
    }

    @ModelAttribute
    @GetMapping
    public String home(@RequestParam(name="id" )String id, Model model){

        model.addAttribute("subscriptionId",id);
        model.addAttribute("transactions", clr.findAllById(id));
        model.addAttribute("filter",new OnlineFilter());

        return "Transactions";
    }

    @PostMapping
    public ModelAndView updateTable(OnlineFilter filter,
                              @RequestParam(name="id")String id,
                                    ModelAndView newModelAndView){

        newModelAndView.setViewName("Transactions");
        newModelAndView.getModel().put("transactions", clr.findByIdWhereFilter(id,filter));
        newModelAndView.getModel().put("subscriptionId", id);
        newModelAndView.getModel().put("filter",filter);

        return newModelAndView;
    }


}
