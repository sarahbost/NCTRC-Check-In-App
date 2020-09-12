package org.nctrc.backend.modules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import io.javalin.Javalin;
import java.io.FileNotFoundException;
import java.net.URL;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.NotNull;
import org.nctrc.backend.config.Constants;
import org.nctrc.backend.startup.entrypoint.AppEntrypoint;
import org.nctrc.backend.startup.entrypoint.EntrypointType;
import org.nctrc.backend.startup.entrypoint.WebEntrypoint;

public class WebModule extends AbstractModule {
  private final Javalin javalin;

  private WebModule(Javalin javalin) {
    this.javalin = javalin;
  }

  @NotNull
  public static WebModule create() {
    return new WebModule(
        Javalin.create(
            javalinConfig -> {
              javalinConfig.enforceSsl = true;
              javalinConfig.enableDevLogging();
              javalinConfig.server(
                  () -> {
                    final Server server = new Server();
                    try {
                      final ServerConnector sslConnector =
                          new ServerConnector(server, getSslContextFactory());
                      sslConnector.setPort(443);
                      server.addConnector(sslConnector);
                    } catch (FileNotFoundException e) {
                      final ServerConnector httpConnector = new ServerConnector(server);
                      httpConnector.setPort(Constants.PORT);
                      server.addConnector(httpConnector);
                    }
                    return server;
                  });
            }));
  }

  private static SslContextFactory getSslContextFactory() throws FileNotFoundException {
    final SslContextFactory sslContextFactory = new SslContextFactory.Server();
    final URL keystorePath = WebModule.class.getResource("/keystore/keystore.jks");
    if (keystorePath == null) {
      throw new FileNotFoundException("Can't find keystore.jsk file");
    }
    sslContextFactory.setKeyStorePath(keystorePath.toString());
    return sslContextFactory;
  }

  @Override
  protected void configure() {
    bind(Javalin.class).toInstance(javalin);
    MapBinder.newMapBinder(binder(), EntrypointType.class, AppEntrypoint.class)
        .addBinding(EntrypointType.REST)
        .to(WebEntrypoint.class);
  }
}