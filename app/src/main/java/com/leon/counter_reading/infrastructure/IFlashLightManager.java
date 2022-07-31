package com.leon.counter_reading.infrastructure;

public interface IFlashLightManager {
    boolean turnOn();

    boolean turnOff();

    boolean toggleFlash();
}
