package shared.transferable;

public enum NetCommands implements Transferable {
    register,
    registrationOk,
    registrationDenied,
    internalClientError,
    connectionStatus,
    notConnected,
    connected,
    requestUserGroups,
    userHasNoGroup,
    userHasGroupUpdate;
}
