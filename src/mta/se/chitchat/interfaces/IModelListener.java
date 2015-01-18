package mta.se.chitchat.interfaces;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut
 * </p> Software Engineering Project
 * </p>  The interface made public to the controller (observer pattern) - goes both
 * ways
 */
public interface IModelListener {

    /**
     * Notifies the listeners that an update occurred in the model
     */
    public void onUpdate();
}
