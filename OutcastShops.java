package me.illusion.outcastshops;

import me.illusion.outcastcore.OutcastCore;
import me.illusion.outcastshops.Commands.Commands;
import me.illusion.outcastshops.Util.Communication.LogMe;
import me.illusion.outcastshops.Util.Config.ConfigState;
import me.illusion.outcastshops.Util.Config.CreateConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public final class OutcastShops extends JavaPlugin {
    static OutcastShops instance = null;

    public OutcastCore coreAPI = null;
    public CreateConfig config, shops;

    @Override
    public void onEnable() {
        instance = this;
        coreAPI = (OutcastCore) Bukkit.getPluginManager().getPlugin("OutcastCore");

        if (coreAPI == null) {
            new LogMe("Unable to find OutcastCore!").Error();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new LogMe("Shops starting...").Warning();

        config = new CreateConfig("config.yml", "Outcast/Shops");
        shops = new CreateConfig("shops.yml", "Outcast/Shops");

        new ConfigState(config).configDefaults();
        new ConfigState(shops).loadConfig();

        setupCommands();
        setupListeners();
    }

    @Override
    public void onDisable() {
        new LogMe("Shops stopping...").Warning();

        new ConfigState(shops).saveConfig();
    }

    public static OutcastShops getInstance() {
        return instance;
    }

    private void setupCommands() {
        getCommand("shops").setExecutor(new Commands());
    }

    private void setupListeners() {
        String packageName = getClass().getPackage().getName();
        for (Class<?> cl : new Reflections(packageName + ".Events").getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) cl.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
