package it.pagopa.selfcare.external_interceptor.connector.model.institution;

import it.pagopa.selfcare.external_interceptor.connector.model.user.RelationshipState;

public enum NotificationType {
    ADD_INSTITUTE(null),
    UPDATE_INSTITUTION(null),
    ACTIVE_USER(RelationshipState.ACTIVE),
    SUSPEND_USER(RelationshipState.SUSPENDED),
    DELETE_USER(RelationshipState.DELETED);

    private RelationshipState relationshipState;
    private NotificationType(RelationshipState relationshipState){
        this.relationshipState = relationshipState;
    }

    public static NotificationType getNotificationTypeFromRelationshipState(RelationshipState relationshipState) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.relationshipState == relationshipState) {
                return notificationType;
            }
        }
        return null; // Return null if no matching NotificationType is found
    }
}
