package app.entities.teame;

// En tegntype, fx "Store bogstaver" eller "Tal" du ved
public class CharacterType {
    private int characterTypeId;
    private String name;

    public CharacterType(int characterTypeId, String name) {
        this.characterTypeId = characterTypeId;
        this.name = name;
    }

    public int getCharacterTypeId() {
        return characterTypeId;
    }

    public String getName() {
        return name;
    }
}
