# Hospital Management Application

This Java EE web application is built using Spring Boot, Spring MVC, Thymeleaf, and Spring Data JPA. It allows you to manage patients by displaying, paginating, searching, and deleting patient records. Below you will find technical details of the important code parts and what they mean.

## Technologies Used
- **Java 21**
- **Spring Boot 3.2.4**
- **Spring MVC** for handling HTTP requests and building the web layer.
- **Spring Data JPA** for ORM and data persistence.
- **Thymeleaf** for server-side HTML templating.
- **Bootstrap (via WebJars)** for responsive UI design.
- **MySQL Connector/J** for connecting to a MySQL database (using XAMPP).
- **Lombok** to reduce boilerplate code (getters, setters, etc.).
- **Spring Boot DevTools** for live reload during development.

## Project Structure & Key Code Excerpts

### 1. Main Application Class

**File:** `HopitalApplication.java`

```java
@SpringBootApplication
public class HopitalApplication implements CommandLineRunner {
    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(HopitalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Seeding the database with sample patient data on startup.
        patientRepository.save(new Patient(null, "Youness", new Date(), false, 123));
        patientRepository.save(new Patient(null, "said", new Date(), false, 1283));
        patientRepository.save(new Patient(null, "hafsa", new Date(), true, 1230));
    }
}
```

**Explanation:**
- `@SpringBootApplication`: This annotation sets up component scanning, auto-configuration, and property support.
- `implements CommandLineRunner`: The `run()` method executes after the application starts, allowing initial data seeding.
- `patientRepository.save(...)`: Inserts sample records into the database.

### 2. JPA Entity

**File:** `Patient.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private int score;
}
```

**Explanation:**
- `@Entity`: Marks this class as a JPA entity.
- `@Id` and `@GeneratedValue`: Designate the primary key and its auto-generation strategy.
- Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`): Automatically generate getters, setters, constructors, and a builder pattern to reduce boilerplate code.

### 3. Repository Interface

**File:** `PatientRepository.java`

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByNomContains(String keyword, Pageable pageable);
    
    @Query("select p from Patient p where p.nom like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}
```

**Explanation:**
- `extends JpaRepository<Patient, Long>`: Provides CRUD operations and pagination support for the Patient entity.
- `findByNomContains`: Method that Spring Data JPA automatically implements to filter patients by a keyword.
- Custom `@Query`: Demonstrates how to write custom JPQL queries with pagination.

### 4. Controller

**File:** `PatientController.java`

```java
@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int p,
                        @RequestParam(name = "size", defaultValue = "4") int s,
                        @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<Patient> pagePatients = patientRepository.findByNomContains(kw, PageRequest.of(p, s));
        model.addAttribute("patientList", pagePatients);
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "patients";
    }

    @GetMapping("/delete")
    public String delete(Long id, String keyword, int page) {
        patientRepository.deleteById(id);
        return "redirect:index?page=" + page + "&keyword=" + keyword;
    }
}
```

**Explanation:**
- `@Controller`: Indicates that this class handles web requests.
- `@GetMapping("/index")`: Maps HTTP GET requests for the patient listing.  
  - Uses pagination and keyword search to filter patients.
  - Passes model attributes to the Thymeleaf view (`patients.html`).
- `@GetMapping("/delete")`: Handles deletion of a patient and redirects to the updated list.

### 5. Thymeleaf Template

**File:** `patients.html`

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>List Patients</title>
    <!-- Bootstrap CSS from WebJar -->
    <link rel="stylesheet" th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}">
    <link rel="stylesheet" th:href="@{/webjars/bootstrap-icons/1.11.3/font/bootstrap-icons.css}">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">List Patients</h2>
    <form th:action="@{index}" method="get">
        <label>Keyword:</label>
        <input type="text" name="keyword" th:value="${keyword}">
        <button type="submit" class="btn btn-info">
            <i class="bi bi-search"></i>
        </button>
    </form>
    <table class="table table-striped table-bordered">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Date</th>
                <th>Malade</th>
                <th>Score</th>
                <th>Deletion</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="p : ${patientList}">
                <td th:text="${p.id}"></td>
                <td th:text="${p.nom}"></td>
                <td th:text="${p.dateNaissance}"></td>
                <td th:text="${p.malade}"></td>
                <td th:text="${p.score}"></td>
                <td>
                    <a th:href="@{delete(id=${p.id},keyword=${keyword},page=${currentPage})}" class="btn btn-danger">
                        <i class="bi bi-trash"></i>
                    </a>
                </td>
            </tr>
        </tbody>
    </table>
    <ul class="nav nav-pills">
        <li th:each="page, status : ${pages}">
            <a th:href="@{/index(page=${status.index},keyword=${keyword})}"
               th:class="${currentPage == status.index ? 'btn btn-info ms-1' : 'btn btn-outline-info ms-1'}"
               th:text="${status.index}"></a>
        </li>
    </ul>
</div>
<!-- Bootstrap Bundle JS from WebJar -->
<script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>
</body>
</html>
```

**Explanation:**
- **Bootstrap Integration:**  
  The `<link>` and `<script>` tags use Thymeleaf's resource syntax to load Bootstrap and Bootstrap Icons from WebJars.
- **Dynamic Content:**  
  Thymeleaf expressions (e.g., `th:text`, `th:each`, and `th:href`) dynamically inject patient data, manage pagination, and handle actions like deletion.
- **Responsive Design:**  
  The Bootstrap classes (e.g., `container`, `table`, `btn`, etc.) ensure that the UI is responsive and visually appealing.

### 6. Configuration & Dependencies

**File:** `pom.xml`

```xml
<!-- Excerpt from pom.xml -->
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <!-- Database Connectors -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- WebJars for Bootstrap -->
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>bootstrap</artifactId>
        <version>5.3.0</version>
    </dependency>
    <dependency>
        <groupId>org.webjars.npm</groupId>
        <artifactId>bootstrap-icons</artifactId>
        <version>1.11.3</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- Spring Boot DevTools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

**Explanation:**
- **Spring Boot Starters:**  
  These bring in the necessary dependencies for data access, web services, and templating.
- **Database Connectors:**  
  Include both H2 (for testing) and MySQL (for production with XAMPP).
- **WebJars:**  
  Package client-side libraries like Bootstrap and Bootstrap Icons as JAR files, which are automatically served as static resources.
- **Lombok:**  
  Reduces boilerplate code in entity and other classes.
- **DevTools:**  
  Improves the development experience with live reloading.

## Setup and Running the Application

1. **Clone the Repository:**
   ```bash
   git clone <repository-url>
   cd hopital
   ```

2. **Configure Database:**
   - Make sure XAMPP is running and MySQL is active.
   - The `application.properties` file is configured to connect to a MySQL database (`hopital-db`) on `localhost:3306`.

3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the Application:**
   Navigate to [http://localhost:8084/index](http://localhost:8084/index) in your browser.
