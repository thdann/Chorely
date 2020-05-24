package shared.transferable;

/**
 * This enum is used to differentiate between different types of Message.
 */
public enum NetCommands implements Transferable {
    registerUser,
    registrationOk,
    registrationDenied,
    registerNewGroup,
    newGroupOk,
    newGroupDenied,
    deleteGroup,
    updateGroup,
    addNewChore,
    addNewReward,
    connected,
    searchForUser,
    userExists,
    userDoesNotExist,
    connectionFailed,
    login,
    loginOk,
    loginDenied,
    logout,
    clientInternalGroupUpdate
    ;
}
