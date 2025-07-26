package ir.maktabsharif.home_service.security;

import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import org.mapstruct.control.MappingControl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

    public UserDetailsImpl getCurrentUser(){
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
