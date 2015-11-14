package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssIOUserOrGroupIdentifier implements IssFlag {
    Administrator("Admins", "common.user_or_group_identifier.admins"),
    AuthenticatedUser("AuthUsers", "common.user_or_group_identifier.authusers"),
    Everyone("Everyone", "common.user_or_group_identifier.everyone"),
    PowerUser("PowerUsers", "common.user_or_group_identifier.powerusers"),
    System("System", "common.user_or_group_identifier.system"),
    User("Users", "common.user_or_group_identifier.users"),
    ;

    public static IssIOUserOrGroupIdentifier fromId(final String id) {
        return IssFlag.findById(id, IssIOUserOrGroupIdentifier.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssIOUserOrGroupIdentifier(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssIOUserOrGroupIdentifier(String id, String descriptionKey, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.deprecated = deprecated;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
