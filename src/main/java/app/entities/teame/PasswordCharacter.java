package app.entities.teame;

// et tegn, der kan indgå i et password, og hvilke a tegntype det hører til
public class PasswordCharacter {
    private char value;
    private int characterTypeId;

    public PasswordCharacter(char value, int characterTypeId) {
        this.value = value;
        this.characterTypeId = characterTypeId;
    }

    public char getValue() {
        return value;
    }

    public int getCharacterTypeId() {
        return characterTypeId;
    }
}
