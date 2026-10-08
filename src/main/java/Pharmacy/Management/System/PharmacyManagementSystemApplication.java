package Pharmacy.Management.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PharmacyManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmacyManagementSystemApplication.class, args);
		System.out.println("=================================================");
		System.out.println(" Pharmacy Management System is running!");
		System.out.println(" Open in your browser: http://localhost:8080");
		System.out.println(" API base path:        http://localhost:8080/api/suppliers");
		System.out.println("=================================================");
	}

}
