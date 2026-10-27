# Lifehack projekt

Dette dokument beskriver de fælles regler for struktur og kodestandard i vores fælles GitHub repository.

Formålet er at sikre:
- ensartet struktur
- overskuelig kode
- lettere samarbejde
- lettere feedback


## Generelt
- Vi skriver al kode på engelsk
- Det er vigtigere med kode der nemt kan læses, end "korte one liners" 
- Husk at skrive kommentarer som hjælp til læsning af koden 
- Husk at opdatere kommentarer ved ændring af eksisterende kode 
- En god kommentar beskriver hvad og hvorfor noget sker, ikke hvordan 

### I må ikke ændre i andre gruppers kode uden aftale!

## Teknologi

Følgende er specificeret i `pom.xml` og versioner må ikke ændres. 

**Java 21** skal anvendes af alle teams. Projektet skal kunne kompileres og afvikles med denne version.

**Maven** anvendes til håndtering af dependencies og build. Alle teams arbejder med den fælles pom.xml.

**Javalin og Thymeleaf 7.2.3** anvendes til udvikling af webapplikationen. HTML-sider genereres på serversiden med Thymeleaf.

**PostgreSQL** anvendes som fælles databaseteknologi. 

**JUnit 5** anvendes til automatiserede tests.

**SLF4J 2.0.17** anvendes til logging i applikationen.


Behov for nye biblioteker eller versionsopdateringer skal drøftes og godkendes i føllesskab, før ændringerne integreres i `main` branch.

Dette skal sikre, at alle teams arbejder med en ensartet teknologistak og undgår unødvendige konflikter i den fælles kodebase.

## Overordnet struktur

Projektet er opdelt i packages. Hver package repræsenterer en type ansvar/arbejdsopgave. 

```
app/
├── configuration/
├── controllers/
├── dto/
├── entities/
├── exceptions/
├── persistence/
├── services/
└── Main.java/
```

## Yderligere opdeling af kode 

### Java klasser

Hver gruppe placerer deres Java klasser i **sub packages** navngivet efter teamnavn:


```
app/controllers/teama/
app/controllers/teamb/
app/entities/teama/
app/entities/teamb/
```

### Templates

Templates placeres under:

```
src/main/resources/templates/
```

Hver gruppe opretter en undermappe:

```
templates/teama/
templates/teamb/
```

Eksempel:
```
templates/teama/error.html
templates/teamb/error.html
```

### CSS og assets
Placeres i undermapper til disse:

```
resources/public/css/
resources/public/images/
resources/public/js/
```
Eksempel:


```
css/teama/
images/teama/
```

### SQL scripts
Placeres i undermapper til denne:

```
resources/sql/
```
Eksempel:


```
sql/teama/init.sql
sql/teamb/init.sql
```

### Dokumentation
Placeres i roden af projektet i undermapper til docs:

```
lifehackfall2026/docs/
```
Eksempel:

```
docs/teama/domainmodel.pump
```

---
## Navngivning

### Java klasser

Klassenavne følger Javas standard, dvs. PascalCase (Stort begyndelsesbogstav).

```java
PostController
UserMapper
Book
```

### Java metoder og variabler

Metoder og variable følger Javas standard, dvs. camelCase (lille begyndelsesbogstav).

```java
createUser()
getAllPosts()
```

Undgå:
```java
Team1PostController   ❌
User_controller       ❌
```

👉 Team-navn skal fremgår af **package**, ikke i klassens navn.


### HTML og templates
- små bogstaver
- bindestreg for sammensatte ord

Eksempel: 

```text
books.html
create-user.html
```


### Database 
Fordi alle arbejder i samme database, *skal* tabeller have teamnavn som præfix for at vi kan undgå navnesammenfald. Vi benytter snake case med '_'.

Eksempel: 

```sql
teama_users
teamb_users
```

Vær konsistent i navngivning af tabeller i enten ental eller flertal.

Vi benytter også snake case  til sammensatte kolonnenavne, keys og indexes. 

Eksempel:

```text
user_id
salary_group
```

**Brug udelukkende små bogstaver i tabelnavne, kolonner, keys indexes m.m.**
Det gælder både i SQL scripts og i JDBC koden. Det får nemlig betydning, når vi deployer til en Linux server, som er case sensitiv.

---

## Routing (Javalin)

### URL-præfix

Hver gruppe SKAL have egne URL præfix baseret på navn på jeres life hack app.

Eksempel:

```
/pizzacalculator/orders
/pizzacalculator/menu

/bmicalculator/calculate
/bmicalculator/history
```

---
## Java kodeprincipper

### Klasser
Skal følge Single Responsibility princippet. Dvs. kun have ét ansvar.

Det kunne være være: 
- en Data Mapper, som har ansvar for at opdatere brugeroplysninger i databasen. 
- en DTO klasse, som samler data på tværs af domæneobjekter og returnerer i ét kald.
- en Service klasse til at beregne den samlede pris for en ordre.

### Metoder
Er som udgangsunkt **ikke** *static*
- skal være korte
- skal have ét ansvar

---

## Fejlhåndtering

- input skal valideres
- brugeren skal have fejlbeskeder
- applikationen må ikke crashe

---


## Git 

- Udfyld navn i Git config 
- Branch merge sker altid med code review (Pull Request)
- Branch navn skal være beskrivende og enten indeholde teamnavn + tekst eller issue nr., f.eks. teama-animated-menu-items eller issue-#16
- Commit beskrivelser skal være sigende. 

### Gode commit-beskeder:
```
Add post entity
Create user registration
Fix validation bug
```

### Dårlige:
```
fix
done
hej
```
