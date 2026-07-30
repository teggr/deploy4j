package dev.deploy4j.jdemo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final Applications applications;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("applications", applications.listApplications());
        model.addAttribute("applicationForm", new ApplicationForm());
        return "home"; // just a list of things you can see demoed
    }

    @PostMapping("/add")
    public String addApplication(@ModelAttribute ApplicationForm form) {
        applications.addApplication(form.getName());
        return "redirect:/";
    }

    @PostMapping("/update")
    public String updateApplication(@ModelAttribute ApplicationForm form) {
        applications.updateApplication(form.getId(), form.getName());
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteApplication(@RequestParam Long id) {
        applications.deleteApplication(id);
        return "redirect:/";
    }

    @Data
    public static class ApplicationForm {
        private Long id;
        private String name;
        // add more fields as needed
    }

}
