package com.siemens.internship;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "You must enter a name")
    @Size(min = 3, max = 100, message = "Item name must be between 3 and 100 characters")
    private String name;
    // Added validation for the name field, it must not be blank, and name 3<=length<=100
    @NotEmpty(message = "You must enter a description ")
    private String description;
    //Added validation for the description field, it can't be empty
    @NotBlank(message = "You must enter the status of the item")
    private String status;
    //Added validation for the status field, it can't be blank

    @NotBlank(message = "You must enter a email address")
    @Email(message = "Not valid email format", regexp = "^[a-zA-Z0-9._+-]+@[a-zA-Z-0-9]+\\.[a-zA-Z]{2,}$")
    private String email;
    //Added email validation , it can't be blank and added a regex for the email format
}