package me.fulcanelly.tgbridge.utils.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;


@Data
public class DuplicateLessList<T> {
    
    List<FrequencyTable<T>> list = new ArrayList<>();

    public boolean add(T value) {
        return list.add(
            new FrequencyTable<T>()
                .withValue(value)
        );
    }

    protected int countSignleRepeats(int shift) {
        var freqTable = list.get(shift);
        int count = 0;

        for (int i = shift + 1; i < list.size(); i++) {
            if (isSameValues(list.get(i), freqTable)) count++; else break;
        }
        
        return count;
    }

    static <T> boolean isSameValues(FrequencyTable<T> a, FrequencyTable<T> b) {
        return a.getValue().equals(b.getValue());
    }

    protected static <T> boolean isSameValues(List<FrequencyTable<T>> a, List<FrequencyTable<T>> b) {
        for (int i = 0; i < a.size(); i++) {
            if (!isSameValues(a.get(i), b.get(i))) {
                return false;
            };
        }
        return true;
    } 

    protected static <T> boolean isSameCount(List<FrequencyTable<T>> values) {
        int first = values.get(0).getCount();
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i).getCount() != first) {
                return false;
            };
        }
        return true;
    }

    protected int countSizedDuplicates(int shift, int size) {
        var base = list.subList(shift, shift + size);
        if (!isSameCount(base)) {
            return 0;
        }

        int count = 0;
        for (int i = shift + size; i + size - 1 < list.size(); i+= size) {

            var slice = list.subList(i, i + size);

            if (isSameValues(base, slice) && isSameCount(slice)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }


    public static<L> List<L> excludeRange(List<L> list, int start, int finish) {
        return Stream.of(
            list.subList(0, start), 
            list.subList(finish, list.size())
        ).flatMap(it -> it.stream())
            .collect(Collectors.toList());
    }

    public void removeRepeatsByOne() {
        for (int i = 0; i < list.size(); i++) {
            var count = countSignleRepeats(i);
            if (count > 0) {
                list.get(i).setCount(count + list.get(i).getCount());
                list = excludeRange(list, i + 1, i + count + 1 );
            }
        }
    }

    void updateCountForAllBy(List<FrequencyTable<T>> values, int inc) {
        values.forEach(freq -> freq.setCount(freq.getCount() + inc));
    }

    int tryReduceAt(int shift) {
        for (int step = 1; step <= (list.size() - shift) / 2; step++) {
            var count = countSizedDuplicates(shift, step);
           
            if (count > 0) {
                var index = shift + step;
                updateCountForAllBy(list.subList(shift, index), count);
                list = excludeRange(list, index, index + count * step);

                return step;
            }
        }

        return 1;
    }

    public void removeDuplicatesByMaxStep() {
        for (int i = 0; i < list.size();) {
            i += tryReduceAt(i);
        }
    }

}
