package com.bespectacled.betterbiomeprovider.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.bespectacled.betterbiomeprovider.BetterBiomeProvider;
import com.bespectacled.betterbiomeprovider.biome.ClimateSampler;

import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.gen.BiomeSource;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin {
    @Unique private ClimateSampler climateSampler;
    @Unique private boolean betterBiomeProviderEnabled;
    
    @Shadow public double[] temperatureNoises;
    @Shadow public double[] rainfallNoises;
    
    @Inject(
        method = "<init>(Lnet/minecraft/level/Level;)V", 
        at = @At("TAIL")
    )
    private void injectClimateSampler(Level level, CallbackInfo info) {
        this.climateSampler = new ClimateSampler(level.getSeed());
        this.betterBiomeProviderEnabled = BetterBiomeProvider.isEnabled();
    }
    
    @Inject(
        method = "getTemperature",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectGetSkyTemperature(int x, int z, CallbackInfoReturnable<Double> info) {
        if (!this.betterBiomeProviderEnabled) return;
        
        info.setReturnValue(this.climateSampler.sampleSkyTemp(x, z));
    }
    
    @Inject(
        method = "getTemperatures",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectGetTemperatures(double[] temperatureNoises, int startX, int startZ, int sizeX, int sizeZ, CallbackInfoReturnable<double[]> info) {
        if (!this.betterBiomeProviderEnabled) return;
        
        if (temperatureNoises == null || temperatureNoises.length < sizeX * sizeZ) {
            temperatureNoises = new double[sizeX * sizeZ];
        }
        
        int ndx = 0;
        for (int x = startX; x < startX + sizeX; ++x) {
            for (int z = startZ; z < startZ + sizeZ; ++z) {
                double temp = this.climateSampler.sampleTemp(x, z);
                
                temperatureNoises[ndx] = temp;
                ndx++;
            }
        }
        
        info.setReturnValue(temperatureNoises);
    }
    
    
    @Inject(
        method = "getBiomes([Lnet/minecraft/level/biome/Biome;IIII)[Lnet/minecraft/level/biome/Biome;", 
        at = @At("HEAD"), 
        cancellable = true
    )
    private void injectGetBiomes(Biome[] biomeArray, int startX, int startZ, int sizeX, int sizeZ, CallbackInfoReturnable<Biome[]> info) {
        if (!this.betterBiomeProviderEnabled) return;
        
        if (biomeArray == null || biomeArray.length < sizeX * sizeZ) {
            biomeArray = new Biome[sizeX * sizeZ];
        }
        
        if (this.temperatureNoises == null || this.temperatureNoises.length < sizeX * sizeZ) {
            this.temperatureNoises = new double[sizeX * sizeZ];
        }
        
        if (this.rainfallNoises == null || this.rainfallNoises.length < sizeX * sizeZ) {
            this.rainfallNoises = new double[sizeX * sizeZ];
        }
        
        int ndx = 0;
        for (int x = startX; x < startX + sizeX; ++x) {
            for (int z = startZ; z < startZ + sizeZ; ++z) {
                double temp = this.climateSampler.sampleTemp(x, z);
                double rain = this.climateSampler.sampleRain(x, z);
                
                this.temperatureNoises[ndx] = temp;
                this.rainfallNoises[ndx] = rain;
                biomeArray[ndx] = Biome.getBiome(temp, rain);
                
                ndx++;
            }
        }
        
        info.setReturnValue(biomeArray);
    }
}
