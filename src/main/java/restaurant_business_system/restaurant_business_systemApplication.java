package restaurant_business_system;

import java.util.EnumSet;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import restaurant_business_system.api.AccountAPI;
import restaurant_business_system.api.FoodApi;
import restaurant_business_system.api.MenuAPI;
import restaurant_business_system.api.RestaurantAPI;
import restaurant_business_system.auth.BasicAuthenticator;
import restaurant_business_system.core.User;
import restaurant_business_system.db.account.AccountDAO;
import restaurant_business_system.db.food.FoodDAO;
import restaurant_business_system.db.menu.MenuDAO;
import restaurant_business_system.db.restaurant.RestaurantDAO;

/**
 * The main application class for the restaurant business system.
 * This class extends the Application class and is responsible for initializing
 * and running the application.
 */
public class restaurant_business_systemApplication extends Application<RestaurantBusinessSystemConfiguration> {

    @UnitOfWork
    public static void main(final String[] args) throws Exception {
        new restaurant_business_systemApplication().run(args);
    }

    @Override
    public String getName() {
        return "restaurant_business_system";
    }

    @Override
    public void initialize(final Bootstrap<RestaurantBusinessSystemConfiguration> bootstrap) {
    }

    @Override
    @UnitOfWork
    public void run(final RestaurantBusinessSystemConfiguration configuration,
            final Environment e) {
        e.jersey().register(new JsonProcessingExceptionMapper(true));

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(e, configuration.getDataSourceFactory(), "mysql");

        e.jersey().register(new AccountAPI(new AccountDAO(jdbi)));
        e.jersey().register(new RestaurantAPI(new RestaurantDAO(jdbi)));
        e.jersey().register(new MenuAPI(new MenuDAO(jdbi)));
        e.jersey().register(new FoodApi(new FoodDAO(jdbi)));

        // Auth
        e.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new BasicAuthenticator(new AccountDAO(jdbi)))
                        // .setAuthorizer(new UserAuthorizer())
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));
        e.jersey().register(RolesAllowedDynamicFeature.class);
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        // Enable CORS
        final FilterRegistration.Dynamic cors = e.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
