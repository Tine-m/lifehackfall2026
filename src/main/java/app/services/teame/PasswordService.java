package app.services.teame;

import app.entities.teame.PasswordCharacter;
import app.exceptions.DatabaseException;
import app.exceptions.teame.ValidationException;
import app.persistence.teame.CharacterMapper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PasswordService {
    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 128;

    // Grænser for styrkeindikatoren, målt i bits entropi
    private static final double MEDIUM_ENTROPY = 50;
    private static final double STRONG_ENTROPY = 80;

    // SecureRandom bruges i stedet for Random, fordi passwords ikke må kunne forudsiges
    private final Random random = new SecureRandom();
    private final CharacterMapper characterMapper;

    public PasswordService(CharacterMapper characterMapper) {
        this.characterMapper = characterMapper;
    }

    // Henter de tilladte tegn fra databasen og genererer et password med dem
    public String generatePassword(int length, List<Integer> characterTypeIds, boolean excludeAmbiguous)
            throws DatabaseException {
        List<PasswordCharacter> characters = getAllowedCharacters(characterTypeIds, excludeAmbiguous);
        return buildPassword(length, characters, random);
    }

    // Beregner styrken for et password med de valgte indstillinger
    public String getPasswordStrength(int length, List<Integer> characterTypeIds, boolean excludeAmbiguous)
            throws DatabaseException {
        List<PasswordCharacter> characters = getAllowedCharacters(characterTypeIds, excludeAmbiguous);
        return calculateStrength(length, characters.size());
    }

    private List<PasswordCharacter> getAllowedCharacters(List<Integer> characterTypeIds, boolean excludeAmbiguous)
            throws DatabaseException {
        List<PasswordCharacter> characters = characterMapper.getCharactersByTypes(characterTypeIds, excludeAmbiguous);
        if (characters.isEmpty()) {
            throw new DatabaseException("Der blev ikke fundet nogen tegn for de valgte tegntyper");
        }
        return characters;
    }

    // Tjekker at længden er et tal mellem 8 og 128
    public static int validateLength(String input) throws ValidationException {
        return validateNumber(input, MIN_LENGTH, MAX_LENGTH,
                "Længden skal være et tal mellem " + MIN_LENGTH + " og " + MAX_LENGTH + ".");
    }

    // Tjekker at mindst én tegntype er valgt, og omsætter id'erne fra formularen til tal
    public static List<Integer> validateCharacterTypes(List<String> input) throws ValidationException {
        List<Integer> characterTypeIds = new ArrayList<>();
        for (String id : input) {
            try {
                characterTypeIds.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                throw new ValidationException("Ugyldig tegntype.");
            }
        }
        if (characterTypeIds.isEmpty()) {
            throw new ValidationException("Vælg mindst én tegntype.");
        }
        return characterTypeIds;
    }

    // Bygger et password af de tilladte tegn.
    // Først tages ét tegn fra hver tegntype, så alle valgte typer er med.
    // Resten trækkes fra alle tegn, og til sidst blandes rækkefølgen, så de første tegn ikke er forudsigelige
    public static String buildPassword(int length, List<PasswordCharacter> characters, Random random) {
        Map<Integer, List<PasswordCharacter>> charactersByType = new LinkedHashMap<>();
        for (PasswordCharacter character : characters) {
            charactersByType.computeIfAbsent(character.getCharacterTypeId(), id -> new ArrayList<>()).add(character);
        }

        List<Character> password = new ArrayList<>();
        for (List<PasswordCharacter> typeCharacters : charactersByType.values()) {
            password.add(pickRandom(typeCharacters, random).getValue());
        }
        while (password.size() < length) {
            password.add(pickRandom(characters, random).getValue());
        }
        Collections.shuffle(password, random);

        StringBuilder result = new StringBuilder();
        for (char c : password) {
            result.append(c);
        }
        return result.toString();
    }

    // Beregner styrken ud fra entropi: længden gange log2 af antal mulige tegn
    public static String calculateStrength(int length, int possibleCharacters) {
        double entropy = length * (Math.log(possibleCharacters) / Math.log(2));
        if (entropy < MEDIUM_ENTROPY) {
            return "Svag";
        }
        if (entropy < STRONG_ENTROPY) {
            return "Middel";
        }
        return "Stærk";
    }

    private static int validateNumber(String input, int min, int max, String errorMessage)
            throws ValidationException {
        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidationException(errorMessage);
        }
        if (number < min || number > max) {
            throw new ValidationException(errorMessage);
        }
        return number;
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
