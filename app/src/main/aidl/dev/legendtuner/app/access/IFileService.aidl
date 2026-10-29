package dev.legendtuner.app.access;

interface IFileService {
    boolean exists(String path);
    byte[] read(String path);
    void write(String path, in byte[] data);
    String stat(String path);
    boolean forceStopGame(String packageName);
}
