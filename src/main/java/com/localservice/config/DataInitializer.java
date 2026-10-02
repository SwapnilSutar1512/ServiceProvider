package com.localservice.config;

import com.localservice.entity.Category;
import com.localservice.entity.Role;
import com.localservice.entity.User;
import com.localservice.repository.CategoryRepository;
import com.localservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeCategories(CategoryRepository categoryRepository, UserRepository userRepository,
                                                 PasswordEncoder passwordEncoder) {
        return args -> {
            if (categoryRepository.count() == 0) {
                List<Category> categories = Arrays.asList(
                        new Category(null, "Plumber", "Professional plumbing services", true, null, null),
                        new Category(null, "Electrician", "Professional electrical services", true, null, null),
                        new Category(null, "Carpenter", "Professional carpentry and woodwork", true, null, null),
                        new Category(null, "Painter", "Professional painting services", true, null, null),
                        new Category(null, "AC Repair", "Air conditioning repair and maintenance", true, null, null),
                        new Category(null, "Cleaner", "Professional cleaning services", true, null, null),
                        new Category(null, "Appliance Repair", "Repair for home appliances", true, null, null),
                        new Category(null, "House Cleaning", "Professional house cleaning", true, null, null),
                        new Category(null, "Pest Control", "Pest control and management", true, null, null)
                );

                categoryRepository.saveAll(categories);
                System.out.println("Service categories initialized successfully");
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@servicelink.local");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);
                userRepository.save(admin);
                System.out.println("Default admin user initialized: username=admin, password=Admin@123");
            }
        };
    }
}
