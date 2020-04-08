package controller;

import shared.transferable.Transferable;

import java.util.ArrayList;

/**
 * Interface ClientListener
 * version 1.0 2020-03-23
 * @autor Angelica Asplund, Emma Svensson and Theresa Dannberg
 */

public interface ClientListener {

    void sendList(ArrayList<Transferable> message);

}
