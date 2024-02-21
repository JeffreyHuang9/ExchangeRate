package com.example.controller;

import com.example.model.ExchangeRate;
import com.example.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/fetchDataAndSaveToDatabase")
    public @ResponseBody String fetchDataAndSaveToDatabase() {
        return exchangeRateService.fetchDataAndSaveToDatabase();
    }

    @GetMapping("/list")
    public String getAllExchangeRates(Model model) {
        List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();
        model.addAttribute("exchangeRates", exchangeRates);
        return "exchange-rate-list"; // Thymeleaf template name
    }

    @GetMapping("/add")
    public String showAddExchangeRateForm(Model model) {
        model.addAttribute("exchangeRate", new ExchangeRate());
        return "add-exchange-rate"; // Thymeleaf template name
    }

    @PostMapping("/add")
    public String addExchangeRate(@ModelAttribute("exchangeRate") ExchangeRate exchangeRate) {
        exchangeRateService.addExchangeRate(exchangeRate);
        return "redirect:/exchange-rates/list";
    }

    @GetMapping("/update/{id}")
    public String showUpdateExchangeRateForm(@PathVariable("id") String id, Model model) {

        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(Long.valueOf(id));
        model.addAttribute("exchangeRate", exchangeRate);
        return "update-exchange-rate"; // Thymeleaf template name
    }

    @PostMapping("/update")
    public String updateExchangeRate(@ModelAttribute("exchangeRate") ExchangeRate exchangeRate) {
        exchangeRateService.updateExchangeRate(exchangeRate.getId(), exchangeRate);
        return "redirect:/exchange-rates/list";
    }

    @PostMapping("/delete/{id}") // Mapping to handle DELETE requests
    public String deleteExchangeRate(@PathVariable("id") Long id) {
        exchangeRateService.deleteExchangeRate(id);
        return "redirect:/exchange-rates/list";
    }
}
