package daryna.service;

public final class WriteStrategyFactory {
    private WriteStrategyFactory() {}

    public static WriteStrategy create(String mode) {
        return switch (mode.toLowerCase()) {
            case "majority" -> new MajorityWriteStrategy();
            case "w3_infinite" -> new W3InfiniteWriteStrategy();
            case "w3_timeout" -> new W3TimeoutWriteStrategy(5000);
            default -> new DefaultWriteStrategy();
        };
    }
}
