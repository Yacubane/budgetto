package pl.cyfrogen.budget.firebase;

import com.google.firebase.database.DatabaseError;

public class FirebaseElement<T> {
    private T element;
    private DatabaseError databaseError;

    public FirebaseElement(T element) {
        this.element = element;
    }
    public FirebaseElement(DatabaseError databaseError) {
        this.databaseError = databaseError;
    }

    public T getElement() {
        return element;
    }

    public boolean hasNoError() {
        return element != null;
    }
}
