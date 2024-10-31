package module;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.play.http.PlayHttpActionAdapter;
import org.pac4j.play.store.PlayCacheSessionStore;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import authorizers.AuthenticatedAuthorizer;
import authorizers.AdministratorAuthorizer;
import authorizers.Roles;
import play.Environment;

import play.cache.SyncCacheApi;

import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.Utils;
import org.hascoapi.console.views.html.*;
import static play.mvc.Results.forbidden;
import static play.mvc.Results.unauthorized;


public class SecurityModule extends AbstractModule {

    private final com.typesafe.config.Config configuration;

    private final String baseUrl;

    public SecurityModule(final Environment environment, final com.typesafe.config.Config configuration) {
        this.configuration = configuration;
        this.baseUrl = configuration.getString("hascoapi.console.host");
    }

 
    @Override
    protected void configure() {
        bind(SessionStore.class).to(PlayCacheSessionStore.class);
    }

    @Provides
    protected HeaderClient provideHeaderClient() {
        final HeaderClient headerClient = new HeaderClient("Authorization", "Bearer ",
                new JwtAuthenticator(new SecretSignatureConfiguration(ConfigProp.getJWTSecret())));
        return headerClient;
    }

    @Provides
    protected Config provideConfig(HeaderClient headerClient, SessionStore sessionStore) {
        final Clients clients = new Clients(headerClient, new AnonymousClient());
        PlayHttpActionAdapter.INSTANCE.getResults().put(HttpConstants.UNAUTHORIZED, unauthorized(error401.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE)));
        PlayHttpActionAdapter.INSTANCE.getResults().put(HttpConstants.FORBIDDEN, forbidden(error403.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE)));

        final Config config = new Config(clients);
        config.addAuthorizer(Roles.AUTHENTICATED, new AuthenticatedAuthorizer());
        config.addAuthorizer(Roles.ADMINISTRATOR, new AdministratorAuthorizer());
        config.setSessionStoreFactory(p -> sessionStore);
        return config;
    }

}
