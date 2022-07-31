package com.leon.counter_reading.infrastructure;

import retrofit2.Response;

public interface ICallback<T> {
    void execute(Response<T> response);
}
