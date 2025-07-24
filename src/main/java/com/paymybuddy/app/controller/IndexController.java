package com.paymybuddy.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
	@GetMapping("/")
	public String title(Model model) {
		model.addAttribute("title", "coucou");
		return "index";
	}
}
