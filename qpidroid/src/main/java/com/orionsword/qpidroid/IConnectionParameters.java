package com.orionsword.qpidroid;

/**
 * Business server connection parameters wrapper interface.
 * @author Pablo.Francisco.Pérez.Hidalgo
 * @since 2014-03-24
 */
public interface IConnectionParameters {
    public String getStringParameter(String name);
    public Integer getIntegerParameter(String name);
}

