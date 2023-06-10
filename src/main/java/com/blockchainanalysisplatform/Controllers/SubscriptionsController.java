package com.blockchainanalysisplatform.Controllers;



import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import com.blockchainanalysisplatform.Services.UsersSubscriptionsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




@Controller
@CrossOrigin(origins="http://localhost:8080")
@AllArgsConstructor
public class SubscriptionsController {

    UserRepository uRepo;

    private UsersSubscriptionsService deleteService;

    @ModelAttribute
    public void addSubscriptionsList(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("subscriptions",uRepo.findById(user.getId()).get().getSubscriptions());
    }

    @GetMapping("/checkSubscription")
    public String check(){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getSubscriptions().isEmpty()){
            return ("redirect:/subscribe");
        }
        return ("redirect:/subscriptions");

    }


    @GetMapping("/subscriptions")
    public String subscription(){

        return "Subscriptions";
    }

    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable("id") String subscriptionId) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //TODO: mb i should use userId instead user class entity?
        deleteService.deleteSubscription(subscriptionId,user.getId());
        return ResponseEntity.ok().build();
    }


}
