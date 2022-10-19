package com.example.registration_demo.controller;

import com.example.registration_demo.dto.UserDto;
import com.example.registration_demo.entity.BankRekening;
import com.example.registration_demo.entity.Bedrag;
import com.example.registration_demo.entity.User;
import com.example.registration_demo.repository.BankRekeningRepository;
import com.example.registration_demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    @Autowired
    private BankRekeningRepository rekeningRepository;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/index")
    public String home(){
        return "index";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result,  Model model){
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }
        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        return "redirect:/register?success";
    }
    // handler method to handle list of users
    @GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    // handler method to handle login request
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/bedrag")
    public String get_bedrag(Model model, Principal principal){
        System.out.println("--> GET bedrag");
        model.addAttribute("bedrag", new Bedrag());
        User current_user = userService.findUserByEmail(principal.getName());
        BankRekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

        model.addAttribute("saldo", user_rekening.getSaldo());
        return "bedrag";
    }

    @PostMapping("/bedrag")
    public String post_bedrag(@Valid Bedrag bedrag, BindingResult result, Model model, Principal principal){

        if(result.hasErrors()){
            model.addAttribute("bedrag", bedrag);
            return "/bedrag";
        }
        User current_user = userService.findUserByEmail(principal.getName());
        BankRekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

        double saldo  = user_rekening.getSaldo();

        double pin_bedragg = bedrag.getPinbedrag().doubleValue();

        saldo = saldo + pin_bedragg;

        user_rekening.setSaldo(saldo);

        model.addAttribute("saldo", user_rekening.getSaldo());

        rekeningRepository.save(user_rekening);
        return "bedrag";
    }

    @GetMapping("/rekening")
    public String get_rekening(Model model){
        System.out.println("--> GET rekening");
        model.addAttribute("rekening", new BankRekening());
        return "rekening";
    }

    @PostMapping("/rekening")
    public String post_rekening(@Valid BankRekening rekening, BindingResult result, Model model, Principal principal){

        if (result.hasErrors()){
            System.out.println("ERRORS");
        }
        rekening.setUser(userService.findUserByEmail(principal.getName()));
        rekeningRepository.save(rekening);

        return "rekening";
    }

}
