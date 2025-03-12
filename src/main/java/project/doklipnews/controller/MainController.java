package project.doklipnews.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "redirect:/articles";
    }

    //Introduce
    @GetMapping("/team")
    public String team(){return "/introduce/team";}
    @GetMapping("/history")
    public String history(){return "/introduce/history";}
    @GetMapping("/mission")
    public String mission(){ return "/introduce/mission";}
    @GetMapping("/jobs")
    public String jobs(){ return "/introduce/jobs";}
    @GetMapping("/about")
    public String about() {return "/introduce/about";}

    //Customer Service
    @GetMapping("/contact")
    public String contact() {return "/customer/contact";}
    @GetMapping("/contact#donation")
    public String donation() {return "/customer/donation";}
    @GetMapping("/faq")
    public String faq() {return "/customer/faq";}
    @GetMapping("/privacy")
    public String privacy() {return "/customer/privacy";}
    @GetMapping("/terms")
    public String terms() {return "/customer/terms";}



} 