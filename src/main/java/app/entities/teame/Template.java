package app.entities.teame;

import java.util.List;

// En skabelon, fx "PIN-kode", med de indstillinger den udfylder formularen med
public class Template {
    private int templateId;
    private String name;
    private int length;
    private boolean excludeAmbiguous;
    private List<Integer> characterTypeIds;

    public Template(int templateId, String name, int length, boolean excludeAmbiguous, List<Integer> characterTypeIds) {
        this.templateId = templateId;
        this.name = name;
        this.length = length;
        this.excludeAmbiguous = excludeAmbiguous;
        this.characterTypeIds = characterTypeIds;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public boolean isExcludeAmbiguous() {
        return excludeAmbiguous;
    }

    public List<Integer> getCharacterTypeIds() {
        return characterTypeIds;
    }
}
