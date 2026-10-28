package app.services.teame;

import app.entities.teame.PasswordCharacter;
import app.exceptions.teame.ValidationException;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private static final int UPPER = 1;
    private static final int LOWER = 2;
    private static final int DIGITS = 3;
    private static final int SYMBOLS = 4;

    private final Random random = new SecureRandom();

    // Laver testtegn, så testene ikke afhænger af databasen
    private List<PasswordCharacter> characters(String values, int characterTypeId) {
        List<PasswordCharacter> result = new ArrayList<>();
        for (char c : values.toCharArray()) {
            result.add(new PasswordCharacter(c, characterTypeId));
        }
        return result;
    }

    private List<PasswordCharacter> allCharacters() {
        List<PasswordCharacter> result = new ArrayList<>();
        result.addAll(characters("ABCDEFGHIJKLMNOPQRSTUVWXYZ", UPPER));
        result.addAll(characters("abcdefghijklmnopqrstuvwxyz", LOWER));
        result.addAll(characters("0123456789", DIGITS));
        result.addAll(characters("!@#$%^&*()-_=+[]{};:,.?", SYMBOLS));
        return result;
    }

    // us1 og uS3: passwordet har den valgte længde
    @Test
    void passwordHarDenValgteLaengde() {
        String password = PasswordService.buildPassword(24, allCharacters(), random);
        assertEquals(24, password.length());
    }

    // US 1: 1000 passwords med samme indstillinger er alle forskellige
    @Test
    void passwordsErTilfaeldige() {
        Set<String> passwords = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            passwords.add(PasswordService.buildPassword(16, allCharacters(), random));
        }
        assertEquals(1000, passwords.size());
    }

    // US4: kun de valgte tegntyper bruges, og hver type er med mindst én gang
    @Test
    void storeBogstaverOgTalErBeggeMedOgIntetAndet() {
        List<PasswordCharacter> chosen = new ArrayList<>();
        chosen.addAll(characters("ABCDEFGHIJKLMNOPQRSTUVWXYZ", UPPER));
        chosen.addAll(characters("0123456789", DIGITS));

        for (int i = 0; i < 200; i++) {
            String password = PasswordService.buildPassword(8, chosen, random);
            assertTrue(password.matches("[A-Z0-9]+"), password);
            assertTrue(password.matches(".*[A-Z].*"), password);
            assertTrue(password.matches(".*[0-9].*"), password);
        }
    }

    // US7 : udelukkde tegn kommer aldrig med
    @Test
    void udelukkedeTegnBrugesIkke() {
        List<PasswordCharacter> withoutAmbiguous = characters("23456789", DIGITS);
        for (int i = 0; i < 200; i++) {
            String password = PasswordService.buildPassword(16, withoutAmbiguous, random);
            assertFalse(password.contains("0") || password.contains("1"), password);
        }
    }

    // US3: gyldig længde accepteres
    @Test
    void gyldigLaengdeGiverIngenFejl() throws ValidationException {
        assertEquals(8, PasswordService.validateLength("8"));
        assertEquals(128, PasswordService.validateLength("128"));
    }

    // US3: længde uden for 8-128 giver fejl
    @Test
    void laengdeUdenForGraenserneGiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> PasswordService.validateLength("7"));
        assertEquals("Længden skal være et tal mellem 8 og 128.", e.getMessage());
        assertThrows(ValidationException.class, () -> PasswordService.validateLength("129"));
    }

    // US3: bogstaver eller tomt felt giver fejl i stedet for at crashe
    @Test
    void laengdeDerIkkeErEtTalGiverFejl() {
        assertThrows(ValidationException.class, () -> PasswordService.validateLength("abc"));
        assertThrows(ValidationException.class, () -> PasswordService.validateLength(""));
        assertThrows(ValidationException.class, () -> PasswordService.validateLength(null));
    }

    // US4: ingen valgte tegntyper giver fejl
    @Test
    void ingenTegntyperGiverFejl() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> PasswordService.validateCharacterTypes(List.of()));
        assertEquals("Vælg mindst én tegntype.", e.getMessage());
    }

    // US6: 8 tegn med kun tal er svagt
    @Test
    void kortPasswordMedKunTalErSvagt() {
        assertEquals("Svag", PasswordService.calculateStrength(8, 10));
    }

    // US6: 16 tegn med alle fire tegntyper er stærkt
    @Test
    void langtPasswordMedAlleTegntyperErStaerkt() {
        assertEquals("Stærk", PasswordService.calculateStrength(16, allCharacters().size()));
    }
}
