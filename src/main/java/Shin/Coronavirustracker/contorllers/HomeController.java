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


@Controller
public class HomeController {


    @Autowired
    CoronavirusDataService coronavirusDataService;
    List<LocationStats> foundStats = new ArrayList<>();
    DecimalFormat formatter = new DecimalFormat("###,###");

    @GetMapping("/")
    public String home(Model model){

        model.addAttribute("locationStats",coronavirusDataService.getAllStats());
        model.addAttribute("totalReportedCases",formatter.format(coronavirusDataService.TotalCases()));
        model.addAttribute("newCases",formatter.format(coronavirusDataService.totalNewCases()));


        return "home";
    }

   @PostMapping("/")
    public String create(searchForm form, Model model){
        List<LocationStats> allStats = coronavirusDataService.getAllStats();
       foundStats.clear();

        for (var i=0; i < allStats.size(); i++) {
            String s = allStats.get(i).getCountry();
            if(StringUtils.containsIgnoreCase(s,form.getSearch(), Locale.ENGLISH)) {
                foundStats.add(allStats.get(i));
            }
        }

        return "redirect:/search";
    }


    //무식하지만 방법을 모르겠다 ㅜㅜ
    @PostMapping("/search")
    public String create2(searchForm form, Model model){
        List<LocationStats> allStats = coronavirusDataService.getAllStats();
        foundStats.clear();

        for (var i=0; i < allStats.size(); i++) {
            String s = allStats.get(i).getCountry();
            if(StringUtils.containsIgnoreCase(s,form.getSearch(), Locale.ENGLISH)) {
                foundStats.add(allStats.get(i));
            }
        }

        return "redirect:/search";
    }


    @GetMapping("/search")
    public String search(Model model){
        if(foundStats.isEmpty()){
            model.addAttribute("locationStats.country","Not Found");
        }
        else{
            model.addAttribute("locationStats",foundStats);

        }

        model.addAttribute("totalReportedCases",formatter.format(coronavirusDataService.TotalCases()));
        model.addAttribute("newCases",formatter.format(coronavirusDataService.totalNewCases()));

        return "home";
    }


}
