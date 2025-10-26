package ru.nsu.neuropsychologist.neuro_psychologist_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class NeuroPsychologistBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeuroPsychologistBackendApplication.class, args);
	}

}
