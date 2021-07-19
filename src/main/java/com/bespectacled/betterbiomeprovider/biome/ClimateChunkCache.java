package com.bespectacled.betterbiomeprovider.biome;

import java.util.LinkedHashMap;
import java.util.Map;
import com.bespectacled.betterbiomeprovider.util.TriFunction;

public class ClimateChunkCache<T> {
    private final TriFunction<Integer, Integer, ClimateSampler, T> chunkFunc;
    private final Map<Long, T> chunkMap;
    
    @SuppressWarnings("serial")
    public ClimateChunkCache(int capacity, boolean evictOldChunks, TriFunction<Integer, Integer, ClimateSampler, T> chunkFunc) {        
        this.chunkFunc = chunkFunc;
        this.chunkMap = new LinkedHashMap<Long, T>(capacity) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Long, T> eldest) {
                return evictOldChunks && size() > capacity;
            }
        };
    }
    
    public ClimateChunkCache(int capacity, TriFunction<Integer, Integer, ClimateSampler, T> chunkFunc) {
        this(capacity, true, chunkFunc);
    }
    
    public void clear() {
        this.chunkMap.clear();
    }
    
    public T get(int chunkX, int chunkZ, ClimateSampler climateSampler) {
        long key = (long)chunkX & 0xFFFFFFFFL | ((long)chunkZ & 0xFFFFFFFFL) << 32;
        
        T chunk = this.chunkMap.get(key);
        
        if (chunk == null) {
            chunk = this.chunkFunc.apply(chunkX, chunkZ, climateSampler);
            
            this.chunkMap.put(key, chunk);
        }
        
        return chunk;
    }
}
