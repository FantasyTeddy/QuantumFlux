package jotato.quantumflux;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationManager
{
    private static Configuration config;
    
    public static int incinerator_output;
    public static int incinerator_buffer;
    public static int incinerator_burnTime;
    
    public static int effIncinerator_burnTime;
    public static int effIncinerator_buffer;
    public static int effIncinerator_output;

    public static void init(Configuration configuration)
    {
        config = configuration;
        config.load();
        hydrateConifg();
        config.save();
    }

    public static void hydrateConifg()
    {
        incinerator_output = config.getInt("output", "incinerator", 10, 1, 100, "The RF generated per tick");
        incinerator_buffer = config.getInt("buffer", "incinerator", 100000, 10000, 1000000, "The amount of energy that can be stored in the block");
        incinerator_burnTime = config.getInt("burnTime", "incinerator", 800, 20, 8000, "How many ticks an item will burn");
        
        effIncinerator_output = config.getInt("output", "efficientIncinerator", 100, 1, 100, "The RF generated per tick");
        effIncinerator_buffer = config.getInt("buffer", "efficientIncinerator", 1000000, 10000, 10000000, "The amount of energy that can be stored in the block");
        effIncinerator_burnTime = config.getInt("burnTime", "efficientIncinerator", 1000, 20, 8000, "How many ticks an item will burn");
        
    }
}
