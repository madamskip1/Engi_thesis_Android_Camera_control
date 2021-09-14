package org.pw.engithesis.androidcameracontrol.interfaces;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable {
    protected Set<Observer> observers = new HashSet<>();

    protected void notifyUpdate() {
        observers.forEach(Observer::update);
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }
}
