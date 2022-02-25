public class CreateConfig {
    private String configName;
    private File file = null;
    private FileConfiguration config = null;

    public CreateConfig(String configName, String folderName)
    {
        this.configName = configName;

        if (!configName.contains(".yml"))
        {
            configName = configName + ".yml";
        }

        this.file = new File("plugins/" + folderName, configName);
        this.config = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists())
        {
            this.config.options().copyDefaults(true);

            try
            {
                this.config.save(this.file);
            }
            catch (IOException var5)
            {
                var5.printStackTrace();
            }
        }
    }

    public void save() {
        try
        {
            this.config.save(this.file);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }

    public void reload()
    {
        try
        {
            this.config.load(this.file);
        }
        catch (IOException | InvalidConfigurationException ex)
        {
            ex.printStackTrace();
        }
    }

    public void setHeader(String text)
    {
        this.config.options().header(text);
        save();
    }

    public void setSection(String location)
    {
        this.config.createSection(location);
        save();
    }

    public void set(String location, Object value)
    {
        this.config.set(location, value);
        save();
    }

    public void setDefault(String location, Object value)
    {
        if (!this.config.contains(location))
            this.config.set(location, value);
        save();
    }

    public void setDefaultSection(String location)
    {
        if (!this.config.contains(location))
            this.config.createSection(location);
        save();
    }

    public Object getItem(String location)
    {
        return this.config.get(location);
    }

    public List<?> getItemList(String location)
    {
        return this.config.getList(location);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void saveMap(String location, Map<?, ?> map)
    {
        if (map.isEmpty())
            return;

        for (Object entry : map.entrySet())
        {
            Map.Entry<Object, Object> entryObject = (Map.Entry<Object, Object>) entry;
            config.set(location + "." + entryObject.getKey(), entryObject.getValue());
        }
        save();
    }

    public void loadMap(String location, Map<String, String> map) {
        if (config.getConfigurationSection(location) == null)
            return;

        config.getConfigurationSection(location).getKeys(false).forEach(key -> {
            // convert the value to a string
            String list = getItem(location + "." + key).toString();
            // put them back into the map
            map.put(key, list);
        });
    }

    public void loadMapInteger(String location, Map<String, Integer> map) {
        if (config.getConfigurationSection(location) == null)
            return;

        config.getConfigurationSection(location).getKeys(false).forEach(key -> {
            // convert the value to a integer
            Integer list = Integer.parseInt(getItem(location + "." + key).toString());
            // put them back into the map
            map.put(key, list);
        });
    }

    public void loadMapLong(String location, Map<String, Long> map) {
        if (config.getConfigurationSection(location) == null)
            return;

        config.getConfigurationSection(location).getKeys(false).forEach(key -> {
            // convert the value to a long
            Long list = Long.parseLong(getItem(location + "." + key).toString());
            // put them back into the map
            map.put(key, list);
        });
    }
}
