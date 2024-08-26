package me.fulcanelly.tgbridge.utils.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data @AllArgsConstructor @NoArgsConstructor @With 
public class FrequencyTable<T> {
    
    public int count = 1;
    public T value;
    
}
