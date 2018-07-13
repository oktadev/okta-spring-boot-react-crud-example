package com.okta.developer.jugtours;

import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
public class JugToursApplication {

	public static void main(String[] args) {
		SpringApplication.run(JugToursApplication.class, args);
	}

	@Bean
    CommandLineRunner init() {
	    return args -> {
	        System.out.println("Hello Spring!");
        };
    }
}

@Data
@Entity
class Meetup {

    public Meetup(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}

interface MeetupRepository extends JpaRepository<Meetup, Long> {
}

@RestController
class MeetupController {

	private MeetupRepository repository;

	public MeetupController(MeetupRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/api/meetups")
	Collection<Meetup> meetups() {
		return repository.findAll();
	}
}

@Component
class MeetupCommandLineRunner implements CommandLineRunner {

    private final MeetupRepository repository;

    public MeetupCommandLineRunner(MeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        Stream.of("Denver JUG", "Utah JUG", "Seattle JUG",
                "Richmond JUG").forEach(name ->
                repository.save(new Meetup(name))
        );
        repository.findAll().forEach(System.out::println);
    }
}