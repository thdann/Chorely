package com.mau.chorely.model.transferrable;

public enum NetCommands implements Transferable {
    register,
    registrationOk,
    registrationDenied,
    internalClientError;
}
