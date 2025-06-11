package com.example.demo.service.person;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;

@Service
public class PersonService implements IPersonService {
	@Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }
}
