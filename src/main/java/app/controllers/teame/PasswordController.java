package app.controllers.teame;

import app.entities.teame.CharacterType;
import app.entities.teame.Template;
import app.exceptions.DatabaseException;
import app.exceptions.teame.ValidationException;
import app.persistence.ConnectionPool;
import app.persistence.teame.CharacterMapper;
import app.persistence.teame.CharacterTypeMapper;
import app.persistence.teame.TemplateMapper;
import app.services.teame.PasswordService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class PasswordController {
    private static final String PAGE = "teame/password-generator.html";
    private static final int DEFAULT_LENGTH = 16;

    private final CharacterTypeMapper characterTypeMapper;
    private final TemplateMapper templateMapper;
    private final PasswordService passwordService;

    public PasswordController(ConnectionPool connectionPool) {
        this.characterTypeMapper = new CharacterTypeMapper(connectionPool);
        this.templateMapper = new TemplateMapper(connectionPool);

        this.passwordService = new PasswordService(new CharacterMapper(connectionPool));
    }

    // Alle SecurePass-routes
    public void setRoutes(JavalinConfig config) {
        config.routes.get("/securepass", ctx -> showFrontpage(ctx));
        config.routes.post("/securepass/generate", ctx -> generate(ctx));
        config.routes.post("/securepass/template", ctx -> useTemplate(ctx));
    }

    // Viser forsiden med et password på 16 tegn, hvor alle tegntyper er valgt
    private void showFrontpage(Context ctx) {
        try {
            List<Integer> allTypeIds = new ArrayList<>();
            for (CharacterType characterType : characterTypeMapper.getAllCharacterTypes()) {
                allTypeIds.add(characterType.getCharacterTypeId());
            }

            setFormValues(ctx, String.valueOf(DEFAULT_LENGTH), allTypeIds, false);
            ctx.attribute("password", passwordService.generatePassword(DEFAULT_LENGTH, allTypeIds, false));
            ctx.attribute("strength", passwordService.getPasswordStrength(DEFAULT_LENGTH, allTypeIds, false));
        } catch (DatabaseException e) {
            setFormValues(ctx, String.valueOf(DEFAULT_LENGTH), List.of(), false);
            ctx.attribute("message", "Vi kan ikke hente tegnene lige nu. Prøv igen senere.");

        }


        render(ctx);
    }

    // Tager imod formularen og genererer et nyt password.
    // Bruges både til første generering og til "Generér igen", så indstillingerne bevares
    private void generate(Context ctx) {
        String lengthInput = ctx.formParam("length");
        List<String> typeIdInput = ctx.formParams("characterTypeId");
        boolean excludeAmbiguous = ctx.formParam("excludeAmbiguous") != null;

        setFormValues(ctx, lengthInput, toIntegers(typeIdInput), excludeAmbiguous);



        try {
            int length = PasswordService.validateLength(lengthInput);
            List<Integer> characterTypeIds = PasswordService.validateCharacterTypes(typeIdInput);
            ctx.attribute("password", passwordService.generatePassword(length, characterTypeIds, excludeAmbiguous));
            ctx.attribute("strength", passwordService.getPasswordStrength(length, characterTypeIds, excludeAmbiguous));
        } catch (ValidationException e) {
            ctx.attribute("message", e.getMessage());
        } catch (DatabaseException e) {
            ctx.attribute("message", "Vi kan ikke hente tegnene lige nu. Prøv igen senere.");
        }


        render(ctx);
    }

    // Udfylder formularen med den valgte skabelons indstillinger og genererer et password med dem
    private void useTemplate(Context ctx) {
        try {
            int templateId = Integer.parseInt(ctx.formParam("templateId"));
            Template template = templateMapper.getTemplateById(templateId);

            ctx.attribute("selectedTemplateId", templateId);
            setFormValues(ctx, String.valueOf(template.getLength()), template.getCharacterTypeIds(),
                    template.isExcludeAmbiguous());
            ctx.attribute("password", passwordService.generatePassword(
                    template.getLength(), template.getCharacterTypeIds(), template.isExcludeAmbiguous()));
            ctx.attribute("strength", passwordService.getPasswordStrength(
                    template.getLength(), template.getCharacterTypeIds(), template.isExcludeAmbiguous()));
        } catch (NumberFormatException e) {
            showFrontpage(ctx);
            return;
        } catch (DatabaseException e) {
            setFormValues(ctx, String.valueOf(DEFAULT_LENGTH), List.of(), false);
            ctx.attribute("message", "Vi kan ikke hente skabelonen lige nu. Prøv igen senere.");
        }
        render(ctx);
    }

    // Gemmer formularens værdier, så de står i felterne igen, når siden vises
    private void setFormValues(Context ctx, String length, List<Integer> characterTypeIds,
                               boolean excludeAmbiguous) {
        ctx.attribute("length", length);
        ctx.attribute("selectedTypeIds", characterTypeIds);
        ctx.attribute("excludeAmbiguous", excludeAmbiguous);
    }

    // Henter tegntyper og skabeloner til formularen og viser siden.
    // Fejler databasen, vises en besked i stedet for en fejlside
    private void render(Context ctx) {
        try {
            ctx.attribute("characterTypes", characterTypeMapper.getAllCharacterTypes());
            ctx.attribute("templates", templateMapper.getAllTemplates());
        } catch (DatabaseException e) {
            ctx.attribute("characterTypes", List.of());
            ctx.attribute("templates", List.of());
            ctx.attribute("message", "Vi kan ikke hente tegntyperne og skabelonerne lige nu. Prøv igen senere.");
        }
        ctx.render(PAGE);
    }

    // Omsætter id'erne fra formularen til tal. Ugyldige id'er springes over
    private List<Integer> toIntegers(List<String> input) {
        List<Integer> result = new ArrayList<>();
        for (String value : input) {

            try {

                result.add(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                // Ugyldige værdier ignoreres her. Servicen giver fejlbeskeden
            }
        }
        return result;
    }
}
