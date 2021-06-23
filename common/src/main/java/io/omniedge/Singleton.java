package io.omniedge;

public abstract class Singleton<T> {
    private volatile T mInstance;

    protected abstract T create(Object... objArr);

    public final T get(Object... params) {
        if (this.mInstance == null) {
            synchronized (this) {
                if (this.mInstance == null) {
                    this.mInstance = create(params);
                }
            }
        }
        return this.mInstance;
    }
}