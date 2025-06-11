package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Person;
import com.example.demo.service.person.IPersonService;

@RestController
@RequestMapping("/api")
public class PersonController {

    @Autowired
    private IPersonService personService;

    // GET /people - List all people derived from expenses
    @GetMapping("/people")
    public ResponseEntity<?> getAllPeople() {
        List<Person> people = personService.getAllPeople();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", people,
            "message", "List of all participants"
        ));
    }
}

