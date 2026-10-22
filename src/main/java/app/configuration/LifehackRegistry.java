package app.configuration;

import app.entities.LifehackSite;

import java.util.List;

public class LifehackRegistry {

    public static List<LifehackSite> getSites() {
        return List.of(
                // teamteachers
                new LifehackSite(
                        "Filosoffernes hjørne",
                        "Få et råd",
                        "/philosophers",
                        "/images/teamteachers/socrates.jpg"
                ),
                //Team A
                new LifehackSite(
                        "RcadeGB",
                        "Match a colour",
                        "/rcade",
                        "/images/teama/rcade-logo.svg"
                )/*,
                ),
                /*,

                // Team A
                new LifehackSite(
                        "Team A's Lifehack",
                        "Prøv det her",
                        "/teama",
                        "/images/teama/logo.png"
                )*/

                // Team D
                new LifehackSite(
                        "PerfectFit",
                        "Find din perfekte BH-størrelse",
                        "/perfectfit",
                        "/images/teamd/perfectfit-logo.svg"
                 )
        );
    }
}
