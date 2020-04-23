package shared.transferable;

public enum NetCommands implements Transferable {
    registerUser,
    registrationOk,
    registrationDenied,
    registerNewGroup,
    newGroupOk,
    newGroupDenied,
    addUserToGroup,
    removeUserFromGroup,
    updateGroup,
    addNewChore,
    newChoreOk,
    addNewReward,
    newRewardOk,
    claimReward,
    internalClientError,
    connectionStatus,
    notConnected,
    connected,
    searchForUser,
    userExists,
    userDoesNotExist,
    connectionFailed,
    requestUserGroups,
    userHasNoGroup,
    userHasGroupUpdate,
    login,
    clientInternalGroupUpdate
    ;
}
