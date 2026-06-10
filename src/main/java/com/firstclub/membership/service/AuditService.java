package com.firstclub.membership.service;

public interface AuditService<T> {

    void record(T entry);

}
