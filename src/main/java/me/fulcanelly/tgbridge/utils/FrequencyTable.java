package me.fulcanelly.tgbridge.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data @AllArgsConstructor @Builder @NoArgsConstructor @With 
public class FrequencyTable<T> {
    
    public int count = 1;
    public T value;
    
}
