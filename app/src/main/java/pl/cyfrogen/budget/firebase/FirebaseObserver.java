package pl.cyfrogen.budget.firebase;

public interface FirebaseObserver<T> {
    void onChanged(T t);
}
