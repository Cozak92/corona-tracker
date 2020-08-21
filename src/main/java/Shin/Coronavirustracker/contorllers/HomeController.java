package Shin.Coronavirustracker.contorllers;


import Shin.Coronavirustracker.Services.CoronavirusDataService;
import Shin.Coronavirustracker.models.LocationStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//
@Controller
public class HomeController {


    @Autowired
    CoronavirusDataService coronavirusDataService;
    DecimalFormat formatter = new DecimalFormat("###,###");

    @GetMapping("/")
    public String home(Model model){

        model.addAttribute("locationStats",coronavirusDataService.getAllStats());
        model.addAttribute("totalReportedCases",formatter.format(coronavirusDataService.TotalCases()));
        model.addAttribute("newCases",formatter.format(coronavirusDataService.totalNewCases()));

        return "home";
    }


}
