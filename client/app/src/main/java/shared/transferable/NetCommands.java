package shared.transferable;

public enum NetCommands implements Transferable {
    register,
    registrationOk,
    registrationDenied,
    internalClientError;
}