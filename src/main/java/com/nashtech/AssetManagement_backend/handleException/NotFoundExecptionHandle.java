package com.nashtech.AssetManagement_backend.handleException;

public class NotFoundExecptionHandle extends RuntimeException {
    public NotFoundExecptionHandle(String msg) {
        super(msg);
    }
}
