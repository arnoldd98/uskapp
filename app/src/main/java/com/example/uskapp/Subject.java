package com.example.uskapp;

public interface Subject {
    void register(Observer o);
    void unregister(Observer o);
    void notifyObservers();

}
