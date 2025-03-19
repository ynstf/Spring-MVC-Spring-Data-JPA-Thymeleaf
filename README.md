# Hospital Management Application

This Java EE web application is built using Spring Boot, Spring MVC, Thymeleaf, and Spring Data JPA. It allows you to manage patients by displaying, paginating, searching, and deleting patient records (Part 1). In addition, the project has been extended (Part 2) to include a common template page for consistent layout and form validation to ensure that patient input data meets business rules.

---

## Technologies Used

- **Java 21**
- **Spring Boot 3.2.4**
- **Spring MVC** for handling HTTP requests and building the web layer.
- **Spring Data JPA** for ORM and data persistence.
- **Thymeleaf** for server-side HTML templating.
- **Thymeleaf Layout Dialect** (or similar) for creating template pages with reusable fragments.
- **Spring Boot Starter Validation** (Hibernate Validator) for validating form data.
- **Bootstrap (via WebJars)** for responsive UI design.
- **MySQL Connector/J** for connecting to a MySQL database.
- **Lombok** to reduce boilerplate code.
- **Spring Boot DevTools** for live reload during development.

---

## Project Structure & Key Code Excerpts

### Part 1: Patient Management

This section of the application manages patient records. It provides the following functionalities:
- Displaying a list of patients with pagination.
- Searching patients by name.
- Deleting a patient.

#### 1. Main Application Class

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
        patientRepository.save(new Patient(null, "Said", new Date(), false, 1283));
        patientRepository.save(new Patient(null, "Hafsa", new Date(), true, 1230));
    }
}
```

#### 2. JPA Entity

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

#### 3. Repository Interface

**File:** `PatientRepository.java`

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByNomContains(String keyword, Pageable pageable);
    
    @Query("select p from Patient p where p.nom like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}
```

#### 4. Controller

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

#### 5. Thymeleaf Template for Patient Listing

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

#### 6. Maven Dependencies (pom.xml)

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

---

### Part 2: Template Page & Form Validation

In this part, additional features have been added:

- **Template Page (Page Template):**  
  To avoid duplicating common layout elements (like headers, navigation bars, and footers) across multiple views, a global template page has been created. This template uses Thymeleaf's layout features (for example, by employing the [Thymeleaf Layout Dialect](https://github.com/ultraq/thymeleaf-layout-dialect)) so that each specific view (e.g., patient form, confirmation pages) can extend the common layout. This results in a consistent look and feel for the entire application and reduces code redundancy.

- **Form Validation:**  
  Form validation has been integrated into the patient entry process. By adding the Spring Boot Starter Validation dependency, you can now annotate the fields in your entity (or DTO) with validation constraints. For example, you might require that the patient's name has a minimum and maximum length or that a numerical score is within an acceptable range.

#### 1. Adding Validation Annotations to the Entity

Key validation annotations include:
- `@NotBlank`: Ensures a field is not empty
- `@Size`: Validates string length
- `@Min`/`@Max`: Validates numerical ranges
- `@DateTimeFormat`: Formats date input

Add the validation dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 2. Controller Changes for Form Handling

The controller needs to handle form submissions with validation:
- Use `@Valid` annotation to trigger validation
- Include `BindingResult` parameter to check for errors
- Return to form page if validation fails

#### 3. Global Template Structure

The global template typically includes:
- Common header with navigation
- Content placeholder for page-specific content
- Common footer
- Shared CSS and JavaScript resources

---

## Setup and Running the Application

1. **Clone the Repository:**
   ```bash
   git clone <repository-url>
   cd hopital
   ```

2. **Configure Database:**
    - Ensure that XAMPP is running and MySQL is active.
    - The `application.properties` file is configured to connect to a MySQL database (`hopital-db`) on `localhost:3306`.

3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the Application:**
    - Visit [http://localhost:8084/index](http://localhost:8084/index) for the patient list.
