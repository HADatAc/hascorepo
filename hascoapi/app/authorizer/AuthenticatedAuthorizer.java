package authorizers;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class AuthenticatedAuthorizer extends ProfileAuthorizer {

    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) throws HttpAction {
        //System.out.println("Is  DataOwnerRoleAuthorizer authorised was called:"+isAnyAuthorized(context, profiles));
        return isAnyAuthorized(context, sessionStore, profiles);
    }

    @Override
    public  boolean isProfileAuthorized(final WebContext context,  final SessionStore sessionStore, final UserProfile profile) {
        if (profile == null) {
            return false;
        }
        Set<String> roles = profile.getRoles();
        List attributeRolesArray = (ArrayList)profile.getAttribute("roles");
        //System.out.println("Is DataOwnerRoleAuthorizer profile authorised was called");
         
        String[] attributeRoles = null;
        if (attributeRolesArray != null) {
            attributeRoles = new String[attributeRolesArray.size()];
            for (int i = 0; i < attributeRolesArray.size(); i++) {
                attributeRoles[i] = (String) attributeRolesArray.get(i);
                //System.out.println("Role " + i + ": " + attributeRoles[i]);
            }
        }
        if (attributeRoles != null) {
            List<String> attributeRolesList = Arrays.asList(attributeRoles);
             return attributeRolesList.contains(Roles.AUTHENTICATED);
        }
        
        return false;
    }

}
