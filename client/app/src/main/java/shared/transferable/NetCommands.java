package shared.transferable;

public enum NetCommands implements Transferable {
    register,
    registrationOk,
    registrationDenied,
    internalClientError,
    connectionStatus,
    notConnected,
    connected,
    connectionFailed,
    requestUserGroups,
    userHasNoGroup,
    userHasGroupUpdate;
}
