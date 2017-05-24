package test.actual;

/**
 * Created by shrey.garg on 02/05/17.
 */
public class Wrapper<T> {
    T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Wrapper{");
        sb.append("response=").append(response);
        sb.append('}');
        return sb.toString();
    }
}
