package com.blockchainanalysisplatform.Controllers;


import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping("/registration")
@Controller
@AllArgsConstructor
public class RegistrationController {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    @GetMapping
    @ModelAttribute
    public String registration(@Param("error")String error,Model model){
        model.addAttribute("error",error);
        return "/registration";
    }

    @PostMapping
    public String processRegistration(User user) {


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //TODO: rewrite later
        try {
            userRepo.save(user);
        }catch(Exception e){

            return "redirect:/registration?error";
        }
        return "redirect:/login";
    }
}
