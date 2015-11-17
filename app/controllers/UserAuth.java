package controllers;

import models.Users;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by rebeca on 11/16/2015.
 */
public class UserAuth extends Security.Authenticator {
    // When return is null, Authentication failed
    @Override
    public String getUsername(final Http.Context ctx) {
        String userIdStr = ctx.session().get("user_id");
        if(userIdStr == null) return null;

        Users users = Users.find.byId(Long.parseLong(userIdStr));
        return (users != null ? users.id.toString() : null);
    }

    @Override
    public Result onUnauthorized(final Http.Context ctx) {
        ctx.flash().put("error",
                "Nice try, but you need to log in first!");
        return redirect(routes.Application.index());
    }
}
