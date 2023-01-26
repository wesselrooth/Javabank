package com.example.registration_demo.controller;

import antlr.StringUtils;
import com.example.registration_demo.Utils.FileUploadUtil;
import com.example.registration_demo.dto.UserDto;
//import com.example.registration_demo.entity.BankRekening;
import com.example.registration_demo.entity.Bankrekening;
import com.example.registration_demo.entity.Bedrag;
import com.example.registration_demo.entity.Transactie;
import com.example.registration_demo.entity.User;
import com.example.registration_demo.entity.Role;
import com.example.registration_demo.repository.BankRekeningRepository;
import com.example.registration_demo.repository.RoleRepository;
import com.example.registration_demo.repository.TransactieRepository;
import com.example.registration_demo.repository.UserRepository;
import com.example.registration_demo.service.UserService;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    @Autowired
    private BankRekeningRepository rekeningRepository;
    @Autowired
    private TransactieRepository transactieRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
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

    @GetMapping("/deposite")
    public String get_bedrag(Model model, Principal principal){
        System.out.println("--> GET bedrag");

        model.addAttribute("bedrag", new Bedrag());
        User current_user = userService.findUserByEmail(principal.getName());
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

        model.addAttribute("saldo", user_rekening.getSaldo());
        return "deposite";
    }

    @PostMapping("/deposite")
    public String post_bedrag(@Valid Bedrag bedrag, BindingResult result, Model model, Principal principal){

        if(result.hasErrors()){
            model.addAttribute("bedrag", bedrag);
            return "/deposite";
        }
        User current_user = userService.findUserByEmail(principal.getName());
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(current_user);

        double saldo  = user_rekening.getSaldo();
        double pin_bedragg = bedrag.getPinbedrag().doubleValue();
        user_rekening.addSaldo(pin_bedragg);

        model.addAttribute("saldo", user_rekening.getSaldo());

        rekeningRepository.save(user_rekening);

        return "deposite";
    }

    @GetMapping("/bankaccount")
    public String get_rekening(Model model){
        System.out.println("--> GET rekening");
        model.addAttribute("rekening", new Bankrekening());
        return "bankaccount";
    }

    @PostMapping("/bankaccount")
    public String post_rekening(@Valid Bankrekening rekening, BindingResult result, Model model, Principal principal){
        System.out.println("--> POST bankaccount");

        if (result.hasErrors()){
            System.out.println("ERRORS");
        }
        rekening.setUser(userService.findUserByEmail(principal.getName()));
        rekeningRepository.save(rekening);

        Role transfer_role = roleRepository.findByname("TRANSFER_USER");

        User user = userService.findUserByEmail(principal.getName());

        List<Role> list = new LinkedList<>();
        list.add(transfer_role);
        user.setRoles(list);

        Role to_delete_role = roleRepository.findByname("USER");
        user.removeRoles(to_delete_role);

        userRepository.save(user);

        return "bankaccount";
    }
/* Withdraw*/
    @GetMapping("/withdraw")
    public String get_pinnen(Model model, Principal principal){
        System.out.println("--> GET pinnen");
        model.addAttribute("bedrag", new Bedrag());
        User user = userService.findUserByEmail(principal.getName());
        Bankrekening userekening =   rekeningRepository.findBankRekeningByUser(user);
        Double saldo = userekening.getSaldo();
        model.addAttribute("saldo", saldo);
        return "withdraw";
    }

    @PostMapping("/withdraw")
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
        return "withdraw";

    }
    @GetMapping("/transfer")
    public String get_overmaken(Model model,Principal principal){
        System.out.println("--> GET overmaken");
        model.addAttribute("bedrag", new Bedrag());

        return "transfer";
    }
    @PostMapping("/transfer_check")
    public String post_overmaken(@Valid Bedrag bedrag, BindingResult result, Model model, Principal principal, @RequestParam int overmaak_rekening){
        System.out.println("--> POST transfer");

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
            model.addAttribute("rekeningnaam", transfer_rekening.getUser().getEmail());
            model.addAttribute("rekeningnummer", transfer_rekening.getId());
            model.addAttribute("bedrag", bedrag.getPinbedrag());
            return "transfer_check";
        }
        else{
            model.addAttribute("error", "Not enough balance");
        }
        model.addAttribute("saldo", rekening.getSaldo());
        return "transfer";
    }
    @PostMapping("/transfer")
    public String overmaakCheck(@RequestParam String rekeningnaam,@RequestParam int rekeningnummer ,@RequestParam int bedrag, Principal principal ){
        System.out.println("--> POST transfer");

        User user = userService.findUserByEmail(principal.getName());
        Bankrekening user_rekening = rekeningRepository.findBankRekeningByUser(user);

//      subtract the money of the account
        double overboek_bedrag = Double.valueOf(bedrag);
        user_rekening.subtractSaldo(overboek_bedrag);

//      Find account of receiver
        User user_reciver = userService.findUserByEmail(rekeningnaam);
        Bankrekening transfer_rekening = rekeningRepository.findBankRekeningByUser(user_reciver);

//        Add money to receiver account
        double transfer_saldo = transfer_rekening.getSaldo();
        transfer_rekening.addSaldo(transfer_saldo);

        Transactie transactie = new Transactie();
        transactie.setBedrag(overboek_bedrag);
        transactie.setRekening(user_rekening);
        transactie.setReceiver_rekening(transfer_rekening);

        transactieRepository.save(transactie);
        rekeningRepository.save(user_rekening);
        rekeningRepository.save(transfer_rekening);

        System.out.println("--> Oke het geld is overgemaakt");
        return "sucess";
    }

    @GetMapping("/role")
    public String get_role(){
        System.out.println("--> get role");
        return "role";
    }
    @PostMapping("/role")
    public String post_role(@RequestParam String role, Principal principal){
        System.out.println("--> POST Removce ROLE");

        Role new_role = new Role();
        new_role.setName(role);
        roleRepository.save(new_role);

        return "role";
    }

    @GetMapping("/profile")
    public String get_profile(Principal principal){
        System.out.println("--> GET profile");

        User user = userRepository.findByEmail(principal.getName());

        return "profile";
    }
    @PostMapping("/profile")
    public String post_profile(Principal principal, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        System.out.println("--> POST profile|");
        System.out.println(multipartFile.getOriginalFilename());

        User user = userRepository.findByEmail(principal.getName());
        user.setProfileImage(multipartFile.getOriginalFilename());

        userRepository.save(user);

        String uploadDir = "user-photos/" + user.getId();

        FileUploadUtil.saveFile(uploadDir, multipartFile.getOriginalFilename(), multipartFile);

        return "profile";

    }

}
