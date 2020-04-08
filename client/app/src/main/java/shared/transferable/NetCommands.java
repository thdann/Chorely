package shared.transferable;

public enum NetCommands implements Transferable {
    register,
    registrationOk,
    registrationDenied,
    registerNewGroup,
    newGroupOk,
    addUserToGroup,
    internalClientError,
    connectionStatus,
    notConnected,
    connected,
    requestUserGroups,
    userHasNoGroup,
    userHasGroupUpdate;
}
