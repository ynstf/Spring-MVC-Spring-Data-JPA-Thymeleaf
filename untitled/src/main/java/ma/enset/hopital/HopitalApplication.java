package ma.enset.hopital;

import ma.enset.hopital.entities.Patient;
import ma.enset.hopital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class HopitalApplication implements CommandLineRunner {
    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(HopitalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Patient patient = new Patient();
//        patient.setId(null);
//        patient.setNom("Youness");
//        patient.setDateNaissance(new Date());
//        patient.setMalade(false);
//        patient.setScore(23);
//
//        Patient patient2 = new Patient(null,"said",new Date(),false,123);
//
//        Patient patient3 = Patient.builder()
//                .nom("hafsa")
//                .dateNaissance(new Date())
//                .score(29)
//                .malade(true)
//                .build();

        //patientRepository.save(new Patient(null,"Youness",new Date(),false,123));
        //patientRepository.save(new Patient(null,"said",new Date(),false,1283));
        //patientRepository.save(new Patient(null,"hafsa",new Date(),true,1230));

    }
}
