package com.blockchainanalysisplatform.Controllers;

import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.SubscriptionStatuses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
@AllArgsConstructor
@RequestMapping("/subscribe")
@SessionAttributes("subscription")
public class RegisterSubscriptionController {






    @ModelAttribute
    public void addTransactionsToModel(Model model){

        model.addAttribute("choices", new ArrayList<>(Arrays.asList("FROM_ADDRESS","TO_ADDRESS"))); //hash deleted
        model.addAttribute("subscription",new Subscription());
        model.addAttribute("statuses", SubscriptionStatuses.values());

    }


    @GetMapping
    public String createSubscription(@Param("error")String error,Model model){
        model.addAttribute("error",error);
        return "createSubscription";
    }

    @PostMapping
    public String postController(@Valid @ModelAttribute Subscription subscription, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            // Обработка ошибок валидации
            return "redirect:/subscribe?error=Invalid_data";
        }

        return "redirect:/filter";

    }


}
