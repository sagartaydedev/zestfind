package com.zestfind;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;
    private static final String ADMIN_PASSWORD = "8767Sagar8767";

    @GetMapping("/")
    public String home(Model model) {
        List<LostItem> items = lostItemRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("items", items);
        model.addAttribute("totalCount", lostItemRepository.count());
        return "index";
    }

    @GetMapping("/report")
    public String reportForm() {
        return "report";
    }

    @PostMapping("/report/submit")
    public String submitReport(
            @RequestParam String itemName,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam String dateLost,
            @RequestParam(required = false) String timeLost,
            @RequestParam String college,
            @RequestParam(required = false) String location,
            @RequestParam String reporterName,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile[] images) {

        LostItem item = new LostItem();
        item.setItemName(itemName);
        item.setCategory(category);
        item.setDescription(description);
        item.setDateLost(LocalDate.parse(dateLost));
        if (timeLost != null && !timeLost.isEmpty()) {
            item.setTimeLost(LocalTime.parse(timeLost));
        }
        item.setCollege(college);
        item.setLocation(location);
        item.setReporterName(reporterName);
        item.setPhone(phone);
        item.setEmail(email);
        item.setStatus("LOST");

        if (images != null && images.length > 0 && !images[0].isEmpty()) {
            try {
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) uploadFolder.mkdirs();
                String fileName = UUID.randomUUID() + "_" + images[0].getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(images[0].getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                item.setImagePath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lostItemRepository.save(item);
        return "redirect:/success";
    }

    @GetMapping("/found")
    public String found() {
        return "found";
    }

    @PostMapping("/found/submit")
    public String submitFound(
            @RequestParam String itemName,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam String dateFound,
            @RequestParam(required = false) String timeFound,
            @RequestParam String college,
            @RequestParam String location,
            @RequestParam String finderName,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String itemWith,
            @RequestParam(required = false) MultipartFile[] images) {

        LostItem item = new LostItem();
        item.setItemName(itemName);
        item.setCategory(category);
        item.setDescription(description);
        item.setDateLost(LocalDate.parse(dateFound));
        if (timeFound != null && !timeFound.isEmpty()) {
            item.setTimeLost(LocalTime.parse(timeFound));
        }
        item.setCollege(college);
        item.setLocation(location);
        item.setReporterName(finderName);
        item.setPhone(phone);
        item.setEmail(email);
        item.setStatus("FOUND");

        if (images != null && images.length > 0 && !images[0].isEmpty()) {
            try {
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) uploadFolder.mkdirs();
                String fileName = UUID.randomUUID() + "_" + images[0].getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(images[0].getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                item.setImagePath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lostItemRepository.save(item);
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
    @GetMapping("/admin")
    public String adminLogin() {
        return "admin-login";
    }
    
    @PostMapping("/admin/login")
    public String adminLoginPost(@RequestParam String password,
                                  Model model,
                                  jakarta.servlet.http.HttpSession session) {
        if (ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Wrong password! Try again.");
        return "admin-login";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model,
                                  jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin";
        }
        model.addAttribute("items", lostItemRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("total", lostItemRepository.count());
        return "admin";
    }
    
    @GetMapping("/admin/logout")
    public String adminLogout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/admin/status/{id}/{status}")
    public String updateStatus(@PathVariable Long id, @PathVariable String status,
                                jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin";
        lostItemRepository.findById(id).ifPresent(item -> {
            item.setStatus(status);
            lostItemRepository.save(item);
        });
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/admin/delete/{id}")
    public String deleteItem(@PathVariable Long id,
                              jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin";
        lostItemRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}