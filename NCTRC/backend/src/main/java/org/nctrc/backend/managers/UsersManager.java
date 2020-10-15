package org.nctrc.backend.managers;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.nctrc.backend.config.Constants;
import org.nctrc.backend.model.internal.DayTimeline;
import org.nctrc.backend.model.request.NewUserRequestModel;
import org.nctrc.backend.model.request.SigninDataRequestModel;
import org.nctrc.backend.model.request.SigninRequestModel;
import org.nctrc.backend.model.request.UserRequestModel;
import org.nctrc.backend.model.response.Result;
import org.nctrc.backend.model.response.UserExistsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UsersManager {

  private static final Logger logger = LoggerFactory.getLogger(UsersManager.class);

  private final Set<UserRequestModel> users;

  private final DayTimeline signinTimeLine;

  private final DatabaseManagerInterface databaseManager;

  private int maxCapacity;

  private int currentCapacity;

  @Inject
  public UsersManager(final DatabaseManagerInterface databaseManager) {
    users = new HashSet<>();
    signinTimeLine = new DayTimeline(new Date());
    this.databaseManager = databaseManager;
    this.maxCapacity = databaseManager.loadMaxCapacity();
    this.currentCapacity = 0;
    try {
      users.addAll(databaseManager.getAllUsers());
    } catch (InterruptedException e) {
      logger.warn("Unable to load users from database: " + e.toString());
    }
  }

  public Result signinUser(final SigninRequestModel signinRequestModel) {
    final UserRequestModel user = signinRequestModel.getUser();
    if (users.contains(user)) {
      // TODO: Validate signin to make sure @ capacity and questions are valid
      if (!signinTimeLine.isUserSignedIn(user)) {
        if (this.currentCapacity < this.maxCapacity) {
          final SigninDataRequestModel signinData = signinRequestModel.getSigninData();
          if (signinData.getTemperature() <= Constants.FEVER_TEMPERATURE
              && signinData.getYesQuestion() == null) {
            signinTimeLine.signUserIn(user);
            databaseManager.signinUser(signinRequestModel);
            this.currentCapacity++;
          } else {
            final String information =
                signinData.getYesQuestion() == null
                    ? "Your temperature is too high: " + signinData.getTemperature()
                    : "You answered yes to \"" + signinData.getYesQuestion() + "\"";
            return new Result(412, information);
          }
        } else {
          return new Result(409, "At Capacity - Try again later");
        }

        return new Result(200, "Success");
      } else {
        return new Result(405, "User is already signed in");
      }
    } else {
      return new Result(404, "User does not exist");
    }
  }

  public Result signoutUser(final UserRequestModel user) {
    if (users.contains(user)) {
      if (signinTimeLine.isUserSignedIn(user)) {
        signinTimeLine.signUserOut(user);
        this.currentCapacity--;
        return new Result(200, "Success");
      } else {
        return new Result(405, "User is not signed in to be signed out");
      }
    } else {
      return new Result(404, "User does not exist");
    }
  }

  public Result createAndSigninUser(final NewUserRequestModel newUserRequestModel) {
    final UserRequestModel user = newUserRequestModel.getUser();
    if (users.contains(user)) {
      return new Result(405, "Email " + user.getEmail() + " already exists!");
    } else {
      signinTimeLine.addUser(user);
      users.add(user);
      databaseManager.addUser(newUserRequestModel);
      // Sign user in after creating them
      final SigninRequestModel signinRequestModel =
          new SigninRequestModel(
              newUserRequestModel.getSigninData(), newUserRequestModel.getUser());
      return this.signinUser(signinRequestModel);
    }
  }

  public Result userExists(final UserRequestModel user) {
    return new UserExistsResult(200, "", users.contains(user));
  }
}
