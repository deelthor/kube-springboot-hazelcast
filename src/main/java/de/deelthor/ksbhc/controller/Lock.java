package de.deelthor.ksbhc.controller;

public class Lock {

    private String uuid;
    private String ip;

    public Lock() {

    }

    public Lock(String uuid, String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
