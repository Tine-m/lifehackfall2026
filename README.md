# Lifehack Fall 2026

![Lifehack](src/main/resources/public/images/main-page/lifehacklogo.png)

## Kom i gang

1. Clone dette repo
2. Opret en `lifehack` database i Postgres
3. Åben IntelliJ og importér alle dependencies
4. Build og kør app og se den på port 7070



## Fælles forside og registrering af routes

I skal indledningsvis lave to registreringer:
- Registrering af team controllers i `Main.java`, som gør jeres endpoints tilgængelige i Javalin.
- Registrering af jeres Lifehacks i `LifehackRegistry.java`, som gør teamets applikation synlig på den fælles forside.

### Registrering af team controller i `Main.java`:

```java
Javalin.create(config -> {

    config.staticFiles.add("/public");

    config.fileRenderer(
        new JavalinThymeleaf(
            ThymeleafConfig.templateEngine()
        )
    );

    // Fælles forside
    new MainController().setRoutes(config);

    // teamteachers
    new QuoteController(connectionPool).setRoutes(config);

    // TODO: Registrér jeres egne controllers her

}).start(7070);
```

---

### Registrering af life hack på fælles forside i `LifehackRegistry.java`

For at et life hack skal vises på den fælles forside, skal det oprettes som LifehackSite entity i en liste:

```java
public class LifehackRegistry {

    public static List<LifehackSite> getSites() {
        return List.of(
                // teamteachers
                new LifehackSite(
                        "Filosoffernes hjørne",
                        "Få et råd",
                        "/philosophers",
                        "/images/teamteachers/socrates.jpg"
                )
        );
    }
}

```

