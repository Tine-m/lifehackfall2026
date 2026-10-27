package app.controllers.teamd;

import app.entities.teamd.Brand;
import app.entities.teamd.SizeGuide;
import app.exceptions.DatabaseException;
import app.exceptions.teamd.ValidationException;
import app.persistence.ConnectionPool;
import app.persistence.teamd.BrandMapper;
import app.persistence.teamd.SizeGuideMapper;
import app.services.teamd.SizeService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import java.util.List;

public class PerfectFitController {
    private final BrandMapper brandMapper;
    private final SizeService sizeService;

    public PerfectFitController(ConnectionPool connectionPool) {
        this.brandMapper = new BrandMapper(connectionPool);
        this.sizeService = new SizeService(new SizeGuideMapper(connectionPool));
    }

    // Alle Perfect Fit-routes
    public void setRoutes(JavalinConfig config) {
        config.routes.get("/perfectfit", ctx -> showFrontpage(ctx));
        config.routes.get("/perfectfit/calculate", ctx -> showCalculator(ctx));
        config.routes.post("/perfectfit/calculate", ctx -> calculate(ctx));
        config.routes.get("/perfectfit/measure", ctx -> showMeasure(ctx));
    }

    // Viser forsiden
    private void showFrontpage(Context ctx) {
        ctx.render("teamd/frontpage.html");
    }

    // Viser beregn-siden med mærkerne fra databasen i dropdown'en
    private void showCalculator(Context ctx) {
        try {
            List<Brand> brands = brandMapper.getAllBrands();
            ctx.attribute("brands", brands);
        } catch (DatabaseException e) {
            ctx.attribute("message", "Mærkerne kunne ikke hentes. Prøv igen senere.");
        }
        ctx.render("teamd/calculate.html");
    }

    // Tager imod formularen, validerer målene og viser resultatet
    private void calculate(Context ctx) {
        String brandId = ctx.formParam("brandId");
        String underbust = ctx.formParam("underbust");
        String bust = ctx.formParam("bust");

        ctx.attribute("selectedBrandId", brandId);
        ctx.attribute("underbust", underbust);
        ctx.attribute("bust", bust);

        try {
            SizeService.validateInput(brandId, underbust, bust);
            SizeGuide sizeGuide = sizeService.calculateSize(brandId, underbust, bust);
            Brand brand = brandMapper.getBrandById(sizeGuide.getBrandId());

            ctx.attribute("brandName", brand.getName());
            ctx.attribute("website", brand.getWebsite());
            ctx.attribute("size", sizeGuide.getSize());
            ctx.attribute("sizeSystem", sizeGuide.getSizeSystem());
            ctx.attribute("sisterSizes", sizeService.getSisterSizes(sizeGuide));
            ctx.render("teamd/result.html");
        } catch (ValidationException e) {
            ctx.attribute("message", e.getMessage());
            showCalculator(ctx);
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt. Prøv igen senere.");
            showCalculator(ctx);
        }
    }

    // Viser "Sådan måler du"-siden
    private void showMeasure(Context ctx) {
        ctx.render("teamd/measure.html");
    }
}