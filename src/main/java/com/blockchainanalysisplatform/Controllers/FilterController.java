package com.blockchainanalysisplatform.Controllers;

import com.blockchainanalysisplatform.Data.*;
import com.blockchainanalysisplatform.Services.EventeumService;
import com.blockchainanalysisplatform.Services.UsersSubscriptionsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.reactive.function.client.WebClient;


@Controller
@RequestMapping("/filter")
@SessionAttributes("subscription")
@AllArgsConstructor
public class FilterController {


    private  UsersSubscriptionsService updateService;
    private EventeumService eventeumService;
    @GetMapping
    public String getController() {
        return "SubscriptionFilters";
    }

    @ModelAttribute
    public void addAttributes(Model model){
        model.addAttribute("filter",new Filter());
    }


    @PostMapping
    public String postController(Filter filter, @SessionAttribute (name="subscription")Subscription subscription, SessionStatus session) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            eventeumService.subscribe(subscription); //TODO:validation and error output
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/subscriptions";
        }

            subscription.setFilter(filter);
            session.setComplete();
            updateService.updatingUsersAndSubscriptions(subscription,user,filter); //filter ?


       // return "redirect:/checkSubscription";
        return "redirect:/subscriptions";
    }

}
