package controller;

import shared.transferable.Message;

/**
 * Interface ClientListener
 * version 1.0 2020-03-23
 *
 * @author Angelica Asplund, Emma Svensson and Theresa Dannberg
 */
public interface ClientListener {
    void sendMessage(Message message);
}
