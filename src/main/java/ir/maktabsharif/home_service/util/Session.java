package ir.maktabsharif.home_service.util;

import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import lombok.Getter;

public class Session {
    @Getter
    private static UserSessionDTO currentUser;
    public static void setCurrentUser(UserSessionDTO currentUser) {
        Session.currentUser = currentUser;
    }
}
