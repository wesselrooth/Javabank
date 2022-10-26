package com.example.registration_demo.controller;

import com.example.registration_demo.dto.UserDto;
//import com.example.registration_demo.entity.BankRekening;
import com.example.registration_demo.entity.Bankrekening;
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
import org.springframework.web.bind.annotation.RequestParam;

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
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

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
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

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
        model.addAttribute("rekening", new Bankrekening());
        return "rekening";
    }

    @PostMapping("/rekening")
    public String post_rekening(@Valid Bankrekening rekening, BindingResult result, Model model, Principal principal){

        if (result.hasErrors()){
            System.out.println("ERRORS");
        }
        rekening.setUser(userService.findUserByEmail(principal.getName()));
        rekeningRepository.save(rekening);
        System.out.println("REKENING opgeslagen");
        return "rekening";
    }

    @GetMapping("/pinnen")
    public String get_pinnen(Model model, Principal principal){
        System.out.println("--> GET pinnen");
        model.addAttribute("bedrag", new Bedrag());
        User user = userService.findUserByEmail(principal.getName());
        Bankrekening userekening =   rekeningRepository.findBankRekeningByUser(user);
        Double saldo = userekening.getSaldo();
        model.addAttribute("saldo", saldo);
        return "pinnen";
    }

    @PostMapping("/pinnen")
    public String post_pinnen(@Valid Bedrag bedrag, Model model, BindingResult result, Principal principal){
        System.out.println("--> POST pinnen");

        User user = userService.findUserByEmail(principal.getName());
        Bankrekening rekening = rekeningRepository.findBankRekeningByUser(user);

        Double saldo = rekening.getSaldo();
        Double double_bedrag = bedrag.getPinbedrag().doubleValue();

        if (double_bedrag < saldo){
                saldo = saldo - double_bedrag;
                rekening.setSaldo(saldo);
                rekeningRepository.save(rekening);
        }
        else{
            model.addAttribute("error", "Niet genoeg saldo");
        }
        model.addAttribute("saldo", rekening.getSaldo());
        return "pinnen";

    }
    /*
    * Wat moet er gebeuren als je geld wilt overmaken
    * Bedrag invullen
    *   Bedrag check, laat error zien
    *
    * Check of de gebruiker wel het bedrag op zijn rekening heeft staan
    *
    * Pak de rekening nummer waar het naartoe moet,
    *   Laat zien op wie zijn naam deze staat
    * Is dit goed, maak dan het geld over
    *
    * */
    @GetMapping("/overmaken")
    public String get_overmaken(Model model,Principal principal){
        System.out.println("--> GET overmaken");
        model.addAttribute("bedrag", new Bedrag());

        return "overmaken";
    }
    @PostMapping("/overmaken")
    public String post_overmaken(@Valid Bedrag bedrag, BindingResult result, Model model, Principal principal, @RequestParam int overmaak_rekening){
        System.out.println("--> POST overmaken");
        System.out.println("Overmaakrekening: " + overmaak_rekening);
        User user = userService.findUserByEmail(principal.getName());
        Bankrekening rekening = rekeningRepository.findBankRekeningByUser(user);

        Double saldo = rekening.getSaldo();
        Double double_bedrag = bedrag.getPinbedrag().doubleValue();

        if (double_bedrag < saldo){
            saldo = saldo - double_bedrag;
            /*
            * Logic om geld op andere rekeneing te krijgen
            * */
            Long rekeningId = Long.valueOf(overmaak_rekening);

            Bankrekening transfer_rekening =  rekeningRepository.findBankRekeningById(rekeningId);
            System.out.println("En damens en heren de nieuwe rekening is **tromgeroffel ---> " + transfer_rekening);
            model.addAttribute("rekeningnaam", transfer_rekening.getUser().getEmail());
            model.addAttribute("rekeningnummer", transfer_rekening.getId());
            model.addAttribute("bedrag", bedrag.getPinbedrag());
            return "overmaak_check";
        }
        else{
            model.addAttribute("error", "Niet genoeg saldo");
        }
        model.addAttribute("saldo", rekening.getSaldo());
        return "overmaken";
    }
    @PostMapping("/overboeken")
    public String overmaakCheck(@RequestParam String rekeningnaam,@RequestParam int rekeningnummer ,@RequestParam int bedrag, Principal principal ){
        System.out.println("--> GET Overboeken");

        User user = userService.findUserByEmail(principal.getName());
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(user);

        double saldo = user_rekening.getSaldo();

        double overboek_bedrag = Double.valueOf(bedrag);

        saldo = saldo - overboek_bedrag;

        user_rekening.setSaldo(saldo);


        User tranfer_user = userService.findUserByEmail(rekeningnaam);
        Bankrekening transfer_rekening = rekeningRepository.findBankRekeningByUser(tranfer_user);

        double transfer_saldo = transfer_rekening.getSaldo();

        transfer_saldo =  transfer_saldo + overboek_bedrag;
        transfer_rekening.setSaldo(transfer_saldo);

        rekeningRepository.save(user_rekening);
        rekeningRepository.save(transfer_rekening);

        System.out.println("--> Oke het geld is overgemaakt");
        return "sucess";
    }
}
