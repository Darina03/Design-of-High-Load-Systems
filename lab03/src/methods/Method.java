package methods;

public interface Method {

    void run(int userId, int threads, int incrementsPerThread) throws Exception;

    String getName();
}