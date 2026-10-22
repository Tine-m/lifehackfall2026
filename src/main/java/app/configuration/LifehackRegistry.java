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

                new LifehackSite(
                        "Team c's Lifehack",
                        "EventPlanner",
                        "/eventPlanner",
                        "/images/team.c/event-kort.png" ) ,

                new LifehackSite(
                        "RcadeGB",
                        "Match a colour",
                        "/rcade",
                        "/images/teama/rcade-logo.svg"
                ),

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
