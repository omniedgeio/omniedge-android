package io.omniedge;

public interface ILogger {
    void d(String str, Throwable th);

    void e(String str, Throwable th);

    void i(String str, Throwable th);

    void v(String str, Throwable th);

    void w(String str, Throwable th);
}