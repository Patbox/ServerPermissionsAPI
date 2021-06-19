package eu.pb4.permissions.api;

import org.jetbrains.annotations.Nullable;

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
}
