package jotato.quantumflux;

import net.minecraftforge.common.config.Configuration;

public class ConfigMan
{
    private static Configuration config;

    public static int incinerator_output;
    public static int incinerator_buffer;
    public static int incinerator_burnTime;

    public static int quibitCluster_baseStorage;
    public static int quibitCluster_baseTransferRate;
    public static int quibitCluster_multiplier;

    public static int zpe_maxPowerGen;

    public static int redfluxField_buffer;
    public static int rfExciter_output;
    public static int rfExciter_maxUpgrades;
    public static int quibitcell_output;

    public static boolean isDebug;

    public static int imaginaryTime_energyRequirement;
    public static int imaginaryTime_range;
    public static int imaginaryTime_chargeRate;

    public static boolean oreGenerationEnabled;

    public static void init(Configuration configuration)
    {
        config = configuration;
        config.load();
        hydrateConifg();
        config.save();
    }

    public static void hydrateConifg()
    {
        incinerator_output = config.getInt("output", "entropyAccelerator", 2, 2, 8, "The RF generated per tick");
        incinerator_buffer = config.getInt("buffer", "entropyAccelerator", 2000, 100, 10000, "The amount of energy that can be stored in the block");
        incinerator_burnTime = config.getInt("burnTime", "entropyAccelerator", 20, 10, 1000, "How many ticks an item will burn");

        quibitCluster_baseStorage = config.getInt("baseStorage", "quibitCluster", 500000, 100000, 1000000, "The base amount of RF the Quibit Clusters can hold");
        quibitCluster_baseTransferRate = config.getInt("baseTransferRate", "quibitCluster", 100, 50, 1000, "The base RF/tick the Quibit Clusters can do");
        quibitCluster_multiplier = config.getInt("multiplier", "quibitCluster", 5, 4, 6, "The multiplier between each level of the clusters");

        zpe_maxPowerGen = config.getInt("powerGen", "zeroPointExtractor", 150, 32, 512, "The maximum amount of rf/t it can generate");

        redfluxField_buffer = config.getInt("buffer", "redfluxField", 50000, 50000, 1000000, "The internal storage of each player's field");
        rfExciter_output = config.getInt("rfExciter_baseOutput", "redfluxField", 100, 100, 1000, "The output without any upgrades");
        quibitcell_output = config.getInt("quibitCell_output", "redfluxField", 100, 100, 500, "How much rf/tick the quibit cell can charge per item");
        rfExciter_maxUpgrades = config.getInt("rfExciter_maxUpgrades", "redfluxField", 640, 100, 1000, "How many upgrades can be applied to an Exciter");

        isDebug = config.getBoolean("debugMode", "misc", false , "Developer only");

        imaginaryTime_energyRequirement =config.getInt("energyRequirement", "imaginaryTime", 800, 50, 1000, "How much energy is used per work cycle. The higher the number the more RF must be pumped in before work will be done");
        imaginaryTime_range=config.getInt("range", "imaginaryTime", 2, 2, 4, "The area of effect along the X&Z. Y is always 2");
        imaginaryTime_chargeRate=config.getInt("chargeRate", "imaginaryTime", 250,25,1000,"How much RF/T it can accept. Setting this lower than the energyRequirement means it won't work every tick");

        oreGenerationEnabled = config.getBoolean("enabled", "oreGeneration", true , "Can be used to disable ore generation");
    }
}
