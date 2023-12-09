package com.blockchainanalysisplatform.Controllers;



import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;




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
    public String processRegistration(@Valid @ModelAttribute User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || userRepo.findByEmailOrUsername(user.getEmail(), user.getUsername()).isPresent()) {
            return "redirect:/registration?error";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/login";
    }
}
