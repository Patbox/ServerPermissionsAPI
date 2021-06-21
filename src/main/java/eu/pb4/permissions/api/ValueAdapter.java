package eu.pb4.permissions.api;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Locale;

@SuppressWarnings({"unused"})
public interface ValueAdapter<T> {
    int sort(T base, T compared);
    @Nullable T create(String string);

    ValueAdapter<Integer> INTEGER = new ValueAdapter<>() {
        @Override
        public int sort(Integer base, Integer compared) {
            return Integer.compare(compared, base);
        }

        @Override
        public Integer create(String string) {
            try {
                return Integer.parseInt(string);
            } catch (Exception e) {
                return null;
            }
        }
    };

    ValueAdapter<Double> DOUBLE = new ValueAdapter<>() {
        @Override
        public int sort(Double base, Double compared) {
            return Double.compare(compared, base);
        }

        @Override
        public Double create(String string) {
            try {
                return Double.parseDouble(string);
            } catch (Exception e) {
                return null;
            }
        }
    };

    ValueAdapter<Duration> DURATION = new ValueAdapter<>() {
        @Override
        public int sort(Duration base, Duration compared) {
            return Long.compare(compared.getSeconds(), base.getSeconds());
        }

        @Override
        public Duration create(String string) {
            try {
                string = string.toLowerCase(Locale.ROOT);
                try {
                    return Duration.ofSeconds(Long.parseLong(string));
                } catch (NumberFormatException e) {
                    String[] times = string.replaceAll("([a-z]+)", "$1|").split("\\|");
                    long time = 0;
                    for (String x : times) {
                        String numberOnly = x.replaceAll("[a-z]", "");
                        String suffixOnly = x.replaceAll("[^a-z]", "");

                        time += switch (suffixOnly) {
                            case "c" -> Double.parseDouble(numberOnly) * 3155692600L;
                            case "y" -> Double.parseDouble(numberOnly) * 31556926;
                            case "mo" -> Double.parseDouble(numberOnly) * 2592000;
                            case "d" -> Double.parseDouble(numberOnly) * 86400;
                            case "h" -> Double.parseDouble(numberOnly) * 3600;
                            case "m" -> Double.parseDouble(numberOnly) * 60;
                            default -> Double.parseDouble(numberOnly);
                        };
                    }
                    return Duration.ofSeconds(time);
                }
            } catch (Exception e) {
                return null;
            }
        }
    };
}
