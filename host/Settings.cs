using System;
using System.IO;
using System.Text.Json;

public static class Settings{
    public struct AppSettings{
        public string MqttServer { get; set; }
        public string Nickname { get; set; }
    }

    private static readonly string SettingsFilePath = "appsettings.json";
    private static void UpdateSettings(AppSettings settings){
        var options = new JsonSerializerOptions { WriteIndented = true };
        string currentDirectory = Directory.GetCurrentDirectory();
        string jsonString = JsonSerializer.Serialize(settings, options);

        Console.WriteLine($"Update on {Path.Join(currentDirectory, SettingsFilePath)}");

        File.WriteAllText(Path.Join(currentDirectory, SettingsFilePath), jsonString);
    }
    public static void SaveMqttNameSettings(string Server){
        AppSettings settings = LoadSettings();
        settings.MqttServer = Server;
        UpdateSettings(settings);
    }
    public static void SaveNickNameSettings(string Server){
        AppSettings settings = LoadSettings();
        settings.Nickname = Server;
        UpdateSettings(settings);
    }

    public static AppSettings LoadSettings(){
        AppSettings tmpSettings = new AppSettings();
        string currentDirectory = Directory.GetCurrentDirectory();
        if (File.Exists(Path.Join(currentDirectory, SettingsFilePath))){
            string jsonString = File.ReadAllText(Path.Join(currentDirectory, SettingsFilePath));
            tmpSettings = JsonSerializer.Deserialize<AppSettings>(jsonString);
        }
        return tmpSettings;
    }
}