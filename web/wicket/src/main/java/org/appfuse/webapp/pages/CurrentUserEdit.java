package org.appfuse.webapp.pages;

import de.agilecoders.wicket.markup.html.bootstrap.common.NotificationMessage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;
import org.appfuse.model.User;
import org.appfuse.service.UserExistsException;
import org.wicketstuff.annotation.mount.MountPath;

import javax.servlet.http.HttpServletRequest;

/**
 * Page for editing current user.
 *
 * @author Marcin Zajączkowski, 2011-03-13
 */
@MountPath("userEdit")
@AuthorizeInstantiation({"ROLE_ADMIN", "ROLE_USER"})
public class CurrentUserEdit extends AbstractUserEdit {

    public CurrentUserEdit() {
        super(NO_RESPONSE_PAGE, new Model<User>());
    }

    @Override
    protected void onInitialize() {

        //TODO: MZA: Here or in constructor?
        //TODO: Quite odd - before super
        User user = getUserManager().getUserByUsername(getCurrentUserUsername());
        setUser(user);

        super.onInitialize();

    }

    private String getCurrentUserUsername() {
        String username = ((HttpServletRequest)getRequest().getContainerRequest()).getRemoteUser();
        if (username == null) {
            throw new IllegalStateException("Unable to get login of current user");
        }
        return username;
    }

    //TODO: MZA: Duplication with FromListUserEdit
    @Override
    protected void onSaveButtonSubmit() {
        User user = getUser();

        log.info("(current) onSubmit: {}", user);
        log.info("(current) onSubmit (address): {}", user.getAddress());

        try {
            getUserManager().saveUser(user);
            getSession().info(new NotificationMessage(new StringResourceModel(
                    "user.added", this, null, new Object[] {user.getUsername()})).hideAfter(Duration.seconds(5)));
            resolveAndSetResponsePage();
        } catch (UserExistsException e) {
            log.warn("User already exists", e);
            error(new StringResourceModel("errors.existing.user", this, null, new Object[] {
                    user.getUsername(), user.getEmail()}).getString());
        }
    }

    @Override
    protected void onDeleteButtonSubmit() {
        throw new IllegalStateException("Delete button should not be able to use on edit current user page");
    }

    @Override
    protected void onCancelButtonSubmit() {
        resolveAndSetResponsePage();
    }

    @Override
    protected boolean getDisplayRolesGroupVisibility() {
        return true;
    }

    @Override
    protected boolean getAccountSettingsGroupVisibility() {
        return false;
    }

    @Override
    protected boolean getButtonsGroupVisibility() {
        return true;
    }

    @Override
    protected boolean getDeleteButtonVisibility() {
        return false;
    }
}
