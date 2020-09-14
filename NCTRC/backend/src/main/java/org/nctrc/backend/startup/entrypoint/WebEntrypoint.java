package org.nctrc.backend.startup.entrypoint;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.google.inject.Inject;
import io.javalin.Javalin;
import org.nctrc.backend.config.Constants;
import org.nctrc.backend.controllers.UserSigninController;

public class WebEntrypoint implements AppEntrypoint {

  private final Javalin javalin;

  private final UserSigninController userSigninController;

  @Inject
  public WebEntrypoint(final Javalin javalin, final UserSigninController userSigninController) {
    this.javalin = javalin;
    this.userSigninController = userSigninController;
  }

  @Override
  public void boot(String[] args) {
    javalin
        .routes(
            () -> {
              path(
                  Constants.MAIN_PATH,
                  () -> {
                    path(
                        Constants.USER_SIGNIN_PATH,
                        () -> {
                          post(this.userSigninController::login);
                        });
                  });
            })
        .start();
  }
}
